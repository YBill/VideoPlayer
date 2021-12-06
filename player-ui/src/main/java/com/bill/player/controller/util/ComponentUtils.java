package com.bill.player.controller.util;

import java.util.Locale;

/**
 * author ywb
 * date 2021/12/6
 * desc
 */
public class ComponentUtils {

    /**
     * 格式化时间
     */
    public static String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

}
