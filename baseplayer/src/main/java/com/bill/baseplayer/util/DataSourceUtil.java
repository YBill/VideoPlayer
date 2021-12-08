package com.bill.baseplayer.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.text.TextUtils;

import java.io.IOException;

/**
 * author ywb
 * date 2021/12/8
 * desc
 */
public class DataSourceUtil {

    public static String buildAssets(String assetsPath) {
        return "file:///android_asset/" + assetsPath;
    }

    public static Uri buildAssetsUri(String assetsPath) {
        return Uri.parse(buildAssets(assetsPath));
    }

    public static AssetFileDescriptor getAssetsFileDescriptor(Context context, String assetsPath) {
        if (TextUtils.isEmpty(assetsPath))
            return null;
        try {
            return context.getAssets().openFd(assetsPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
