package com.xman.ui.email;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.xman.R;
import com.xman.app.BaseActivity;
import com.xman.utils.Logger;

import javax.mail.MessagingException;

import butterknife.OnClick;


/**
 * 实现 邮件的发送
 * 1 分别采用 Util工具类型，和Bean实体类型
 * 2 注意要打开发件邮箱的POP3/IMAP/SMTP服务，不然会认证失败
 * 3 使用账户+授权码的方式发送
 * 4 邮箱发送涉及网络，在子线程中进行
 */
public class MailActivity extends BaseActivity {
    private final static String TAG = "MailActivity";
    Handler handler1;

    @Override
    protected int setContentView() {
        return R.layout.activity_mail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread handlerThread = new HandlerThread("mail");
        handlerThread.start();
        handler1 = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // 处理从子线程发送过来的消息
                Logger.d(TAG, "Current:" + Thread.currentThread().getName());
                switch (msg.what) {
                    case 1:
                        try {
                            new MailUtil().sendMail("android test");
                        } catch (MessagingException e) {
                            Logger.d(TAG, "exception:" + e.toString());
                        }
                        break;
                    case 2:
                        send();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @OnClick({R.id.mail_send, R.id.mail_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mail_send:
                Message msg1 = new Message();
                msg1.what = 1;
                handler1.sendMessage(msg1);
                break;
            case R.id.mail_more:
                Message msg2 = new Message();
                msg2.what = 2;
                handler1.sendMessage(msg2);
                break;
        }
    }

    private void send() {
        try {
            MailBean m = new MailBean("jun18320786746@163.com", "0000");//自己的授权码
            String[] toArr = {"sjun945@outlook.com"};
            m.set_to(toArr);
            m.set_from("jun18320786746@163.com");
            m.set_subject("Android 客户端测试邮件");
            m.set_body("Android 客户端测试链接");
            m.addAttachment("/storage/emulated/0/1/lbuilder773_1524820219.apk");
            if (m.send()) {
                Toast.makeText(this, "邮件发送成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "邮件发送失败", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Logger.d(TAG, "exception:" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler1 != null) {
            handler1.removeCallbacks(null);
        }
    }
}
