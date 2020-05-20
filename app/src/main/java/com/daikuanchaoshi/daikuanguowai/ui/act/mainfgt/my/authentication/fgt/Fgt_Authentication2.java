package com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daikuanchaoshi.daikuanguowai.R;
import com.daikuanchaoshi.daikuanguowai.commt.MyApplication;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.getContentCallLogBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userContactBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.authentication.fgt.bean.userSaveInfoBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.bean.ContactBeen;
import com.daikuanchaoshi.daikuanguowai.ui.http.HttpHelper;
import com.daikuanchaoshi.daikuanguowai.ui.utils.permission.RxPermissions;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lykj.aextreme.afinal.common.BaseFragment;
import com.lykj.aextreme.afinal.utils.Debug;
import com.lykj.aextreme.afinal.utils.MyToast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * 联系人
 */
public class Fgt_Authentication2 extends BaseFragment {
    @BindView(R.id.spinner1)
    MaterialSpinner spinner1;
    @BindView(R.id.spinner2)
    MaterialSpinner spinner2;
    @BindView(R.id.spinner3)
    MaterialSpinner spinner3;
    Unbinder unbinder1;
    RxPermissions rxPermissions;
    @BindView(R.id.tv_EmergencyContact1)
    TextView et_EmergencyContact1;
    @BindView(R.id.tv_EmergencyContact2)
    TextView et_EmergencyContact2;
    @BindView(R.id.tv_EmergencyContact3)
    TextView et_EmergencyContact3;
    @BindView(R.id.commit)
    TextView commit;
    private String phoneName1 = "", phoneName2 = "", phoneName3 = "";

    @Override
    public int initLayoutId() {
        return R.layout.fgt_authentication2;
    }

    Unbinder unbinder;

