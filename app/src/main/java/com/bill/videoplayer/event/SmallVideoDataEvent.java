package com.bill.videoplayer.event;

import com.bill.videoplayer.small_video.SmallVideoBean;

import java.util.List;

/**
 * author ywb
 * date 2022/1/10
 * desc 小视频数据传递
 */
public class SmallVideoDataEvent extends BaseEvent {

    public List<SmallVideoBean> mList;
    public int mIndex;

    public SmallVideoDataEvent(List<SmallVideoBean> list, int index) {
        this.mList = list;
        this.mIndex = index;
    }

}
