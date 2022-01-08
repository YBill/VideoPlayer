package com.bill.videoplayer.small_video;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频滑动监听
 */
public interface OnViewPagerListener {

    void onPageRelease(int position);

    void onPageSelected(int position, int total);

}