    @Override
    public void initView() {
        hideHeader();
        unbinder = ButterKnife.bind(Fgt_Authentication2.this, v);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        relationship1 = "Orang Tua";
        //紧急联系人关系
        String[] ITEMS = {"Orang Tua", "Suami/Istri", "Anak", "Saudara"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                relationship1 = ITEMS[i];
                edStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        relationship2 = "Orang Tua";
        //紧急联系人 2关系
        String[] ITEMS1 = {"Orang Tua", "Suami/Istri", "Anak", "Teman", "Saudara", "Lainnya"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ITEMS1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter1);
        spinner2.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                relationship2 = ITEMS1[i];
                edStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        relationship3 = "Orang Tua";
        //紧急联系人 3关系
        String[] ITEMS2 = {"Orang Tua", "Suami/Istri", "Anak", "Teman", "Saudara", "Lainnya"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ITEMS2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter2);
        spinner3.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                relationship3 = ITEMS2[i];
                edStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void initData() {
        if (MyApplication.lognStatus) {
            userContact();
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

    private int choose = 0;

    @OnClick({R.id.tv_EmergencyContact1, R.id.tv_EmergencyContact2, R.id.tv_EmergencyContact3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_EmergencyContact1:
                openPermissions(1);
                break;
            case R.id.tv_EmergencyContact2:
                openPermissions(2);
                break;
            case R.id.tv_EmergencyContact3:
                openPermissions(3);
                break;
        }
    }

    private List<ContactBeen> phoneDtos;
    private int VERSION;
    protected final int REQUEST_ADDRESSBOOK = 11;
    public void openPermissions(int choseStatus) {
        VERSION = Build.VERSION.SDK_INT;
        if (VERSION >= 23) {
            rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            choose = choseStatus;
                            startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    ContactsContract.Contacts.CONTENT_URI), 0);
                        } else {
                            Toast.makeText(getContext(), "Buka aksesmu", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
//            startActivityForResult(new Intent(Intent.ACTION_PICK,
//                    ContactsContract.Contacts.CONTENT_URI), 0);
            choose = choseStatus;
            Intent intent = new Intent();
            intent.setAction("android.intent.action.PICK");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("vnd.android.cursor.dir/phone_v2");
            startActivityForResult(intent, REQUEST_ADDRESSBOOK);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                getPhoneContacts11();
                getContentCallLog();
                if (data == null) {
                    return;
                }
                //处理返回的data,获取选择的联系人信息
                Uri uri = data.getData();
                String[] contacts = getPhoneContacts(uri);
                if (choose == 1) {
                    if (contacts.length >= 1) {
                        et_EmergencyContact1.setText(contacts[0] + "：" + contacts[1]);
                        name1 = contacts[0];
                    }
                    if (contacts.length >= 2) {
                        phoneName1 = contacts[0] + "：" + contacts[1];
                        phone1 = contacts[1];
                    }
                    edStatus();
                } else if (choose == 2) {
                    if (contacts.length >= 2) {
                        et_EmergencyContact2.setText(contacts[0] + "：" + contacts[1]);
                        phoneName2 = contacts[0] + "：" + contacts[1];
                        phone2 = contacts[1];
                    }
                    if (contacts.length >= 1) {
                        name2 = contacts[0];
                    }
                    edStatus();
                } else if (choose == 3) {
                    if (contacts.length >= 2) {
                        et_EmergencyContact3.setText(contacts[0] + "：" + contacts[1]);
                        phoneName3 = contacts[0] + "：" + contacts[1];
                        phone3 = contacts[1];
                    }
                    if (contacts.length >= 1) {
                        name3 = contacts[0];
                    }
                    edStatus();
                }
                Gson gson = new Gson();
                try {
                    othercontacts = "";
                    othercontacts = gson.toJson(phoneDtos);
                    phone_records = "";
                    phone_records = gson.toJson(tonghuajilu);
                } catch (Exception e) {
                }
                break;
            case REQUEST_ADDRESSBOOK:
                getPhoneContacts11();
                getContentCallLog();
                try {
                    if (data != null) {
                        Uri uri1 = data.getData();
                        String phoneNum = null;
                        String contactName = null;
                        // 创建内容解析者
                        ContentResolver contentResolver = getActivity().getContentResolver();
                        Cursor cursor = null;
                        if (uri1 != null) {
                            cursor = contentResolver.query(uri1, new String[]{"display_name", "data1"}, null, null, null);
                        }
                        while (cursor.moveToNext()) {
                            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        cursor.close();
                        if (phoneNum != null) {
                            phoneNum = phoneNum.replaceAll("-", " ");
                            phoneNum = phoneNum.replaceAll(" ", "");
                            if (choose == 1) {
                                et_EmergencyContact1.setText(contactName + "：" + phoneNum);
                                phoneName1 = contactName + "：" + phoneNum;
                                phone1 = phoneNum;
                                name1 = contactName;
                                edStatus();
                            } else if (choose == 2) {
                                et_EmergencyContact2.setText(contactName + "：" + phoneNum);
                                phoneName2 = contactName + "：" + phoneNum;
                                phone2 = phoneNum;
                                name2 = contactName;
                                edStatus();
                            } else if (choose == 3) {
                                et_EmergencyContact3.setText(contactName + "：" + phoneNum);
                                phoneName3 = contactName + "：" + phoneNum;
                                phone3 = phoneNum;
                                name3 = contactName;
                                edStatus();
                            }
                        }
                        Gson gson1 = new Gson();
                        try {
                            othercontacts = "";
                            othercontacts = gson1.toJson(phoneDtos);
                            phone_records = "";
                            phone_records = gson1.toJson(tonghuajilu);
                        } catch (Exception e) {
                        }
                    } else {
                        Toast.makeText(getContext(), "用户未选择联系人", Toast.LENGTH_LONG).show(); //用户未选择联系人
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "获取联系人失败", Toast.LENGTH_LONG).show();
                } //异常
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getActivity().getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor != null) {
                cursor.moveToFirst();
                //取得联系人姓名
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contact[0] = cursor.getString(nameFieldColumnIndex);
                //取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                if (phone != null) {
                    phone.moveToFirst();
                    contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phone.close();
                cursor.close();
            } else {
                return null;
            }
        }
        return contact;
    }

    private String name1 = "", phone1 = "", relationship1 = "", name2 = "", phone2 = "", relationship2 = "", name3 = "", phone3 = "", relationship3 = "", othercontacts = "", phone_records = "";

    /**
     * 保存工作信息
     */
    public void userSaveInfo() {
        loding.show();
        HttpHelper.userSaveInfo(getContext(), name1, phone1, relationship1, name2, phone2, relationship2, name3, phone3, relationship3, othercontacts, phone_records, new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                userSaveInfoBean loangetBean = gson.fromJson(succeed, userSaveInfoBean.class);
                if (loangetBean.getStatus() == 1) {
                    getActivity().setResult(10);
                    getActivity().finish();
                    MyToast.show(getContext(), "Kirim berhasil！");
                }
            }

            @Override
            public void onError(String error) {
                loding.dismiss();
                MyToast.show(context, error);
            }
        });
    }

    @OnClick(R.id.commit)
    public void onViewClicked() {
        Debug.e("------------提交");
        userSaveInfo();
    }

    /**
     * 状态显示
     */
    public void edStatus() {
        if (TextUtils.isEmpty(name1)) {
            return;
        }
        if (TextUtils.isEmpty(phone1)) {
            return;
        }
        if (TextUtils.isEmpty(relationship1)) {
            return;
        }
        if (TextUtils.isEmpty(name2)) {
            return;
        }
        if (TextUtils.isEmpty(phone2)) {
            return;
        }
        if (TextUtils.isEmpty(relationship2)) {
            return;
        }
        if (TextUtils.isEmpty(name3)) {
            return;
        }
        if (TextUtils.isEmpty(phone3)) {
            return;
        }
        if (TextUtils.isEmpty(relationship3)) {
            return;
        }
        commit.setSelected(true);
    }

    /**
     * 获取紧急联系人
     */
    public void userContact() {
        HttpHelper.userContact(getContext(), new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Debug.e("----------获取紧急联系人====" + succeed);
                Gson gson = new Gson();
                userContactBean loangetBean = gson.fromJson(succeed, userContactBean.class);
                if (loangetBean.getStatus() == 1) {
                    if (loangetBean.getData().getContactinfo() != null) {
                        commit.setSelected(true);
                        et_EmergencyContact1.setText(loangetBean.getData().getContactinfo().get(0).getUsername() + " " + loangetBean.getData().getContactinfo().get(0).getTelephone());
                        et_EmergencyContact2.setText(loangetBean.getData().getContactinfo().get(1).getUsername() + " " + loangetBean.getData().getContactinfo().get(1).getTelephone());
                        et_EmergencyContact3.setText(loangetBean.getData().getContactinfo().get(2).getUsername() + " " + loangetBean.getData().getContactinfo().get(2).getTelephone());
                    } else {
                        commit.setSelected(false);
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


    private Uri callUri = CallLog.Calls.CONTENT_URI;
    private String[] columns = {CallLog.Calls.CACHED_NAME// 通话记录的联系人
            , CallLog.Calls.NUMBER// 通话记录的电话号码
            , CallLog.Calls.DATE// 通话记录的日期
            , CallLog.Calls.DURATION// 通话时长
            , CallLog.Calls.TYPE};// 通话类型}
    private List<getContentCallLogBean> tonghuajilu = new ArrayList<>();

    //获取通话记录
    @TargetApi(Build.VERSION_CODES.N)
    private void getContentCallLog() {
        tonghuajilu.clear();
        @SuppressLint("MissingPermission")
        Cursor cursor = getActivity().getContentResolver().query(callUri, // 查询通话记录的URI
                columns
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));  //姓名
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));  //号码
                long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); //获取通话日期
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
                String time = new SimpleDateFormat("HH:mm").format(new Date(dateLong));
                int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));//获取通话时长，值为多少秒
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)); //获取通话类型：1.呼入2.呼出3.未接
//            String dayCurrent = new SimpleDateFormat("dd").format(new Date());
//            String dayRecord = new SimpleDateFormat("dd").format(new Date(dateLong));
                getContentCallLogBean bean = new getContentCallLogBean();
                if (name != null) {
                    bean.setName(name);
                } else {
                    bean.setName("erburu-buru, bukan?");
                }
                if (number != null) {
                    bean.setPhone(number);
                } else {
                    bean.setPhone("Nomor ponsel tidak tersedia saat ini");
                }
                if (time != null) {
                    bean.setTime(date + time);
                } else {
                    bean.setPhone("Tidak untuk sementara waktu.");
                }
                tonghuajilu.add(bean);
//            Debug.e("--------Call log: " + "\n"
//                    + "name: " + name + "\n"
//                    + "phone number: " + number + "\n"
//                    + "日期-" + date + "  " + time
//
//            );

            }
        }
    }

    /**
     * 获取库Phone表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    /**
     * 电话号码
     **/
    final int PHONES_NUMBER = 1;
    int PHONES_DISPLAY_NAME = 0;

    // 获取手机联系人
    private void getPhoneContacts11() {
        phoneDtos = new ArrayList<>();
        phoneDtos.clear();
        ContentResolver resolver = getActivity().getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PHONES_PROJECTION, null, null, null);
        // 不为空
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME);
                ContactBeen phoneDto = new ContactBeen(contactName, phoneNumber);
                phoneDtos.add(phoneDto);
            }
            phoneCursor.close();
//            MyToast.show(getContext(), phoneDtos.toString());
        }
    }

}
