package com.xman.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.simple.HightSimpleActivity;
import com.simple.ShakeSimpleActivity;
import com.xman.R;
import com.xman.app.BaseActivity;
import com.xman.view.book.AboutActivity;
import com.xman.view.book.BookViewActivity;
import com.xman.view.book.SearchActivity;

import java.lang.reflect.Field;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;


public class XmanActivity extends BaseActivity {
    private final static int REQUEST_CODE = 100;

    @BindView(R.id.iv_main_back)
    ImageView ivMainBack;

    @Override
    protected int setContentView() {
        return R.layout.activity_xman;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Random random = new Random();
        int i = Math.abs(random.nextInt()) % 5 + 1;
        try {
            Field field = R.drawable.class.getField("main_back" + i);
            int j = field.getInt(new R.drawable());
            ivMainBack.setBackgroundResource(j);
        } catch (Exception e) {
            ivMainBack.setBackgroundResource(R.drawable.main_back1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (((requestCode == REQUEST_CODE) || (resultCode == Activity.RESULT_OK)) && data != null) {
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
                readGo(AboutActivity.class);
                intent = new Intent(XmanActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.actionbar_score:
                Uri uri = Uri.parse("market://details?id=" + "com.scanbook");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @OnClick({R.id.rl_more, R.id.rl_search, R.id.rl_shake, R.id.rl_hight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_more:
                break;
            case R.id.rl_search:
                readGoForResult(SearchActivity.class, REQUEST_CODE);
                break;
            case R.id.rl_shake:
                readGo(ShakeSimpleActivity.class);
                break;
            case R.id.rl_hight:
                readGo(HightSimpleActivity.class);
                break;
        }
    }
}
