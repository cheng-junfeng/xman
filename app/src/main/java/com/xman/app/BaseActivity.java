package com.xman.app;

import android.content.Intent;
import android.os.Bundle;

import com.swipeback.SwipeBackActivity;
import com.xman.R;

import butterknife.ButterKnife;

public abstract class BaseActivity extends SwipeBackActivity {

    protected abstract int setContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        super.onCreate(savedInstanceState);
        setContentView(setContentView());
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    protected void readGo(Class<?> cls) {
        readGo(cls, null);
    }

    protected void readGo(Class<?> cls, Bundle bundle) {
        Intent inten = new Intent(this, cls);
        if (bundle != null) {
            inten.putExtras(bundle);
        }
        startActivity(inten);
    }

    protected void readGoForResult(Class<?> cls, int requestCode) {
        Intent inten = new Intent(this, cls);
        startActivityForResult(inten, requestCode);
    }

    protected void readGoForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent inten = new Intent(this, cls);
        if (bundle != null) {
            inten.putExtras(bundle);
        }
        startActivityForResult(inten, requestCode);
    }
}
