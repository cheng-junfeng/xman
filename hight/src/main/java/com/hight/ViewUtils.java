package com.hight;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

class ViewUtils {

    private ViewUtils() {
    }

    /**
     * 获取最顶层视图
     *
     * @param activity {@link Activity}
     * @return 返回最顶层视图
     */
    @SuppressWarnings("unused")
    static ViewGroup getDeCorView(@NonNull Activity activity) {
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    /**
     * 获取内容区域根视图
     *
     * @param activity {@link Activity}
     * @return 返回内容区域根视图
     */
    private static ViewGroup getRootView(@NonNull Activity activity) {
        return (ViewGroup) activity.findViewById(android.R.id.content);
    }

    /**
     * 在整个窗体上面增加一层布局
     *
     * @param activity       {@link Activity}
     * @param layoutId       layoutId 布局id
     * @param clickLisstener {@link OnViewClickListener} 监听对象
     */
    static void addView(@NonNull final Activity activity, @LayoutRes int layoutId,
                        @NonNull final OnViewClickListener clickLisstener) {
        final View view = View.inflate(activity, layoutId, null);
        FrameLayout frameLayout = (FrameLayout) getRootView(activity);
        frameLayout.addView(view);

        // 设置整个布局的单击监听
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(activity, view);
                clickLisstener.onClick(view);
            }
        });
    }

    /**
     * 移除View
     *
     * @param activity {@link Activity}
     * @param view     需要移出的视图
     */
    private static void removeView(@NonNull Activity activity, @NonNull View view) {
        FrameLayout frameLayout = (FrameLayout) getRootView(activity);
        frameLayout.removeView(view);
    }

    /**
     * 获取子View在父View中的位置
     *
     * @param parent 父View
     * @param child  子View
     * @return Rect对象
     */
    static Rect getLocationInView(View parent, View child) {
        if (child == null || parent == null) {
            throw new IllegalArgumentException(
                    "parent and child can not be null .");
        }

        View decorView = null;
        Context context = child.getContext();
        if (context instanceof Activity) {
            decorView = ((Activity) context).getWindow().getDecorView();
        }

        Rect result = new Rect();
        Rect tmpRect = new Rect();

        View tmp = child;

        if (child == parent) {
            child.getHitRect(result);
            return result;
        }

        String noSaveStateFrameLayout = "android.support.v4.app.NoSaveStateFrameLayout";
        while (tmp != decorView && tmp != parent) {
            tmp.getHitRect(tmpRect);
            if (!noSaveStateFrameLayout.equals(tmp.getClass().getName())) {
                result.left += tmpRect.left;
                result.top += tmpRect.top;
            }
            tmp = (View) tmp.getParent();
        }

        result.right = result.left + child.getMeasuredWidth();
        result.bottom = result.top + child.getMeasuredHeight();
        return result;
    }

    /**
     * 单击视图监听，用于多个引导页面时连续调用
     */
    interface OnViewClickListener {
        /**
         * 单击监听回调
         *
         * @param view 点击的View
         */
        void onClick(View view);
    }
}
