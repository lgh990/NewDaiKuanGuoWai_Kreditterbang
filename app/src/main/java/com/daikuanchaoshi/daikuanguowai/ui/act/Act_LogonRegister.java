package com.daikuanchaoshi.daikuanguowai.ui.act;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daikuanchaoshi.daikuanguowai.R;
import com.daikuanchaoshi.daikuanguowai.commt.BaseAct;
import com.daikuanchaoshi.daikuanguowai.commt.MyApplication;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.codeLoginBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.getSmsVerifyBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.home.act.Act_AgreeWeb;
import com.daikuanchaoshi.daikuanguowai.ui.http.HttpHelper;
import com.daikuanchaoshi.daikuanguowai.ui.utils.CountDownTimerUtils;
import com.daikuanchaoshi.daikuanguowai.ui.utils.MyPreferencesManager;
import com.google.gson.Gson;
import com.lykj.aextreme.afinal.utils.ACache;
import com.lykj.aextreme.afinal.utils.MyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Act_LogonRegister extends BaseAct {
    @BindView(R.id.back_next)
    ImageView backNext;
    @BindView(R.id.user_phone)
    EditText userPhone;
    @BindView(R.id.user_code)
    EditText userCode;
    @BindView(R.id.send_code)
    TextView sendCode;
    @BindView(R.id.login_sure)
    Button loginSure;
    @BindView(R.id.service)
    TextView service;
    @BindView(R.id.choose_serve)
    ImageView chooseServe;
    private ACache aCache;
    private CountDownTimer utils;
    private boolean isChoose = true;//判断是否选择了服务

    @Override
    public int initLayoutId() {
        return R.layout.act__logon_register;
    }


    @Override
    public void initView() {
        hideHeader();
        ButterKnife.bind(this);

        aCache = ACache.get(this);
        if (MyPreferencesManager.getLong("date", 0) != 0) {
            if (null == utils) {
                if (System.currentTimeMillis() - MyPreferencesManager.getLong("date", 0) - 60000 < 0) {
                    utils = new CountDownTimer(60000 - (System.currentTimeMillis() - MyPreferencesManager.getLong("date", 0)), 1000) {
                        @Override
                        public void onTick(long l) {
                            sendCode.setClickable(false); //设置不可点击
                            sendCode.setText(l / 1000 + "s");  //设置倒计时时间
                            sendCode.setTextColor(Color.parseColor("#D1D1D1")); //设置按钮为灰色，这时是不能点击的
                            /**
                             * 超链接 URLSpan
                             * 文字背景颜色 BackgroundColorSpan
                             * 文字颜色 ForegroundColorSpan
                             * 字体大小 AbsoluteSizeSpan
                             * 粗体、斜体 StyleSpan
                             * 删除线 StrikethroughSpan
                             * 下划线 UnderlineSpan
                             * 图片 ImageSpan
                             * http://blog.csdn.net/ah200614435/article/details/7914459
                             */
                            SpannableString spannableString = new SpannableString(sendCode.getText().toString());  //获取按钮上的文字
                            ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#D1D1D1"));
                            /**
                             * public void setSpan(Object what, int start, int end, int flags) {
                             * 主要是start跟end，start是起始位置,无论中英文，都算一个。
                             * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
                             */
                            spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            sendCode.setText(spannableString);
                        }

                        @Override
                        public void onFinish() {
                            MyPreferencesManager.putLong("date", 0);
                            sendCode.setText("Dapatkan Kode");
                            sendCode.setClickable(true);//重新获得点击
                            sendCode.setTextColor(Color.parseColor("#FFD606"));
                        }
                    };
                    utils.start();
                } else {
                    MyPreferencesManager.putLong("date", 0);
                }
            }
        }
    }

    protected void hideHeader() {
        if (toolbar != null) toolbar.setVisibility(View.GONE);
    }
    /**
     * 验证码登录
     */
    public void codeLogin(String address) {
        loding.show();
        HttpHelper.codeLogin(this, userPhone.getText().toString().trim(), userCode.getText().toString().trim(), address, getSystemModel(), MyApplication.adid, new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                codeLoginBean getSmsVerifyBean = gson.fromJson(succeed, codeLoginBean.class);
                if (getSmsVerifyBean.getStatus() == 1) {
//                    AdjustEvent adjustEvent = new AdjustEvent(Utils.getId(Act_LogonRegister.this) + "");
//                    Adjust.trackEvent(adjustEvent);
                    aCache.put("lognbean", succeed);
                    aCache.put("phone", userPhone.getText().toString().trim());
                    MyApplication.setLognBean(getSmsVerifyBean.getData());
                    startAct(Act_Main.class);
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                loding.dismiss();
                MyToast.show(context, error);
            }
        });
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 验证码
     */
    public void getCertificateByOldPhone() {
        if (userPhone.getText().toString().length() < 9) {
//            MyToast.show(this, "请输入11位手机号码！");
            MyToast.show(this, "Masukkan nomor ponsel yang benar!");
            return;
        }
        loding.show();
        HttpHelper.getSmsVerify(this, userPhone.getText().toString().trim(), "2", new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                getSmsVerifyBean getSmsVerifyBean = gson.fromJson(succeed, com.daikuanchaoshi.daikuanguowai.ui.act.bean.getSmsVerifyBean.class);
                if (getSmsVerifyBean.getStatus() == 1) {
                    MyToast.show(Act_LogonRegister.this, "Kode verifikasi sukses dikirim！");
                    utils = new CountDownTimerUtils(sendCode, 60000, 1000);
                    utils.start();
                    MyPreferencesManager.putLong("date", System.currentTimeMillis());
                }
            }

            @Override
            public void onError(String error) {
                loding.dismiss();
                MyToast.show(context, error);
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != utils) {
            utils.cancel();
            utils = null;
        }
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onNoInterNet() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        userPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 8 && userCode.getText().toString().trim().length() == 6 && isChoose) {
                    loginSure.setBackgroundResource(R.drawable.button_sure);
                } else {
                    loginSure.setBackgroundResource(R.drawable.button_unsure);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        userCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6 && userPhone.getText().toString().trim().length() > 8 && isChoose) {
                    loginSure.setBackgroundResource(R.drawable.button_sure);
                } else {
                    loginSure.setBackgroundResource(R.drawable.button_unsure);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @OnClick({R.id.back_next, R.id.login_sure, R.id.service, R.id.send_code, R.id.choose_serve})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_next:
                finish();
                break;
            case R.id.login_sure:
                if (userPhone.getText().toString().length() < 9) {
                    MyToast.show(this, "Masukkan nomor ponsel yang benar!");
                    return;
                }
                if (userCode.getText().toString().length() != 6) {
                    MyToast.show(this, "Masukkan kode verifikasi yang benar!");
                    return;
                }
                if (!isChoose) {
                    MyToast.show(this, "Silakan pilih layanan!");
                    return;
                }
                codeLogin(MyApplication.addr);
                break;
            case R.id.service:
                startAct(Act_AgreeWeb.class);
                break;
            case R.id.send_code:
                getCertificateByOldPhone();
                break;
            case R.id.choose_serve:
                if (isChoose) {
                    isChoose = false;
                    chooseServe.setImageResource(R.mipmap.gocash_land_choice);
                } else {
                    isChoose = true;
                    chooseServe.setImageResource(R.mipmap.gocash_land_choice_ed);
                }
                if (userCode.getText().toString().trim().length() == 6 && userPhone.getText().toString().trim().length() > 8 && isChoose) {
                    loginSure.setBackgroundResource(R.drawable.button_sure);
                } else {
                    loginSure.setBackgroundResource(R.drawable.button_unsure);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}
