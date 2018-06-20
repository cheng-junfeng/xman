package com.xman.view.book;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xman.R;
import com.xman.widget.MaterialDialog;


public class AboutActivity extends Activity {

    private RelativeLayout mRlAuthor,mRlBlog,mRlPro,mRlUpdate;
    private MaterialDialog mMaterialDialog;
    private TextView mTvVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mRlAuthor= findViewById(R.id.rl_about_author);
        mRlBlog= findViewById(R.id.rl_about_blog);
        mRlPro= findViewById(R.id.rl_about_pro);
        mRlUpdate= findViewById(R.id.rl_about_update);
        mTvVersion= findViewById(R.id.tv_about_version);
        mRlAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog = new MaterialDialog(AboutActivity.this);
                mMaterialDialog.setTitle("关于作者")
                        .setMessage(
                                "花名：Jay.Fang\n" +
                                        "博客：http://fangjie.info\n" +
                                        "Weibo：@方杰_Jay\n" +
                                        "Email：JayFang1993#gmail\n"
                        )
                        .setPositiveButton(
                                "去TA博客", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse("http://fangjie.info");
                                        intent.setData(content_url);
                                        startActivity(intent);
                                    }
                                }
                        )
                        .setNegativeButton(
                                "算了", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                }
                        )
                        .setCanceledOnTouchOutside(false)
                        .show();

            }
        });
        mRlBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://fangjie.info");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        mRlPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog = new MaterialDialog(AboutActivity.this);
                mMaterialDialog.setTitle("GitHub")
                        .setMessage(
                                " 别忘了star项目哦 ^ ^"
                        )
                        .setPositiveButton(
                                "去GitHub", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse("http://github.com/JayFang1993/ScanBook");
                                        intent.setData(content_url);
                                        startActivity(intent);
                                    }
                                }
                        )
                        .setNegativeButton(
                                "算了", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                }
                        )
                        .setCanceledOnTouchOutside(false)
                        .show();
            }
        });
        mRlUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setHomeButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
