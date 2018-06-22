package com.xman.view.book;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xman.R;
import com.xman.app.BaseActivity;
import com.xman.app.bean.Book;
import com.xman.net.BaseAsyncHttp;
import com.xman.net.FileDownloadHandler;
import com.xman.net.HttpResponseHandler;
import com.xman.widget.CircularProgressView;
import com.xman.widget.PromotedActionsLibrary;
import com.xman.widget.RippleView;

import org.json.JSONObject;

import butterknife.BindView;


public class BookViewActivity extends BaseActivity {


    @BindView(R.id.iv_book_icon)
    ImageView mIvIcon;
    @BindView(R.id.tv_book_author)
    TextView mTvAuthor;
    @BindView(R.id.tv_book_time)
    TextView mTvDate;
    @BindView(R.id.tv_book_page)
    TextView mTvPage;
    @BindView(R.id.tv_book_publicer)
    TextView mTvPublisher;
    @BindView(R.id.tv_book_isbn)
    TextView mTvIsbn;
    @BindView(R.id.tv_book_price)
    TextView mTvPrice;
    @BindView(R.id.tv_book_score)
    TextView mTvRate;
    @BindView(R.id.tv_book_tag)
    TextView mTvtags;
    @BindView(R.id.rl_review)
    RippleView mRlAnnotation;
    @BindView(R.id.tv_book_intro_content)
    TextView mTvSummary;
    @BindView(R.id.ll_book_intro)
    LinearLayout mLlIntro;
    @BindView(R.id.tv_book_mulu_content)
    TextView mTvContent;
    @BindView(R.id.ll_book_mulu)
    LinearLayout mLlMulu;
    @BindView(R.id.progress_view)
    CircularProgressView progressView;
    @BindView(R.id.container)
    FrameLayout frameLayout;

    private Book mBook;
    private String isbn;

    private Thread updateThread;

    @Override
    protected int setContentView() {
        return R.layout.activity_bookview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBtn();
        if (getIntent().hasExtra("book")) {
            mBook = getIntent().getParcelableExtra("book");
            updateToView();
        } else if (getIntent().hasExtra("isbn")) {

            isbn = getIntent().getStringExtra("isbn");
            getRequestData(isbn);
        }

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

        getSupportActionBar().setTitle(mBook.getTitle());
    }

    private void initBtn() {
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

        promotedActionsLibrary.addMainItem(getResources().getDrawable(R.drawable.share), R.drawable.btn_back);
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
