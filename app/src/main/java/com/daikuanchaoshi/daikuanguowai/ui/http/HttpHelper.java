package com.daikuanchaoshi.daikuanguowai.ui.http;

import android.content.Context;
import android.content.Intent;

import com.daikuanchaoshi.daikuanguowai.commt.MyApplication;
import com.daikuanchaoshi.daikuanguowai.ui.act.Act_LogonRegister;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.MyLoanBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.codeLoginBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.getSmsVerifyBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.publicForgetBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.publicRegisterBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.publicaddressBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.userModify_passwordBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.home.bean.HomeDashbordBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.home.bean.loangetBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.home.bean.postapplyLoanBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.MyUserInfoBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userContactBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userExtendInfoBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userSaveExtendInfoBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userSaveInfoBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userSaveWorkBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userWorkBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.bean.bankListsBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.bean.bankUpdateBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.bean.userGetBean;
import com.google.gson.Gson;
import com.lykj.aextreme.afinal.utils.ACache;
import com.lykj.aextreme.afinal.utils.Debug;
import com.lykj.aextreme.afinal.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

public class HttpHelper {
    /**
     * ???????????????
     */
    public static void getSmsVerify(Context context, String telephone, String type, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("telephone", telephone);
        map.put("type", type);
        map.put("tag", "20");
        map.put("auth", md5("w934ukfnsi23re4@#!"));
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.getSmsVerify(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        getSmsVerifyBean entity = gson.fromJson(succeed, getSmsVerifyBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //Md5??????
    public static String md5(String plainText) {
        //????????????????????????
        byte[] secretBytes = null;
        try {
            // ????????????MD5??????????????????
            MessageDigest md = MessageDigest.getInstance("MD5");
            //????????????????????????
            md.update(plainText.getBytes());
            //????????????????????????
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("??????md5???????????????");
        }
        //??????????????????????????????16????????????
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // ????????????????????????32?????????????????????0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }


    /**
     * ??????
     */
    public static void checkforupdate(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "android");
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.checkforupdate(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        Debug.e("-------------succeed===" + succeed);
                        getSmsVerifyBean entity = gson.fromJson(succeed, getSmsVerifyBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError("???????????????");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * ???????????????
     */
    public static void codeLogin(Context context, String telephone, String code, String address, String from, String android_id, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("telephone", telephone);
        map.put("from", "android_" + from);
        map.put("code", code);
        map.put("address", address);
        map.put("tag", "20");
        map.put("android_id", android_id);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.codeLogin(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        codeLoginBean entity = gson.fromJson(succeed, codeLoginBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * ????????????
     */
    public static void login(Context context, String telephone, String password, String address, String apppath, String from, String android_id, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("telephone", telephone);
        map.put("from", "android_" + from);
        map.put("password", password);
        map.put("address", address);
        map.put("apppath", apppath);
        map.put("android_id", android_id);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.login(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        Debug.e("-------------????????????==" + map.toString() + "_----------" + succeed);
                        codeLoginBean entity = gson.fromJson(succeed, codeLoginBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ??????
     */
    public static void publicRegister(Context context, String telephone, String password, String repassword, String varf, String android_id, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("telephone", telephone);
        map.put("password", password);
        map.put("repassword", repassword);
        map.put("varf", varf);
        map.put("from", "android");
        map.put("tag", "20");
        map.put("android_id", android_id);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.publicRegister(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        publicRegisterBean entity = gson.fromJson(succeed, publicRegisterBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * ????????????
     */
    public static void publicForget(Context context, String telephone, String password, String varf, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("telephone", telephone);
        map.put("password", password);
        map.put("varf", varf);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.publicForget(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        publicForgetBean entity = gson.fromJson(succeed, publicForgetBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * ????????????
     */
    public static void homeDashbord(Context context, String Token, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        if (Token.equals("")) {
            httpService.homeDashbord(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(String succeed) {
                            Gson gson = new Gson();
                            HomeDashbordBean entity = gson.fromJson(succeed, HomeDashbordBean.class);
                            if (choseLoginStatis(entity.getStatus(), context)) {
                                return;
                            }
                            if (entity.getStatus() == 1) {
                                callBack.onSucceed(succeed);
                            } else {
                                callBack.onError(entity.getMsg());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            callBack.onFailure(httpFailureMsg());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            httpService.homeDashbord(map, Token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(String succeed) {
                            Gson gson = new Gson();
                            HomeDashbordBean entity = gson.fromJson(succeed, HomeDashbordBean.class);
                            if (choseLoginStatis(entity.getStatus(), context)) {
                                return;
                            }
                            if (entity.getStatus() == 1) {
                                callBack.onSucceed(succeed);
                            } else {
                                callBack.onError(entity.getMsg());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            callBack.onFailure(httpFailureMsg());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }

    /**
     * ???????????????
     */
    public static void bankLists(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.bankLists(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        bankListsBean entity = gson.fromJson(succeed, bankListsBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ?????????????????????
     */
    public static void bankUpdate(Context context, String telephone, String username, String idcard, String bankno, String bankname, String bank_key, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("telephone", telephone);
        map.put("username", username);
        map.put("idcard", idcard);
        map.put("bankno", bankno);
        map.put("bankname", bankname);
        map.put("bank_key", bank_key);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.bankUpdate(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        bankUpdateBean entity = gson.fromJson(succeed, bankUpdateBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ????????????
     */
    public static void bankValidBanks(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.bankValidBanks(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        Debug.e("------------????????????===succeed==" + succeed);
                        bankListsBean entity = gson.fromJson(succeed, bankListsBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ????????????
     */
    public static void loanapplyLoan(Context context, String days, String amount, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("days", days);
        map.put("amount", amount);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.loanapplyLoan(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        postapplyLoanBean entity = gson.fromJson(succeed, postapplyLoanBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ????????????
     */
    public static void loanget(Context context, String loan_id, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("loan_id", loan_id);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.loanget(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        loangetBean entity = gson.fromJson(succeed, loangetBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Debug.e("-----------????????????===succeed==" + e.getMessage());
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ??????????????????
     */
    public static void userWork(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userWork(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        userWorkBean entity = gson.fromJson(succeed, userWorkBean.class);
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * ??????????????????
     * type ????????????
     * income??????????????????
     * work_time ???????????????
     * name ????????????
     * address ????????????
     * income_date ??????????????????
     * telephone ??????????????????
     */
    public static void userSaveWork(Context context,
                                    String type,
                                    String income,
                                    String work_time,
                                    String name,
                                    String address,
                                    String income_date,
                                    String telephone,
                                    String address2,
                                    final HttpUtilsCallBack<String> callBack) {
        OkHttpUtils
                .post()
                .url(ApiConstant.ROOT_URL + ApiConstant.userSaveWork)
                .addHeader("token", MyApplication.getLognBean().getMember().getToken())
                .addParams("type", type)
                .addParams("income", income)
                .addParams("work_time", work_time)
                .addParams("name", name)
                .addParams("address", address)
                .addParams("income_date", income_date)
                .addParams("telephone", telephone)
                .addParams("address2", address2)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        userSaveWorkBean entity = gson.fromJson(response, userSaveWorkBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(response);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }
                });
    }

    /**
     * ??????????????????
     */
    public static void userSaveInfo(Context context,
                                    String username1, String telephone1, String relationship1,
                                    String username2, String telephone2, String relationship2,
                                    String username3, String telephone3, String relationship3,
                                    String othercontacts, String phone_records,
                                    final HttpUtilsCallBack<String> callBack) {
        Debug.e("--------?????????-othercontacts===" + othercontacts);
        Debug.e("--------????????????-phone_records===" + phone_records);
        OkHttpUtils
                .post()
                .url(ApiConstant.ROOT_URL + ApiConstant.userSaveContact)
                .addHeader("token", MyApplication.getLognBean().getMember().getToken())
                .addParams("username1", username1)
                .addParams("telephone1", telephone1)
                .addParams("relationship1", relationship1)
                .addParams("username2", username2)
                .addParams("telephone2", telephone2)
                .addParams("relationship2", relationship2)
                .addParams("username3", username3)
                .addParams("telephone3", telephone3)
                .addParams("relationship3", relationship3)
                .addParams("othercontacts", othercontacts)
                .addParams("phone_records", phone_records)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Debug.e("-------------response===" + response);
                        Gson gson = new Gson();
                        userSaveInfoBean entity = gson.fromJson(response, userSaveInfoBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(response);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }
                });
    }

    /**
     * ?????????????????????
     */
    public static void userContact(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userContact(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        userContactBean entity = gson.fromJson(succeed, userContactBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * ??????????????????
     */
    public static void userExtendInfo(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userExtendInfo(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        userExtendInfoBean entity = gson.fromJson(succeed, userExtendInfoBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * ??????????????????
     * degree ??????
     * marriage  ??????
     * children ????????????
     * bank_name ????????????
     * bank_no ????????????
     */
    public static void userSaveExtendInfo(Context context,
                                          String degree, String marriage, String children,
                                          String bank_name, String bank_no,
                                          final HttpHelper.HttpUtilsCallBack<String> callBack) {
        OkHttpUtils
                .post()
                .url(ApiConstant.ROOT_URL + ApiConstant.userSaveExtendInfo)
                .addHeader("token", MyApplication.getLognBean().getMember().getToken())
                .addParams("degree", degree)
                .addParams("marriage", marriage)
                .addParams("children", children)
                .addParams("bank_name", bank_name)
                .addParams("bank_no", bank_no)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        userSaveExtendInfoBean entity = gson.fromJson(response, userSaveExtendInfoBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(response);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }
                });
    }

    /**
     * ??????????????????
     */
    public static void userGet(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userGet(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        try {
                            Gson gson = new Gson();
                            userGetBean entity = gson.fromJson(succeed, userGetBean.class);
                            if (choseLoginStatis(entity.getStatus(), context)) {
                                return;
                            }
                            if (entity.getStatus() == 1) {
                                callBack.onSucceed(succeed);
                            } else {
                                callBack.onError(entity.getMsg());
                            }
                        } catch (Exception e) {
                            callBack.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ??????????????????
     * idcard ????????????
     * username  ???????????????
     * ocr_info ????????????
     * idcardimg1 ???????????????
     * idcardimg2 ???????????????
     * idcardimg3 ???????????????
     */
    public static void userSaveInfo(Context context,
                                    String idcard, String username, String idcardimg1,
                                    String idcardimg2, String idcardimg3,
                                    final HttpHelper.HttpUtilsCallBack<String> callBack) {
        OkHttpUtils
                .post()
                .url(ApiConstant.ROOT_URL + ApiConstant.userSaveInfo)
                .addHeader("token", MyApplication.getLognBean().getMember().getToken())
                .addParams("idcard", idcard)
                .addParams("username", username)
                .addParams("idcardimg1", idcardimg1)
                .addParams("idcardimg2", idcardimg2)
                .addParams("idcardimg3", idcardimg3)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        userSaveExtendInfoBean entity = gson.fromJson(response, userSaveExtendInfoBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(response);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }
                });
    }


    /**
     * ??????????????????
     */
    public static void userInfo(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userInfo(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        MyUserInfoBean entity = gson.fromJson(succeed, MyUserInfoBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ????????????
     */
    public static void userModify_password(Context context, String password, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("password", password);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userModify_password(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        userModify_passwordBean entity = gson.fromJson(succeed, userModify_passwordBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ??????????????????
     */
    public static void loanLists(Context context, String page, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("limit", "10");
        map.put("page", page);
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.loanLists(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        MyLoanBean entity = gson.fromJson(succeed, MyLoanBean.class);
                        if (choseLoginStatis(entity.getStatus(), context)) {
                            return;
                        }
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ????????????????????????
     */
    public static void userContract(Context context, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userContract(map, MyApplication.getLognBean().getMember().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        Debug.e("----------succeed==" + succeed);
//                        MyLoanBean entity = gson.fromJson(succeed, MyLoanBean.class);
//                        if (choseLoginStatis(entity.getStatus(), context)) {
//                            return;
//                        }
//                        if (entity.getStatus() == 1) {
//                            callBack.onSucceed(succeed);
//                        } else {
//                            callBack.onError(entity.getMsg());
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
//    /**
//     * ????????????
//     */
//    public static void userapplyConfirm(Context context, String orderno, final HttpUtilsCallBack<String> callBack) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("orderno", orderno);
//        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
//        httpService.userapplyConfirm(map, MyApplication.getLognBean().getMember().getToken())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(String succeed) {
//                        Gson gson = new Gson();
//                        Debug.e("------------userapplyConfirm==="+succeed);
////                        loangetBean entity = gson.fromJson(succeed, loangetBean.class);
////                        if (entity.getStatus() == 1) {
////                            callBack.onSucceed(succeed);
////                        } else {
////                            callBack.onError("???????????????");
////                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        callBack.onFailure(httpFailureMsg());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });
//    }


    /**
     * ??????app??????
     */
    public static void userstatistics(Context context, String device_id, final HttpUtilsCallBack<String> callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("device_id", device_id);
        map.put("type", "1");
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.userstatistics(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
//                        MyLoanBean entity = gson.fromJson(succeed, MyLoanBean.class);
//                        if (entity.getStatus() == 1) {
//                            callBack.onSucceed(succeed);
//                        } else {
//                            callBack.onError(entity.getMsg());
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * ????????????
     */
    public static void publicaddress(HttpUtilsCallBack<String> callBack) {
        HttpService httpService = RetrofitFactory.getRetrofit(15l, 15l).create(HttpService.class);
        httpService.publicaddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String succeed) {
                        Gson gson = new Gson();
                        publicaddressBean entity = gson.fromJson(succeed, publicaddressBean.class);
                        if (entity.getStatus() == 1) {
                            callBack.onSucceed(succeed);
                        } else {
                            callBack.onError(entity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(httpFailureMsg());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public interface HttpUtilsCallBack<T> {
        public void onFailure(String failure);

        public void onSucceed(T succeed);

        public void onError(String error);
    }

    private static String httpFailureMsg() {
        if (NetUtils.isConnected()) {
//            return "?????????????????????????????????????????????";
            return "Silakan Cek Link Internet";
        } else {
//            return "?????????????????????????????????????????????";
            return "Silakan Cek Link Internet";
        }
    }

    /**
     * ????????????????????????
     */
    public static boolean choseLoginStatis(int messgecode, Context context) {
        if (messgecode == 401) {
            if (Utils.isFastClick() == false) {//???????????????
                return true;
            }
            ACache aCache = ACache.get(context);
            aCache.put("lognbean", "");
            Intent intent = new Intent(context, Act_LogonRegister.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}