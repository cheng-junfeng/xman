package com.shake;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Toast;


public abstract class BaseShakeActivity extends Activity implements Shakeable {
    //传感器管理器
    protected SensorManager manager;
    //传感器监听器
    protected MySensorEventListener listener;
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);
        listener = new MySensorEventListener(this);
        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(listener,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    //摇一摇的回调方法
    public void onShake(Object... objs) {
        if (getShakeable()) {
            float[] values = (float[]) objs[0];
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 400, 100, 400};
            vibrator.vibrate(pattern, 2);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vibrator.cancel();
                }
            }, 1000);
            Toast.makeText(this, "x:" + values[0] + ",y:" + values[1] + ",z:" + values[2], Toast.LENGTH_SHORT).show();
        }
    }

    //如果某个页面不想处理摇一摇事件，将isShakeable设置为false即可
    protected boolean getShakeable() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterListener(listener);
        handler.removeCallbacks(null);
    }
}
