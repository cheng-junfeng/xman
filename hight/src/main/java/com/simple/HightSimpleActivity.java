package com.simple;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hight.HighLight;
import com.hight.R;

public class HightSimpleActivity extends Activity {

    private final static String TAG = "HightSimpleActivity";
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hight);

        view = findViewById(R.id.tip2);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                addHightView1();
            }
        });
    }

    private void addHightView1() {
        HighLight highLight = new HighLight(this)
                .anchor(findViewById(R.id.id_container)) //绑定根布局，在Activity中可不写
                .setIntercept(true) // 查看注释和代码，可设置其他属性
                .setShadow(false)
                .setIsNeedBorder(true)
                .setShadow(false)
                .setOnClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        Log.d(TAG, "click 1");
                        addHightView2();
                    }
                })
                .setBroderLineType(HighLight.BorderLineType.DASH_LINE)
                .addHighLight(R.id.tip1, R.layout.hight_tip1_down, new HighLight.OnPosCallback() {
                    /**
                     * @param rightMargin
                     *            高亮view在anchor中的右边距
                     * @param bottomMargin
                     *            高亮view在anchor中的下边距
                     * @param rectF
                     *            高亮view的l,t,r,b,w,h都有
                     * @param marginInfo
                     *            设置你的布局的位置，一般设置l,t或者r,b
                     */
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {

                        Log.d(TAG, "rightMargin" + rightMargin);
                        Log.d(TAG, "rectF.width()" + rectF.width());
                        Log.d(TAG, "rectF.height()" + rectF.height());
                        Log.d(TAG, "bottomMargin" + bottomMargin);
                        marginInfo.rightMargin = rightMargin + rectF.width() / 2;
                        marginInfo.bottomMargin = bottomMargin + rectF.height();
                    }
                });

        highLight.show();
    }

    private void addHightView2() {
        // 使用默认的设置
        HighLight highLight = new HighLight(this)
                .anchor(findViewById(R.id.id_container)) //绑定根布局，在Activity中可不写
                .setIntercept(true) // 查看注释和代码，可设置其他属性
                .setShadow(false)
                .setIsNeedBorder(true)
                .setShadow(false)
                .setOnClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        Log.d(TAG, "click 2");
                        addHightView3();
                    }
                })
                .setBroderLineType(HighLight.BorderLineType.DASH_LINE)
                .addHighLight(R.id.tip2, R.layout.hight_tip2_up, new HighLight.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.leftMargin = rectF.right - rectF.width() / 2;
                        marginInfo.topMargin = rectF.bottom;
                    }
                });
        highLight.show();
    }

    private void addHightView3() {
        // 使用默认的设置
        HighLight highLight = new HighLight(this)
                .setBroderLineType(HighLight.BorderLineType.FULL_LINE) // 使用实线
                .setOnClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        Log.d(TAG, "click 3");
                    }
                })
                .addHighLight(R.id.tip3, R.layout.hight_tip3_center, new HighLight.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.rightMargin = rightMargin;
                        marginInfo.bottomMargin = bottomMargin + 2*(view.getHeight());
                    }
                }, HighLight.HightLightShape.CIRCULAR);// 圆形高亮
        highLight.show();
    }
}
