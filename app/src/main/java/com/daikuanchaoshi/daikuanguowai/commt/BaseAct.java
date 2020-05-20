package com.daikuanchaoshi.daikuanguowai.commt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.adjust.sdk.Adjust;
import com.daikuanchaoshi.daikuanguowai.R;
import com.lykj.aextreme.afinal.common.BaseActivity;

public abstract class BaseAct extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);
    }

    @SuppressLint("ResourceType")
    protected void setHeaderLeft(@DrawableRes int left) {
        if (left > 0) {
            if (toolbar.findViewById(R.id.head_vLeft) == null) {
                View v = View.inflate(context, R.layout.in_head_left, toolbar);
                ImageView img = getView(v, R.id.head_vLeft);
                img.setOnClickListener(this);
                img.setImageResource(left);
            } else {
                ImageView img = getView(toolbar, R.id.head_vLeft);
                img.setImageResource(left);
            }
        }
    }

    protected void onResume() {
        super.onResume();
        Adjust.onResume();
    }

    protected void onPause() {
        super.onPause();
        Adjust.onPause();
    }
}
