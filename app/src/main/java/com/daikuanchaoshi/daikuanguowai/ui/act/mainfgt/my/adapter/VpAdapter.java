package com.daikuanchaoshi.daikuanguowai.ui.act.mainfgt.my.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lykj.aextreme.afinal.common.BaseFragment;

import java.util.List;

public class VpAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragmentList;
    public VpAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
