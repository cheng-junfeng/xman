package com.xman.ui.word;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xman.R;
import com.xman.ui.word.adapter.MyexpandableListAdapter;
import com.xman.ui.word.util.WordUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HtmlActivity extends Activity implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener {
    private final static String TAG = "HtmlActivity";
    @BindView(R.id.wv_content)
    WebView wv_content;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.expandablelist)
    ExpandableListView expandableListView;
    @BindView(R.id.searchBar)
    EditText searchBar;

    private MyexpandableListAdapter adapter;
    private ArrayList<String> groupList;
    private ArrayList<List<String>> childList;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ButterKnife.bind(this);
        mContext = this;


        InitData();
        adapter = new MyexpandableListAdapter(this, groupList, childList);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/2.docx";
        WordUtils wu = new WordUtils(path);
        Log.d(TAG, "htmlPath=" + wu.htmlPath);
        wv_content.loadUrl("file:///"+wu.htmlPath);

        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(mContext, "search", Toast.LENGTH_SHORT).show();

                    search(searchBar.getText().toString());
                    return true;
                } else {
                    Toast.makeText(mContext, "go", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
    }

    private void search(String str) {
        drawerLayout.closeDrawer(GravityCompat.END);
        Log.d("junfeng", "Find:" + str);
        wv_content.findAllAsync(str);
        wv_content.setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int position, int all, boolean b) {
//                            tv_position.setText("(位置："+(position+1)+"/"+all+")");
                Log.d("junfeng", "Find:" + all);
                if (all == 0) {
                    Toast.makeText(mContext,
                            "找不到匹配项", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /***
     * InitData
     */
    void InitData() {
        groupList = new ArrayList<String>();
        groupList.add("Ios");
        groupList.add("Android");
        groupList.add("Window");
        childList = new ArrayList<List<String>>();
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<String> childTemp;
            if (i == 0) {
                childTemp = new ArrayList<String>();
                childTemp.add("iphone 4");
                childTemp.add("iphone 5");
            } else if (i == 1) {
                childTemp = new ArrayList<String>();
                childTemp.add("Anycall");
                childTemp.add("HTC");
                childTemp.add("Motorola");
            } else {
                childTemp = new ArrayList<String>();
                childTemp.add("Lumia 800C ");
            }
            childList.add(childTemp);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onGroupClick(final ExpandableListView parent, final View v,
                                int groupPosition, final long id) {
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        search(childList.get(groupPosition).get(childPosition));
        return false;
    }

    @OnClick(R.id.tvType)
    public void onViewClicked() {
        search(searchBar.getText().toString());
    }
}
