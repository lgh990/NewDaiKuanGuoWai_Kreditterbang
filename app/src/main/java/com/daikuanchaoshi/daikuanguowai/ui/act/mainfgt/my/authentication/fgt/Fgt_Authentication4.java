package com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daikuanchaoshi.daikuanguowai.R;
import com.daikuanchaoshi.daikuanguowai.commt.MyApplication;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.FileBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.MyUserInfoBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.PostFaceComparisonBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userSaveExtendInfoBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.dlg.Dlg_Photograph;
import com.daikuanchaoshi.daikuanguowai.ui.http.HttpHelper;
import com.daikuanchaoshi.daikuanguowai.ui.utils.Auth;
import com.daikuanchaoshi.daikuanguowai.ui.utils.MyUtils;
import com.daikuanchaoshi.daikuanguowai.ui.utils.permission.RxPermissions;
import com.daikuanchaoshi.daikuanguowai.ui.utils.permission.SchedulerTransformer;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lykj.aextreme.afinal.common.BaseFragment;
import com.lykj.aextreme.afinal.utils.Debug;
import com.lykj.aextreme.afinal.utils.MyToast;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UploadManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ai.advance.liveness.lib.LivenessResult;
import ai.advance.liveness.sdk.activity.LivenessActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.app.Activity.RESULT_OK;

/**
 * ????????????4
 */
