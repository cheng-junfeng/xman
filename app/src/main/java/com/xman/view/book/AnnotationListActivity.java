package com.xman.view.book;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.xman.R;
import com.xman.app.adapter.AnnotationAdapter;
import com.xman.app.bean.Annotation;
import com.xman.net.BaseAsyncHttp;
import com.xman.net.HttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;


public class AnnotationListActivity extends Activity {

    private ListView mLvAnnotation;
    private List<Annotation> mAnnotations = new ArrayList<Annotation>();
    private AnnotationAdapter mAdapter;
    private String bookid, bookname;
    private SwipeRefreshLayout mSrLayout;

    private int hasNum = 0; //已经加载的数量

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        bookid = getIntent().getStringExtra("id");
        bookname = getIntent().getStringExtra("name");
        this.getActionBar().setTitle("《" + bookname + "》的笔记");

        mSrLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        mSrLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hasNum = 0;
                reqAnnotationList(0, 20);
            }
        });
        mSrLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mLvAnnotation = (ListView) findViewById(R.id.lv_annotation);
        mLvAnnotation.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (mLvAnnotation.getLastVisiblePosition() == (mLvAnnotation.getCount() - 1)) {
                            mSrLayout.setRefreshing(true);
                            reqAnnotationList(hasNum, 20);
                        }
                        break;
                }
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hasNum = 0;
                mSrLayout.setRefreshing(true);
                reqAnnotationList(0, 20);
            }
        }, 200);
        mAdapter = new AnnotationAdapter(AnnotationListActivity.this, mAnnotations);
        mLvAnnotation.setAdapter(mAdapter);
    }

    private void reqAnnotationList(int start, int count) {
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);
        BaseAsyncHttp.getReq("/v2/book/" + bookid + "/annotations", params, new HttpResponseHandler() {
            @Override
            public void jsonSuccess(JSONObject resp) {
                if (hasNum == 0)
                    mAnnotations.clear();
                JSONArray jsons = resp.optJSONArray("annotations");
                for (int i = 0; i < jsons.length(); i++) {
                    Annotation annotation = new Annotation();
                    annotation.setAuthor(jsons.optJSONObject(i).optJSONObject("author_user").optString("name"));
                    annotation.setAuthorHead(jsons.optJSONObject(i).optJSONObject("author_user").optString("avatar"));
                    annotation.setAbstract(jsons.optJSONObject(i).optString("abstract"));
                    annotation.setCheapter(jsons.optJSONObject(i).optString("chapter"));
                    annotation.setContent(jsons.optJSONObject(i).optString("content"));
                    annotation.setPage(jsons.optJSONObject(i).optInt("page_no"));
                    annotation.setTime(jsons.optJSONObject(i).optString("time"));
                    mAnnotations.add(annotation);
                }
                if (mAnnotations.size() == 0) {
                    Toast.makeText(AnnotationListActivity.this, "没有发现本书的读书笔记", Toast.LENGTH_SHORT).show();
                    AnnotationListActivity.this.finish();
                }

                mAdapter.setList(mAnnotations);
                mAdapter.notifyDataSetChanged();
                if (hasNum == resp.optInt("total"))
                    Toast.makeText(AnnotationListActivity.this, "没有更多的读书笔记", Toast.LENGTH_SHORT).show();
                hasNum = mAnnotations.size();
                mSrLayout.setRefreshing(false);

            }

            @Override
            public void jsonFail(JSONObject resp) {
                Toast.makeText(AnnotationListActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
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
