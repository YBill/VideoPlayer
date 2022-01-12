package com.bill.videoplayer.small_video;

import java.io.Serializable;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频数据
 */
public class SmallVideoBean implements Serializable {

    public String video_url;
    public String cover;
    public String title;
    public String duration;
    public String timestamp;
    public String comment_count;
    public String likes_count;
    public String share_count;

}
