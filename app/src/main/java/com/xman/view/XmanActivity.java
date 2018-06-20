package com.xman.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.simple.HightSimpleActivity;
import com.xman.R;
import com.xman.view.book.AboutActivity;
import com.xman.view.book.BookViewActivity;
import com.xman.view.book.CaptureActivity;
import com.xman.view.book.SearchActivity;
import com.xman.view.shake.ShakeActivity;
import com.xman.widget.MaterialDialog;

import java.lang.reflect.Field;
import java.util.Random;


public class XmanActivity extends Activity {
    private RelativeLayout mRlBtnScan, mRlBtnSearch;
    private MaterialDialog mMaterialDialog;
    private ImageView mIvBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xman);

        mRlBtnScan = (RelativeLayout) findViewById(R.id.rl_scan);
        mRlBtnSearch = (RelativeLayout) findViewById(R.id.rl_search);
        mIvBack = (ImageView) findViewById(R.id.iv_main_back);

        Random random = new Random();
        int i = Math.abs(random.nextInt()) % 5 + 1;
        try {
            Field field = R.drawable.class.getField("main_back" + i);
            int j = field.getInt(new R.drawable());
            mIvBack.setBackgroundResource(j);
        } catch (Exception e) {
            mIvBack.setBackgroundResource(R.drawable.main_back1);
        }

        mRlBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(XmanActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        mRlBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(XmanActivity.this, SearchActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (((requestCode == 100) || (resultCode == Activity.RESULT_OK)) && data != null) {
            String isbn = data.getStringExtra("result");
            Intent intent = new Intent(XmanActivity.this, BookViewActivity.class);
            intent.putExtra("isbn", isbn);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_xman, menu);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeButtonEnabled(false);// 不可点击
        getActionBar().setDisplayHomeAsUpEnabled(false);// 去掉默认的返回箭头
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.actionbar_aboutme:
                intent = new Intent(XmanActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.actionbar_shake:
                intent = new Intent(XmanActivity.this, ShakeActivity.class);
                startActivity(intent);
                break;
            case R.id.actionbar_score:
                Uri uri = Uri.parse("market://details?id=" + "com.scanbook");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.actionbar_hight:
                intent = new Intent(XmanActivity.this, HightSimpleActivity.class);
                startActivity(intent);
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
