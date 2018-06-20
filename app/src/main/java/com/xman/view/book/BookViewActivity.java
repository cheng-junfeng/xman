package com.xman.view.book;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xman.R;
import com.xman.app.bean.Book;
import com.xman.net.BaseAsyncHttp;
import com.xman.net.FileDownloadHandler;
import com.xman.net.HttpResponseHandler;
import com.xman.widget.CircularProgressView;
import com.xman.widget.PromotedActionsLibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


public class BookViewActivity extends Activity {


    private TextView mTvRate, mTvPrice, mTvAuthor, mTvPublisher, mTvDate, mTvIsbn, mTvSummary, mTvPage, mTvtags, mTvContent;
    private ImageView mIvIcon;

    private Book mBook;

    private RelativeLayout mRlAnnotation;
    private LinearLayout mLlIntro, mLlMulu;
    private String isbn;

    private CircularProgressView progressView;
    private Thread updateThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_bookview);
        initBtn();
        findViews();
        if (getIntent().hasExtra("book")) {
            mBook = getIntent().getParcelableExtra("book");
            updateToView();
        } else if (getIntent().hasExtra("isbn")) {

            isbn = getIntent().getStringExtra("isbn");
            getRequestData(isbn);
        }

        mRlAnnotation = findViewById(R.id.rl_review);
        mRlAnnotation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(BookViewActivity.this, AnnotationListActivity.class);
                        intent.putExtra("id", mBook.getId());
                        intent.putExtra("name", mBook.getTitle());
                        startActivity(intent);
                    }
                }, 800);
            }
        });

    }

    private void findViews() {
        progressView = findViewById(R.id.progress_view);

        mTvAuthor = findViewById(R.id.tv_book_author);
        mTvPublisher = findViewById(R.id.tv_book_publicer);
        mTvDate = findViewById(R.id.tv_book_time);
        mTvIsbn = findViewById(R.id.tv_book_isbn);
        mTvRate = findViewById(R.id.tv_book_score);
        mTvPrice = findViewById(R.id.tv_book_price);
        mTvPage = findViewById(R.id.tv_book_page);
        mTvtags = findViewById(R.id.tv_book_tag);
        mIvIcon = findViewById(R.id.iv_book_icon);
        mTvSummary = findViewById(R.id.tv_book_intro_content);
        mTvContent = findViewById(R.id.tv_book_mulu_content);
        mLlIntro = findViewById(R.id.ll_book_intro);
        mLlMulu = findViewById(R.id.ll_book_mulu);
    }

    public void getRequestData(String isbn) {
        startAnimationThreadStuff(100);
        RequestParams params = new RequestParams();
        BaseAsyncHttp.getReq("/v2/book/isbn/" + isbn, params, new HttpResponseHandler() {
            @Override
            public void jsonSuccess(JSONObject resp) {
                progressView.setVisibility(View.GONE);
                mBook = new Book();
                mBook.setId(resp.optString("id"));
                mBook.setRate(resp.optJSONObject("rating").optDouble("average"));
                mBook.setReviewCount(resp.optJSONObject("rating").optInt("numRaters"));
                String authors = "";
                for (int j = 0; j < resp.optJSONArray("author").length(); j++) {
                    authors = authors + " " + resp.optJSONArray("author").optString(j);
                }
                mBook.setAuthor(authors);
                String tags = "";
                for (int j = 0; j < resp.optJSONArray("tags").length(); j++) {
                    tags = tags + " " + resp.optJSONArray("tags").optJSONObject(j).optString("name");
                }
                mBook.setTag(tags);
                mBook.setAuthorInfo(resp.optString("author_intro"));
                mBook.setBitmap(resp.optString("image"));
                mBook.setId(resp.optString("id"));
                mBook.setTitle(resp.optString("title"));
                mBook.setPublisher(resp.optString("publisher"));
                mBook.setPublishDate(resp.optString("pubdate"));
                mBook.setISBN(resp.optString("isbn13"));
                mBook.setSummary(resp.optString("summary"));
                mBook.setPage(resp.optString("pages"));
                mBook.setPrice(resp.optString("price"));
                mBook.setContent(resp.optString("catalog"));
                mBook.setUrl(resp.optString("ebook_url"));
                updateToView();
            }

            @Override
            public void jsonFail(JSONObject resp) {
                progressView.setVisibility(View.GONE);
                if (resp == null || resp.optInt("code") == 6000) {
                    Toast.makeText(BookViewActivity.this, "没有找到该图书或者不是图书的二维码", Toast.LENGTH_LONG).show();
//                    finish();
                }
            }
        });
    }

    private void updateToView() {
        mTvAuthor.setText(mBook.getAuthor().trim());
        mTvPublisher.setText(mBook.getPublisher());
        mTvDate.setText(mBook.getPublishDate());
        mTvIsbn.setText(mBook.getISBN());
        mTvRate.setText(mBook.getRate() + "分");
        mTvPrice.setText(mBook.getPrice());
        mTvPage.setText(mBook.getPage() + "页");
        if (mBook.getSummary().trim().equals(""))
            mLlIntro.setVisibility(View.GONE);
        else
            mTvSummary.setText(mBook.getSummary());
        if (mBook.getContent().trim().equals(""))
            mLlMulu.setVisibility(View.GONE);
        else
            mTvContent.setText(mBook.getContent());
        mTvtags.setText(mBook.getTag().equals("") ? "无标签" : mBook.getTag());
        ImageLoader.getInstance().displayImage(mBook.getBitmap(), mIvIcon);

        this.getActionBar().setTitle(mBook.getTitle());
    }

    private void initBtn() {
        FrameLayout frameLayout = findViewById(R.id.container);
        PromotedActionsLibrary promotedActionsLibrary = new PromotedActionsLibrary();
        promotedActionsLibrary.setup(getApplicationContext(), frameLayout);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        promotedActionsLibrary.addItem(getResources().getDrawable(R.drawable.weibo), R.drawable.weibo_back, new OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] allowedContentTypes = new String[]{"image/png", "image/jpeg"};
                BaseAsyncHttp.downloadFile(mBook.getBitmap(), new FileDownloadHandler(allowedContentTypes) {
                    @Override
                    public void DownSuccess() {
                        DownFail();
                    }

                    @Override
                    public void DownFail() {
                        Toast.makeText(BookViewActivity.this, "分享失败：download picture fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        promotedActionsLibrary.addItem(getResources().getDrawable(R.drawable.weixin), R.drawable.weixin_back, new OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] allowedContentTypes = new String[]{"image/png", "image/jpeg"};
                BaseAsyncHttp.downloadFile(mBook.getBitmap(), new FileDownloadHandler(allowedContentTypes) {
                    @Override
                    public void DownSuccess() {
                        DownFail();
                    }

                    @Override
                    public void DownFail() {
                        Toast.makeText(BookViewActivity.this, "分享失败：download picture fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        promotedActionsLibrary.addItem(getResources().getDrawable(R.drawable.timeline), R.drawable.timeline_back, new OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] allowedContentTypes = new String[]{"image/png", "image/jpeg"};
                BaseAsyncHttp.downloadFile(mBook.getBitmap(), new FileDownloadHandler(allowedContentTypes) {
                    @Override
                    public void DownSuccess() {
                        DownFail();
                    }

                    @Override
                    public void DownFail() {
                        Toast.makeText(BookViewActivity.this, "分享失败：download picture fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        promotedActionsLibrary.addMainItem(getResources().getDrawable(R.drawable.share), R.drawable.btn_back);
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

    private void startAnimationThreadStuff(long delay) {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressView.setVisibility(View.VISIBLE);
                progressView.setProgress(0f);
                progressView.startAnimation(); // Alias for resetAnimation, it's all the same
                updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressView.getProgress() < progressView.getMaxProgress() && !Thread.interrupted()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressView.setProgress(progressView.getProgress() + 10);
                                }
                            });
                            SystemClock.sleep(250);
                        }
                    }
                });
                updateThread.start();
            }
        }, delay);
    }
}
