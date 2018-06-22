package com.xman.ui.book;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.xman.R;
import com.xman.app.BaseActivity;
import com.xman.ui.book.adapter.AnnotationAdapter;
import com.xman.ui.book.bean.Annotation;
import com.xman.net.BaseAsyncHttp;
import com.xman.net.HttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 实现 豆瓣 api 的搜索
 * 1 android-Asynchttp 异步网络请求 douban.api ，笔记信息
 * 2 SwipeRefreshLayout 下拉刷新的列表
 * 3 Adapter中 的 CircleImageView 圆形图片
 */
public class AnnotationListActivity extends BaseActivity {

    @BindView(R.id.lv_annotation)
    ListView mLvAnnotation;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSrLayout;

    private List<Annotation> mAnnotations = new ArrayList<>();
    private AnnotationAdapter mAdapter;
    private String bookid, bookname;

    private int hasNum = 0; //已经加载的数量

    @Override
    protected int setContentView() {
        return R.layout.activity_annotation;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookid = getIntent().getStringExtra("id");
        bookname = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle("《" + bookname + "》的笔记");

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
                }

                mAdapter.setList(mAnnotations);
                mAdapter.notifyDataSetChanged();
                if (hasNum == resp.optInt("total")) {
                    Toast.makeText(AnnotationListActivity.this, "没有更多的读书笔记", Toast.LENGTH_SHORT).show();
                }
                hasNum = mAnnotations.size();
                mSrLayout.setRefreshing(false);
            }

            @Override
            public void jsonFail(JSONObject resp) {
                Toast.makeText(AnnotationListActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
