package com.bill.videoplayer.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.bill.videoplayer.app.AppAuxiliary;

import java.util.List;

/**
 * author ywb
 * date 2021/12/23
 * desc
 */
public class Utils {

    private static Toast toast;

    public static void toast(String msg) {
        if (toast == null)
            toast = Toast.makeText(AppAuxiliary.getInstance().getAppContext(), msg, Toast.LENGTH_SHORT);
        else
            toast.setText(msg);
        toast.show();
    }

    public static float dp2Px(float dp) {
        Context context = AppAuxiliary.getInstance().getAppContext();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth() {
        Context context = AppAuxiliary.getInstance().getAppContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        Context context = AppAuxiliary.getInstance().getAppContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static boolean isTopActivity(Activity activity) {
        return activity != null && isForeground(activity, activity.getClass().getName());
    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null)
            return false;
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(10);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
