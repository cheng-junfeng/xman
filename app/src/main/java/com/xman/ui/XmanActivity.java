package com.xman.ui;

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
import com.xman.ui.email.MailActivity;
import com.xman.ui.log.LogActivity;
import com.xman.ui.book.SearchActivity;

import java.lang.reflect.Field;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;


public class XmanActivity extends BaseActivity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_xman, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);// 不可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);// 去掉默认的返回箭头
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.actionbar_score:
                Uri uri = Uri.parse("market://details?id=" + "com.scanbook");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.rl_log, R.id.rl_search, R.id.rl_email, R.id.rl_more, R.id.rl_shake, R.id.rl_hight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_log:
                readGo(LogActivity.class);
                break;
            case R.id.rl_search:
                readGo(SearchActivity.class);
                break;
            case R.id.rl_email:
                readGo(MailActivity.class);
                break;
            case R.id.rl_more:
//                readGo(ShakeSimpleActivity.class);
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
