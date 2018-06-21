package com.xman.view.Log;

import android.text.TextUtils;

import com.xman.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class LogHelper {
    private String TAG = "LogHelper";
    private Builder mBuilder;

    private Process process;
    private boolean flag;

    private LogHelper(Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    private StringBuilder stringBuilder;

    public void start() {
        flag = true;
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
        } else {
            stringBuilder.setLength(0);
        }
        stringBuilder.append("logcat -s ");
        stringBuilder.append(mBuilder.tags);

        Observable.create(new ObservableOnSubscribe<PingResult>() {
            @Override
            public void subscribe(ObservableEmitter<PingResult> emitter) throws Exception {
                executePing(stringBuilder.toString(), emitter);
            }
        }).subscribe(new Observer<PingResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                Logger.d(TAG, "subscribe");
                if (onLogListener != null) {
                    onLogListener.onStart();
                }
            }

            @Override
            public void onNext(PingResult value) {
                Logger.d(TAG, "onNext" + value.successState + (onLogListener == null));
                if (onLogListener == null) {
                    return;
                }
                if (value.successState) {
                    Logger.d(TAG, "listener:" + value.result);
                    onLogListener.onSucceed(value.result);
                } else {
                    onLogListener.onError(value.result);
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.d(TAG, "error");
            }

            @Override
            public void onComplete() {
                Logger.d(TAG, "complete");
                if (onLogListener != null) {
                    onLogListener.onStop();
                    stop();
                }
            }
        });
    }

    public void stop() {
        if (process != null) {
            process.destroy();
        }

        flag = false;
    }

    private void executePing(String command, ObservableEmitter<PingResult> emitter) {
        BufferedReader successReader = null;
        BufferedReader errorReader = null;

        PingResult pingResult;
        try {
            process = Runtime.getRuntime().exec(command, null, null);
            InputStream in = process.getInputStream();

            successReader = new BufferedReader(new InputStreamReader(in));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String lineStr;
            while ((lineStr = successReader.readLine()) != null && flag) {
                Logger.d(TAG, "lineStr:" + lineStr);
                if (!TextUtils.isEmpty(lineStr)) {
                    pingResult = new PingResult();
                    pingResult.result = lineStr;
                    pingResult.successState = true;
                    emitter.onNext(pingResult);
                }
            }

            while ((lineStr = errorReader.readLine()) != null && flag) {
                Logger.d(TAG, "lineStr:" + lineStr);
                pingResult = new PingResult();
                pingResult.result = lineStr;
                pingResult.successState = false;
                emitter.onNext(pingResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (successReader != null) {
                    successReader.close();
                }
                if (errorReader != null) {
                    errorReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
//                process.destroyForcibly();
                process = null;
            }
            emitter.onComplete();
        }
    }

    private class PingResult {
        private String result;
        private boolean successState;
    }

    private OnLogListener onLogListener;

    public void setOnPingListener(OnLogListener onListener) {
        this.onLogListener = onListener;
    }

    public interface OnLogListener {
        /**
         * 开始ping,工作在主线程
         */
        void onStart();

        /**
         * 成功返回一条信息
         *
         * @param succeedStr
         */
        void onSucceed(String succeedStr);

        /**
         * 失败返回一条信息
         *
         * @param errorStr
         */
        void onError(String errorStr);

        /**
         * ping 停止
         */
        void onStop();
    }

    public static class Builder {
        private String tags = "";
        /**
         * 单位s
         */
        private int time = 3;

        public Builder tags(String tag) {
            this.tags = tag;
            return this;
        }

        public LogHelper build() {
            return new LogHelper(this);
        }
    }
}