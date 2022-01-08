package com.bill.videoplayer.listener;

/**
 * author ywb
 * date 2022/1/8
 * desc 通用Listener
 */
public interface CustomItemClickListener<T> {

    void onClick(T data, int position);

}
