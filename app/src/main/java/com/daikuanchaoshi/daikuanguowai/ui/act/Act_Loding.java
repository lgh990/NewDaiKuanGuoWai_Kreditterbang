package com.daikuanchaoshi.daikuanguowai.ui.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.daikuanchaoshi.daikuanguowai.R;
import com.daikuanchaoshi.daikuanguowai.commt.BaseAct;
import com.daikuanchaoshi.daikuanguowai.commt.MyApplication;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.checkforupdateBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.codeLoginBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.publicaddressBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.JsonDataBean;
import com.daikuanchaoshi.daikuanguowai.ui.http.HttpHelper;
import com.daikuanchaoshi.daikuanguowai.ui.utils.AdvertisingIdClient;
import com.daikuanchaoshi.daikuanguowai.ui.utils.DeviceIdUtil;
import com.daikuanchaoshi.daikuanguowai.ui.utils.I18NUtils;
import com.daikuanchaoshi.daikuanguowai.ui.utils.LocationTool;
import com.daikuanchaoshi.daikuanguowai.ui.utils.permission.RxPermissions;
import com.google.gson.Gson;
import com.lykj.aextreme.afinal.utils.ACache;
import com.lykj.aextreme.afinal.utils.Debug;
import com.lykj.aextreme.afinal.utils.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 加载页
 */
public class Act_Loding extends BaseAct implements LocationTool.onBackLoCtion {
    @BindView(R.id.until_time)
    TextView untilTime;
    private CountDownTimer utils;

    @Override
    public int initLayoutId() {
        return R.layout.act_loding;
    }

    private ACache aCache;
    RxPermissions rxPermissions;

    @Override
    public void initView() {
        hideHeader();
    }

