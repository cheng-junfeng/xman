package com.shake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class MySensorEventListener implements SensorEventListener {

    private Shakeable mActivity;

    public MySensorEventListener(Shakeable mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //获取传感器类型
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        //如果传感器类型为加速度传感器，则判断是否为摇一摇
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
                    .abs(values[2]) > 17)) {
                Log.d("sensor x ", "============ values[0] = " + values[0]);
                Log.d("sensor y ", "============ values[1] = " + values[1]);
                Log.d("sensor z ", "============ values[2] = " + values[2]);
                //摇一摇的回调方法
                mActivity.onShake(values);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
