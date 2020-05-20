package com.lykj.aextreme.afinal.common;


import androidx.annotation.LayoutRes;

/**
 * @author HeYan
 */
public abstract interface IBaseActivity {
    @LayoutRes
    int initLayoutId();

    /**
     * 初始化控件
     */
    void initView();

    /**
     * 初始化数据
     */
    void initData();
    /**
     * 更新UI
     */
    void updateUI();
    void onNoInterNet();
}
