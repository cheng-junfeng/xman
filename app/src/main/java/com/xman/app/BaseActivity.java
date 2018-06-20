package com.xman.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;

public abstract class BaseActivity extends Activity {

    protected abstract int setContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentView());
        ButterKnife.bind(this);
    }

    protected void readGo(Class<?> cls){
        readGo(cls, null);
    }

    protected void readGo(Class<?> cls, Bundle bundle){
        Intent inten = new Intent(this, cls);
        if(bundle != null){
            inten.putExtras(bundle);
        }
        startActivity(inten);
    }

    protected void readGoForResult(Class<?> cls, int requestCode){
        Intent inten = new Intent(this, cls);
        startActivityForResult(inten, requestCode);
    }

    protected void readGoForResult(Class<?> cls, Bundle bundle, int requestCode){
        Intent inten = new Intent(this, cls);
        if(bundle != null){
            inten.putExtras(bundle);
        }
        startActivityForResult(inten, requestCode);
    }
}
