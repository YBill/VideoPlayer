package com.bill.baseplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * author ywb
 * date 2021/11/26
 * desc
 */
public class Utils {

    public static final int NO_NETWORK = 0; // 无网络
    public static final int NETWORK_CLOSED = 1; // 网络断开或关闭
    public static final int NETWORK_ETHERNET = 2; // 以太网
    public static final int NETWORK_WIFI = 3; // wifi
    public static final int NETWORK_MOBILE = 4; // 手机网络
    public static final int NETWORK_UNKNOWN = -1; // unknown

    public static boolean isHasNet(Context context) {
        return isMobileNet(context) || isWiFi(context);
    }

    public static boolean isMobileNet(Context context) {
        int type = getNetworkType(context);
        return type == NETWORK_MOBILE;
    }

    public static boolean isWiFi(Context context) {
        int type = getNetworkType(context);
        return type == NETWORK_WIFI || type == NETWORK_ETHERNET;
    }

    /**
     * 获取当前网络类型
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectMgr == null) {
            return NO_NETWORK;
        }

        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            // 没有任何网络
            return NO_NETWORK;
        }
        if (!networkInfo.isConnected()) {
            // 网络断开或关闭
            return NETWORK_CLOSED;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            // 以太网网络
            return NETWORK_ETHERNET;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接
            return NETWORK_WIFI;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭
            switch (networkInfo.getSubtype()) {
                // 2G
                case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                case TelephonyManager.NETWORK_TYPE_EDGE: // 电信2g
                case TelephonyManager.NETWORK_TYPE_CDMA: // 移动2g
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    // 3G
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    // 4G
                case TelephonyManager.NETWORK_TYPE_LTE:
                    // 5G
                case TelephonyManager.NETWORK_TYPE_NR:
                    return NETWORK_MOBILE;
            }
        } else {
            String subtypeName = networkInfo.getSubtypeName();
            // 中国移动 联通 电信 三种3G制式
            if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                    || subtypeName.equalsIgnoreCase("WCDMA")
                    || subtypeName.equalsIgnoreCase("CDMA2000")) {
                return NETWORK_MOBILE; // 3G
            } else {
                MLog.d("NetworkType = " + subtypeName);
                return NETWORK_MOBILE;
            }
        }

        // 未知网络
        return NETWORK_UNKNOWN;
    }

    public static int getNetworkType2(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Android版本在6.0之前只能使用NetworkInfo获取网络状态
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        return NETWORK_WIFI;
                    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return NETWORK_MOBILE;
                    }
                }
            }
        } else {
            if (connectivityManager != null) {
                Network networks = connectivityManager.getActiveNetwork();
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(networks);
                if (networkCapabilities != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return NETWORK_WIFI;
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return NETWORK_MOBILE;
                    }
                }
            }
        }
        //无网络
        return NO_NETWORK;
    }

    /**
     * 获取Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context, boolean isIncludeNav) {
        if (isIncludeNav) {
            return context.getResources().getDisplayMetrics().widthPixels + getNavigationBarHeight(context);
        } else {
            return context.getResources().getDisplayMetrics().widthPixels;
        }
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context, boolean isIncludeNav) {
        if (isIncludeNav) {
            return context.getResources().getDisplayMetrics().heightPixels + getNavigationBarHeight(context);
        } else {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
    }


    /**
     * 获取NavigationBar的高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (!hasNavigationBar(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 是否存在NavigationBar
     */
    public static boolean hasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager(context).getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.x != size.x || realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    /**
     * 获取WindowManager
     */
    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 获取竖屏下状态栏高度
     */
    public static double getStatusBarHeightPortrait(Context context) {
        int statusBarHeight = 0;
        // 获取status_bar_height_portrait资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height_portrait", "dimen", "android");
        if (resourceId > 0) {
            // 根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 边缘检测
     */
    public static boolean isEdge(Context context, MotionEvent e) {
        float edgeSize = dp2px(context, 40);
        return e.getRawX() < edgeSize
                || e.getRawX() > getScreenWidth(context, true) - edgeSize
                || e.getRawY() < edgeSize
                || e.getRawY() > getScreenHeight(context, true) - edgeSize;
    }


    /**
     * dp转为px
     */
    public static float dp2px(Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转为px
     */
    public static float sp2px(Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValue, context.getResources().getDisplayMetrics());
    }


}
