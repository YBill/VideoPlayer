package com.bill.videoplayer.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * author ywb
 * date 2021/12/22
 * desc 软键盘工具类
 */
public class KeyboardHelper {

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    public static void showSoftInput(View view) {
        if (view == null) return;
        view.requestFocus();
        InputMethodManager manager = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, 0);
    }

    /**
     * 隐藏软件盘
     */
    public static void hideSoftInput(View view) {
        if (view == null) return;
        InputMethodManager manager = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
