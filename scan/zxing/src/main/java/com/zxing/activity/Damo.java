package com.zxing.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zxing.R;
import com.zxing.utils.ScanUtil;

/**
 * Title:
 * description:
 * autor:pei
 * created on 2020/4/29
 */
public class Damo extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dame);

        ScanUtil.i("========我是demo log======");
    }
}
