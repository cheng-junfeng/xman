package com.xman.ui.log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xman.R;
import com.xman.app.BaseActivity;
import com.xman.utils.Logger;
import com.xman.utils.TimeUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Android 4.1 以后，做了权限限制 Logcat的签名变为 “signature|system|development
 * 1 只有系统应用才能拿到 所有应用的日志
 * 2 Runtime.getRuntime().exec 与adb shell 的权限并不一样，给adb 的权限更多
 * 3 手机端在非root情况下，只能拿到自己应用的日志
 */
public class LogActivity extends BaseActivity {
    private final static String TAG = "LogActivity";

    @BindView(R.id.log_path)
    TextView logPath;
    @BindView(R.id.log_tx)
    TextView logTx;

    Handler handler1;
    LogHelper helper;

    @Override
    protected int setContentView() {
        return R.layout.activity_log;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "Current:" + Thread.currentThread().getName());
//        try {
//            ArrayList commnandList = new ArrayList();
//            commnandList.add("logcat -d -v time -f /mnt/sdcard/bugLog/logcat.txt");
//            ShellUtils.execCommand(commnandList, true);
//        } catch (Exception e) {
//            Logger.d(TAG, "Exception:" + e.getMessage());
//        }
    }

    String fileName = "path";

    @OnClick({R.id.log_out, R.id.log_share, R.id.log_shell})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.log_out:
                String result = "unkown";
                long current = System.currentTimeMillis();
                fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "log" + TimeUtil.millisecondsString(current) + ".log";
                try {
                    StringBuffer commandLine = new StringBuffer();
                    commandLine.append("logcat");
                    commandLine.append(" -d");//使用该参数可以让logcat获取日志完毕后终止进程
                    commandLine.append(" -v");
                    commandLine.append(" time");
                    commandLine.append(" -f");//如果使用commandLine.add(">");是不会写入文件，必须使用-f的方式
                    commandLine.append(" " + fileName);
                    Process process = Runtime.getRuntime().exec(commandLine.toString(), null, null);
                    result = "save " + fileName;
                    logPath.setText(fileName);
                } catch (Exception e) {
                    Logger.d(TAG, "Exception:" + e.getMessage());
                    result = "exception " + e.getMessage();
                } finally {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.log_share:
                String lastPath = logPath.getText().toString();
                if (!TextUtils.isEmpty(lastPath) && lastPath.endsWith(".log")) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(lastPath));
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Log分享"));
                } else {
                    Toast.makeText(this, "无日志文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.log_shell:
                HandlerThread handlerThread = new HandlerThread("logger");
                handlerThread.start();
                handler1 = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // 处理从子线程发送过来的消息
                        Logger.d(TAG, "Current:" + Thread.currentThread().getName());
                        helper = new LogHelper.Builder().tags("LogActivity").build();
                        helper.setOnPingListener(new LogHelper.OnLogListener() {
                            @Override
                            public void onStart() {
                                setLogStr("logcat -s LogActivity");
                            }

                            @Override
                            public void onSucceed(String succeedStr) {
                                setLogStr("success:" + succeedStr);
                            }

                            @Override
                            public void onError(String errorStr) {
                                setLogStr("error" + errorStr);
                            }

                            @Override
                            public void onStop() {
                                setLogStr("end");
                            }
                        });
                        helper.start();
                    }
                };
                handler1.sendMessage(new Message());
                setLogStr("start");
                break;
        }
    }

    private void setLogStr(final String strs) {
        Logger.d(TAG, "setLogStr:" + strs);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTx.append(strs + "\r\n");
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.stop();
        }
        if (handler1 != null) {
            handler1.removeCallbacks(null);
        }
    }
}
