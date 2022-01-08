package com.bill.videoplayer.small_video;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.videoplayer.R;
import com.bill.videoplayer.listener.CustomItemClickListener;
import com.bill.videoplayer.util.ParseVideoDataHelper;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频列表
 */
public class SmallVideoListActivity extends AppCompatActivity implements CustomItemClickListener<SmallVideoBean> {

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
        SmallVideoListAdapter adapter = new SmallVideoListAdapter(this, this);
        adapter.setData(ParseVideoDataHelper.parseSmallVideoData(this));
        mVideoListRv.setAdapter(adapter);
    }

    @Override
    public void onClick(SmallVideoBean data, int position) {
        Intent intent = new Intent(this, SmallVideoDetailActivity.class);
        intent.putExtra("index", position);
        startActivity(intent);
    }
}