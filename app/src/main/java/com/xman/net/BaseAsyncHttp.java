package com.xman.net;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class BaseAsyncHttp extends AsyncHttpClient {

    public static final String HOST = "https://api.douban.com";

    public static void postReq(String host, String url, RequestParams params, JsonHttpResponseHandler hander) {
        new AsyncHttpClient().post(host + url, params, hander);
    }

    public static void postReq(String url, RequestParams params, JsonHttpResponseHandler hander) {
        Log.i("fangjie", HOST + url);
        new AsyncHttpClient().post(HOST + url, params, hander);
    }

    public static void getReq(String host, String url, RequestParams params, JsonHttpResponseHandler hander) {
        new AsyncHttpClient().get(host + url, params, hander);
    }

    public static void getReq(String url, RequestParams params, JsonHttpResponseHandler hander) {
        Log.i("fangjie", HOST + url);
        new AsyncHttpClient().get(HOST + url, params, hander);
    }

    public static void downloadFile(String url, FileDownloadHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, handler);
    }
}
