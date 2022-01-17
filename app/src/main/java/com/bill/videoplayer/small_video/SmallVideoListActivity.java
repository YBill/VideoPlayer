package com.bill.videoplayer.small_video;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.videoplayer.R;
import com.bill.videoplayer.event.SmallVideoDataEvent;
import com.bill.videoplayer.listener.CustomItemClickListener;
import com.bill.videoplayer.util.ParseVideoDataHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频列表
 */
public class SmallVideoListActivity extends AppCompatActivity implements CustomItemClickListener<SmallVideoBean> {

    SmallVideoListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_video_list);
        initView();
    }

    private void initView() {
        RecyclerView mVideoListRv = findViewById(R.id.rv_small_video_list);

        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mVideoListRv.setLayoutManager(manager);
        mVideoListRv.addItemDecoration(new SmallVideoListDecoration());
        mAdapter = new SmallVideoListAdapter(this, this);
        mAdapter.setData(ParseVideoDataHelper.parseSmallVideoData(this));
        mVideoListRv.setAdapter(mAdapter);
    }

    @Override
    public void onClick(SmallVideoBean data, int position) {
        Intent intent = new Intent(this, SmallVideoDetailActivity.class);
        startActivity(intent);

        // 因为实际list的大小是不确定的，如果用Intent传递可能会超出Intent的大小，所以使用EventBus传递值了
        EventBus.getDefault().postSticky(new SmallVideoDataEvent(mAdapter.getData(), position));
    }
}