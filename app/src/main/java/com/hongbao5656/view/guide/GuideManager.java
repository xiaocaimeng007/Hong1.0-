package com.hongbao5656.view.guide;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import com.hongbao5656.util.GuideReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
/**
 * Created by cz on 15/9/17.
 * //引导管理对象
 * 自定义维护一个配置配,管理所有引导是否生效
 */
public class GuideManager {
    private static final String DEFAULT_PREFERENCE = "guide";
    public static final GuideManager instance = new GuideManager();
    public final HashMap<String, ArrayList<GuideItem>> mGuideItems;

    public static final int DEFAULT_DURATION = 3 * 1000;

    public GuideManager() {
        mGuideItems = new HashMap<>();
        HashMap<String, ArrayList<GuideItem>> guideItems = new GuideReader().read();
        if (null != guideItems && !guideItems.isEmpty()) {
            mGuideItems.putAll(guideItems);
        }
    }

    public static GuideManager get() {
        return instance;
    }

    /**
     * 显示指定对象引导记录
     *
     * @param object 任一引导对象
     * @param activity 引导界面所属activity
     */
    public void show(Object object, Activity activity) {
        String name = object.getClass().getSimpleName();
        if (null == activity || !needGuide(activity, name)) return;//如果不需要引导,则退出,自动维护的首次引导提示
        ArrayList<GuideItem> guideItems = mGuideItems.get(name);
        if (null != guideItems && !guideItems.isEmpty()) {
            ViewGroup root = (ViewGroup) activity.getWindow().getDecorView();
            int size = guideItems.size();
            final HashMap<View, GuideItem> items = new HashMap<>();
            for (int i = 0; i < size; i++) {
                GuideItem item = guideItems.get(i);
                View findView = findView(root, item);
                if (null != findView) {
                    items.put(findView, item);
                }
            }
            if (!items.isEmpty()) {
                //创建布局画布出来
                GuideView guideView = new GuideView(activity);
                guideView.setId(name.hashCode());
                guideView.setGuideItems(items);
                root.addView(guideView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                com.nineoldandroids.view.ViewHelper.setAlpha(guideView, 0f);
                ViewPropertyAnimator.animate(guideView).alpha(1f);
                guideView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss(root, guideView);
                    }
                });
            }
        }
    }

    /**
     * 隐藏蒙板
     *
     * @param object 任一引导对象
     * @param activity 引导界面所属activity
     */
    public void hide(Object object, Activity activity) {
        String name = object.getClass().getSimpleName();
        if (null == activity || !needGuide(activity, name)) return;//如果不需要引导,则退出,自动维护的首次引导提示
        ArrayList<GuideItem> guideItems = mGuideItems.get(name);
        if (null != guideItems && !guideItems.isEmpty()) {
            ViewGroup root = (ViewGroup) activity.getWindow().getDecorView();
            View findView = root.findViewById(name.hashCode());
            if (null != findView) {
                dismiss(root, findView);
            }
        }
    }

    private ViewPropertyAnimator dismiss(ViewGroup root, View view) {
        return ViewPropertyAnimator.animate(view).alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                root.removeView(view);
            }
        });
    }


    /**
     * 查找控件位置
     *
     * @param root
     * @param item
     * @return
     */
    private View findView(ViewGroup root, GuideItem item) {
        View findView = null;
        if (0 != item.list) {
            ViewGroup findList = (ViewGroup) root.findViewById(item.list);
            if (null != findList) {
                findView = findList.getChildAt(item.child);
                if (null != findView && 0 != item.id) {
                    findView = findView.findViewById(item.id);
                }
            }
        } else {
            findView = root.findViewById(item.id);
        }
        return findView;
    }

    /**
     * 界面是否需要引导
     *
     * @param context
     * @param name
     * @return
     */
    private boolean needGuide(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return !preferences.getBoolean(name, false);
    }

    /**
     * 设置初始化引导
     *
     * @param context
     * @param name
     */
    private void setGuide(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(name, true).commit();
    }

    /**
     * 清空所有引导记录
     *
     * @param context
     */
    public void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        Map<String, ?> all = preferences.getAll();
        if (!all.isEmpty()) {
            SharedPreferences.Editor editor = preferences.edit();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String name = entry.getKey();
                editor.putBoolean(name, false);
            }
        }
    }
}