    private Handler updateHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 14) {
                checkforupdate();
            }
        }
    };
    private LocationTool locationTool;

    @Override
    public void initData() {
        hideHeader();
        ButterKnife.bind(this);
        locationTool = new LocationTool(Act_Loding.this, Act_Loding.this);
        aCache = ACache.get(this);
        try {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        MyApplication.adid = AdvertisingIdClient.getGoogleAdId(getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        VERSION = Build.VERSION.SDK_INT;
        if (VERSION < 24) {
            if (VERSION >= 23) {
                rxPermissions2();
            } else {
                locationTool.startLocation();
                initDataView();
            }
        } else {
            rxPermissions1();
        }
    }

    private int VERSION;

    public void rxPermissions1() {
        rxPermissions = new RxPermissions(Act_Loding.this);
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        try {
                            locationTool.startLocation();
                        } catch (Exception e) {

                        }
                        initDataView();
                    } else {
                        initDataView();
                        Toast.makeText(Act_Loding.this, "Buka aksesmu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void rxPermissions2() {
        rxPermissions = new RxPermissions(Act_Loding.this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        try {
                            locationTool.startLocation();
                        } catch (Exception e) {

                        }
                        initDataView();
                    } else {
                        initDataView();
                        Toast.makeText(Act_Loding.this, "Buka aksesmu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initDataView() {
        //        toSetLanguage(1);
        updateHandler.sendEmptyMessageDelayed(14, 3000);
        utils = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long l) {
                untilTime.setClickable(false); //设置不可点击
                untilTime.setText(l / 1000 + "s");  //设置倒计时时间
                untilTime.setTextColor(Color.parseColor("#ffffff")); //设置按钮为灰色，这时是不能点击的
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
                SpannableString spannableString = new SpannableString(untilTime.getText().toString());  //获取按钮上的文字
                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ffffff"));
                /**
                 * public void setSpan(Object what, int start, int end, int flags) {
                 * 主要是start跟end，start是起始位置,无论中英文，都算一个。
                 * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
                 */
                spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                untilTime.setText(spannableString);
            }

            @Override
            public void onFinish() {
                untilTime.setText("");
                untilTime.setClickable(true);//重新获得点击
                untilTime.setTextColor(Color.parseColor("#ffffff"));
            }
        };
        utils.start();
        userstatistics();
        //获取地址信息
        publicaddress();
        if (aCache.getAsString("lognbean") != null && !aCache.getAsString("lognbean").equals("")) {
            Gson gson = new Gson();
            codeLoginBean getSmsVerifyBean = gson.fromJson(aCache.getAsString("lognbean"), codeLoginBean.class);
            MyApplication.setLognBean(getSmsVerifyBean.getData());
        }
    }

    @Override
    public void updateUI() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.cancel();
    }

    @Override
    public void onNoInterNet() {

    }

    /**
     * 更新
     */
    public void checkforupdate() {
        HttpHelper.checkforupdate(this, new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                startAct(Act_Main.class);
                finish();
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                checkforupdateBean getSmsVerifyBean = gson.fromJson(succeed, checkforupdateBean.class);
                if (getSmsVerifyBean.getStatus() == 1) {
                    Double versionCode = Double.valueOf(getVersionName());
                    if (versionCode < Double.valueOf(getSmsVerifyBean.getData().getData().getCode())) {
                        upData(getSmsVerifyBean.getData().getData().getApp_link());
                    } else {
//                        if (aCache.getAsString("lognbean") != null && !aCache.getAsString("lognbean").equals("")) {
//                            codeLoginBean codeLoginBean = gson.fromJson(aCache.getAsString("lognbean"), codeLoginBean.class);
//                            MyApplication.setLognBean(codeLoginBean.getData());
                        startAct(Act_Main.class);
                        finish();
//                        } else {
//                        startAct(Act_LogOn.class);
//                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                loding.dismiss();
                startAct(Act_Main.class);
                finish();
                MyToast.show(context, error);
            }
        });
    }

    private long getVersionName() {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionCode;
    }

    public void upData(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新提示");
        builder.setMessage("退出");
        builder.setNegativeButton("退出软件", (dialogInterface, i) -> finish());
        builder.setPositiveButton("开始更新", (dialogInterface, i) -> {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void toSetLanguage(int type) {
        boolean sameLanguage = I18NUtils.isSameLanguage(this, type);
        if (!sameLanguage) {
            I18NUtils.setLocale(this, type);
            // 前面取系统语言时判断spType=0时取第一值，所以设置完语言后缓存type
            I18NUtils.putLanguageType(this, type);
            I18NUtils.toRestartMainActvity(this);
        } else {
            // 缓存用户此次选择的类型，可能出现type不同而locale一样的情况（如：系统默认泰语type = 0，而我选择的也是泰语type = 3）
            I18NUtils.putLanguageType(this, type);
        }
    }

    String deviceId = "";

    /**
     * 统计app下载
     */
    public void userstatistics() {
        try {
            deviceId = DeviceIdUtil.getDeviceId(this);
        } catch (Exception e) {
        }
        HttpHelper.userstatistics(this, deviceId, new HttpHelper.HttpUtilsCallBack<String>() {
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
     * 地址选择
     */
    public void publicaddress() {
        HttpHelper.publicaddress(new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                publicaddressBean getSmsVerifyBean = gson.fromJson(succeed, publicaddressBean.class);
                if (getSmsVerifyBean.getStatus() == 1) {
                    JsonDataBean jsonDataBean = new JsonDataBean();
                    List<JsonDataBean.DataBean> shengfen = new ArrayList<>();
                    for (int i = 0; i < getSmsVerifyBean.getData().size(); i++) {
                        JsonDataBean.DataBean dataBean = new JsonDataBean.DataBean();
                        List<JsonDataBean.DataBean.CityBean> City = new ArrayList<>();
                        for (int j = 0; j < getSmsVerifyBean.getData().get(i).getRegencies().size(); j++) {
                            JsonDataBean.DataBean.CityBean bean = new JsonDataBean.DataBean.CityBean();
                            List<String> area = new ArrayList<>();
                            for (int f = 0; f < getSmsVerifyBean.getData().get(i).getRegencies().get(j).getDistricts().size(); f++) {
                                area.add(getSmsVerifyBean.getData().get(i).getRegencies().get(j).getDistricts().get(f).getName());
                            }
                            bean.setName(getSmsVerifyBean.getData().get(i).getRegencies().get(j).getName());
                            bean.setArea(area);
                            City.add(bean);
                        }
                        dataBean.setName(getSmsVerifyBean.getData().get(i).getName());
                        dataBean.setCity(City);
                        shengfen.add(dataBean);
                    }
                    jsonDataBean.setData(shengfen);
                    String datas = gson.toJson(jsonDataBean);
                    JSONObject jsonObject = null;
                    String data = "";
                    try {
                        jsonObject = new JSONObject(datas);
                        data = jsonObject.getString("data");
                        aCache.put("cityBean", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void backAdd(String location) {
        MyApplication.addr = location;
        Debug.e("--------地址获取成功===" + location);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onSheZhiError() {

    }
}
