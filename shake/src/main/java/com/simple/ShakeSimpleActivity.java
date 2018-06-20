package com.simple;


import android.os.Bundle;
import android.widget.TextView;

import com.shake.BaseShakeActivity;
import com.shake.R;

public class ShakeSimpleActivity extends BaseShakeActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        textView = (TextView) findViewById(R.id.textView);
    }

    protected void onResume(){
        super.onResume();
    }
}