public class Fgt_Authentication4 extends BaseFragment implements Dlg_Photograph.OnClick {
    Unbinder unbinder1;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_idNumber)
    TextView tvIdNumber;
    @BindView(R.id.idCardZhengMian)
    ImageView idCardZhengMian;
    @BindView(R.id.idCardFanMian)
    ImageView idCardFanMian;
    @BindView(R.id.bt_commit)
    TextView btCommit;

    @Override
    public int initLayoutId() {
        return R.layout.fgt_authentication4;
    }

    Unbinder unbinder;
    private UploadManager uploadManager;
    private Recorder recorder;
    private KeyGenerator keyGen;
    private String upToken;
    String accessKey = "kFiJToSdmLlsn2ttg-L8_X51Cu4oFialS-9qIKtB";
    String secretKey = "MGYWxZcIKcDcxzxJfDiar42ERXg1Wtimz-ixCl0U";

    String bucket = "ynimage001";

    @Override
    public void initView() {
        hideHeader();
        unbinder = ButterKnife.bind(Fgt_Authentication4.this, v);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //????????????????????????????????????????????????
                .init();
        Auth auth = Auth.create(accessKey, secretKey);
        upToken = auth.uploadToken(bucket);
        config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // ???????????????????????????????????? ??????256K
                .putThreshhold(1024 * 1024)   // ?????????????????????????????????512K
                .connectTimeout(10)           // ?????????????????????10???
                .useHttps(true)               // ????????????https????????????
                .responseTimeout(60)          // ??????????????????????????????60???
                .recorder(null)           // recorder????????????????????????????????????????????????null
                .recorder(recorder, keyGen)   // keyGen ??????????????????????????????????????????????????????????????????????????????????????????
                .zone(FixedZone.zoneAs0)        // ????????????????????????????????????????????????????????????????????????IP???
                .build();
    }

    Configuration config;
    private Dlg_Photograph dlg_photograph;

    @Override
    public void initData() {
        dlg_photograph = new Dlg_Photograph(getContext(), this);
        dlg_photograph.setTouchCancle(true);
        if (MyApplication.lognStatus) {
            userInfo();
        }
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onNoInterNet() {

    }

    @Override
    public void sendMsg(int flag, Object obj) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }


    @OnClick({R.id.idCardZhengMian, R.id.idCardFanMian})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.idCardZhengMian:
                dlg_photograph.show();
                break;
            case R.id.idCardFanMian:
                new RxPermissions(getActivity())
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .compose(new SchedulerTransformer<Boolean>())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                toLivenessActivity();
                            } else {
                                MyToast.show(context, "??????????????????????????????????????????");
                            }
                        });
                break;
        }
    }

    static final int REQUEST_CODE_LIVENESS = 1000;

    private void toLivenessActivity() {
        Intent intent = new Intent(getActivity(), LivenessActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LIVENESS);
    }

    //??????
    @Override
    public void PhotographBack() {
        new RxPermissions(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .compose(new SchedulerTransformer<Boolean>())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        camera();
                    } else {
                        MyToast.show(context, "??????????????????????????????????????????");
                    }
                });
    }

    //??????
    @Override
    public void photoAlbumBack() {
        new RxPermissions(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .compose(new SchedulerTransformer<Boolean>())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1);
                    } else {
                        MyToast.show(context, "??????????????????????????????????????????");
                    }
                });
    }

    @Override
    public void onCancle() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1://?????????????????????
                if (data == null) {
                    return;
                }
                uri = data.getData();
                file = new File(MyUtils.getPath(uri, getActivity()));
                loding.show();
                Avatar();
                break;
            case 2://??????????????????
                if (Build.VERSION.SDK_INT < 24) {
                    file = currentImageFile;
                } else {
                    file = new File(MyUtils.getPath(uri, getActivity()));
                }
                loding.show();
                Avatar();
                break;
            case REQUEST_CODE_LIVENESS:
                if (resultCode == RESULT_OK) {
                    String livenessId = LivenessResult.getLivenessId();
                    Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();
                    String transactionId = LivenessResult.getTransactionId();
                    boolean success = LivenessResult.isSuccess();
                    String errorMsg = LivenessResult.getErrorMsg();
                    saveImageToGallery(livenessBitmap);
                    shouchiFile = new File(appDir + "/" + fileName);
                    Glide.with(getContext()).load(shouchiFile).into(idCardFanMian);
                    postFaceComparison();
                }
                break;
        }

    }

    /**
     * ????????????
     */
    private File currentImageFile;

    public void camera() {
        File dir = new File(Environment.getExternalStorageDirectory(), "myimage");//???sd??????????????????myimage???Environment.getExternalStorageDirectory()??????SD???????????????
        if (!dir.exists()) {    //exists()???????????????????????????????????????????????????
            dir.mkdirs();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//?????????????????????android???????????????????????????????????????????????????????????????
        String filename = df.format(new Date());
        currentImageFile = new File(dir, filename + ".jpg");
        if (!currentImageFile.exists()) {
            try {
                currentImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < 24) {
            currentImageFile = new File(dir, filename + ".jpg");
            // ??????????????????uri
            uri = Uri.fromFile(currentImageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            // ??????Android 7.0 ???????????????????????????
            ContentValues contentValues = new ContentValues();
            uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, 2);
    }

    Uri uri;
    private File file = null;

    public void Avatar() {
//????????????File????????????????????????
        File file1 = new File(Environment.getExternalStorageDirectory(), "mywork");
//????????????????????????????????????????????????????????????????????????
        if (!file1.exists()) {
            //??????file???mkdirs()???????????????????????????????????????????????????
            file1.mkdirs();
        }
        Luban.with(getContext())
                .load(file)
                .ignoreBy(20)
                .setTargetDir(file1.getPath())
                .filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO ???????????????????????????????????????????????? loading UI
                    }

                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(final File file) {
                        zhengFile = file;
                        postFile(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Debug.e("onError--------" + e.getLocalizedMessage());
                        // TODO ????????????????????????????????????
                    }
                }).launch();
    }

    File zhengFile, shouchiFile;
    private String zhengmianKey = "", shouchiKey = "";

    public void postFile(File file) {
        OkHttpUtils
                .post()
                .url("https://api.advance.ai/openapi/face-recognition/v2/ocr-check")
                .addFile("ocrImage", file.getName(), file)
                .addHeader("X-ADVAI-KEY", "368a31911af9c834")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loding.dismiss();
                        MyToast.show(getActivity(), e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        loding.dismiss();
                        Gson gson = new Gson();
                        Debug.e("----------"+response);
                        FileBean fileBean = gson.fromJson(response, FileBean.class);
                        if (fileBean.getCode().equals("SUCCESS")) {
                            tvName.setText(fileBean.getData().getName());
                            tvIdNumber.setText(fileBean.getData().getIdNumber());
                            idcard = fileBean.getData().getIdNumber();
                            username = fileBean.getData().getName();
                            Glide.with(getActivity()).load(zhengFile).into(idCardZhengMian);
                            MyToast.show(getActivity(), fileBean.getMessage());
                            postFaceComparison();
                        } else {
                            MyToast.show(getActivity(), fileBean.getMessage());
                        }
                    }
                });
    }

    File appDir;
    String fileName;

    public int saveImageToGallery(Bitmap bmp) {
        //????????????
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirName = "erweima16";
        appDir = new File(root, dirName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //??????????????????
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(timeStamp));
        fileName = sd + ".png";
        //????????????
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return 2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * ?????????????????????????????????
     */
    public void postFaceComparison() {
        if (shouchiFile == null || zhengFile == null) {
            return;
        }
        OkHttpUtils
                .post()
                .url("https://api.advance.ai/openapi/face-recognition/v3/check")
                .addFile("firstImage", zhengFile.getName(), zhengFile)
                .addFile("secondImage", shouchiFile.getName(), shouchiFile)
                .addHeader("X-ADVAI-KEY", "368a31911af9c834")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        MyToast.show(getActivity(), e.getMessage());
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        PostFaceComparisonBean fileBean = gson.fromJson(response, PostFaceComparisonBean.class);
                        if (fileBean.getCode().equals("SUCCESS")) {
//                            if (fileBean.getData().getSimilarity() >= 70) {
                                btCommit.setSelected(true);
//                            } else {
//                                MyToast.show(getActivity(), fileBean.getMessage());
//                            }
                        } else {
                            MyToast.show(getActivity(), fileBean.getMessage());
                        }
                    }
                });
    }

    @OnClick(R.id.bt_commit)
    public void onViewClicked() {
        if (!btCommit.isSelected()) {
            MyToast.show(getContext(), "Silakan lihat apakah dipilih lengkap???");
            return;
        }
        loding.show();
        for (int i = 0; i < 2; i++) {
            String token = upToken;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String key1 = "icon_" + i + sdf.format(new Date());
            uploadManager = new UploadManager(config);
            File myFile;
            if (i == 0) {
                myFile = zhengFile;
            } else {
                myFile = shouchiFile;
            }
            int finalI = i;
            uploadManager.put(myFile, key1, token,
                    (key, info, res) -> {
                        //res??????hash???key??????????????????????????????????????????????????????
                        if (info.isOK()) {
                            if (finalI == 0) {
                                zhengmianKey = key;
                            } else {
                                shouchiKey = key;
                            }
                            if (TextUtils.isEmpty(zhengmianKey)) {
                                return;
                            }
                            if (TextUtils.isEmpty(shouchiKey)) {
                                return;
                            }
                            userSaveInfo();
                        } else {
                            loding.dismiss();
                            Toast.makeText(getContext(), "Upload gambar gagal tolong cek koneksi internet", Toast.LENGTH_SHORT).show();
                            //??????????????????????????????info?????????????????????????????????????????????????????????????????????
                        }
                    }, null);
        }
    }

    private String idcard = "", username = "";

    /**
     * ??????????????????
     * idcard ????????????
     * username  ???????????????
     * idcardimg1 ???????????????
     * idcardimg2 ???????????????
     * idcardimg3 ???????????????
     */
    public void userSaveInfo() {
        if (TextUtils.isEmpty(idcard)) {
            return;
        }
        if (TextUtils.isEmpty(username)) {
            return;
        }
        if (TextUtils.isEmpty(zhengmianKey)) {
            return;
        }
        if (TextUtils.isEmpty(shouchiKey)) {
            return;
        }
        HttpHelper.userSaveInfo(getContext(), idcard, username, zhengmianKey, zhengmianKey, shouchiKey, new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                userSaveExtendInfoBean loangetBean = gson.fromJson(succeed, userSaveExtendInfoBean.class);
                if (loangetBean.getStatus() == 1) {
                    MyToast.show(getContext(), "Berhasil dilestarikan???");
                    getActivity().setResult(10);
                    getActivity().finish();
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
     * ??????????????????
     */
    public void userInfo() {
        HttpHelper.userInfo(getContext(), new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                MyUserInfoBean entity = gson.fromJson(succeed, MyUserInfoBean.class);
                if (entity.getStatus() == 1) {
                    Glide.with(getContext()).load(entity.getData().getMember().getIdcardimg1()).into(idCardZhengMian);
                    Glide.with(getContext()).load(entity.getData().getMember().getIdcardimg3()).into(idCardFanMian);
                    tvName.setText(entity.getData().getMember().getUsername());
                    tvIdNumber.setText(entity.getData().getMember().getIdcard());
                }
            }

            @Override
            public void onError(String error) {
                loding.dismiss();
                MyToast.show(context, error);
            }
        });
    }
}
