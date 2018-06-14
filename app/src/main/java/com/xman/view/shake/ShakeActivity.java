package com.xman.view.shake;


import android.os.Bundle;
import android.widget.TextView;

import com.xman.R;

public class ShakeActivity extends BaseActivity  {


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
