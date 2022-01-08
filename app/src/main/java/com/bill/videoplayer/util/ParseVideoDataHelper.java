package com.bill.videoplayer.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.bill.videoplayer.small_video.SmallVideoBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * author ywb
 * date 2022/1/8
 * desc 视频数据解析
 */
public class ParseVideoDataHelper {

    public static List<SmallVideoBean> parseSmallVideoData(Context context) {
        String data = getDataInAssets(context, "small_video_data");

        Gson gson = new Gson();
        List<SmallVideoBean> list = gson.fromJson(data, new TypeToken<List<SmallVideoBean>>() {
        }.getType());

        return list;
    }

    public static String getDataInAssets(Context context, String fileName) {
        StringBuilder builder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

}
