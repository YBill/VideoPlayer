package com.bill.videoplayer.small_video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.baseplayer.base.BaseVideoController;
import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.player.DataSource;
import com.bill.videoplayer.R;
import com.bill.videoplayer.component.DebugInfoComponent;
import com.bill.videoplayer.util.ParseVideoDataHelper;
import com.bill.videoplayer.util.Utils;

/**
 * author ywb
 * date 2022/12/24
 * desc 小视频详情页
 */
public class SmallVideoDetailActivity extends AppCompatActivity {

    private RecyclerView mVideoRv;
    private VideoLayoutManager mLayoutManager;
    private VideoView mVideoView;
    private BaseVideoController mController;
    private int mCurrentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_video_detail);
        initView();
        initVideo();

        Intent extras = getIntent();
        int index = extras.getIntExtra("index", 0);
        mVideoRv.scrollToPosition(index);
    }

    private void initVideo() {
        mVideoView = new VideoView(this);
        mController = new BaseVideoController(this);
        mController.addControlComponent(new DebugInfoComponent(this));
        mVideoView.setVideoController(mController);
        mVideoView.setLooping(true);
    }

    private void initView() {
        mVideoRv = findViewById(R.id.rv_small_video_detail);
        mLayoutManager = new VideoLayoutManager(this);
        mVideoRv.setLayoutManager(mLayoutManager);
        SmallVideoDetailAdapter mAdapter = new SmallVideoDetailAdapter(this, null);
        mAdapter.setData(ParseVideoDataHelper.parseSmallVideoData(this));
        mVideoRv.setAdapter(mAdapter);

        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override
            public void onPageRelease(int position) {
                if (mCurrentPosition != position) return;
                mVideoView.release();
            }

            @Override
            public void onPageSelected(int position, int total) {
                if (mCurrentPosition == position) return;
                playerVideo();
            }
        });

    }

    private void playerVideo() {
        int visibleItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        if (visibleItemPosition >= 0 && mCurrentPosition != visibleItemPosition) {
            mVideoView.release(); // 停止上一个视频
            mCurrentPosition = visibleItemPosition;
            View holderView = mLayoutManager.findViewByPosition(mCurrentPosition);
            if (holderView != null) {
                Utils.removeViewFormParent(mVideoView);
                SmallVideoDetailAdapter.SmallVideoHolder viewHolder = (SmallVideoDetailAdapter.SmallVideoHolder)
                        mVideoRv.getChildViewHolder(holderView);
                viewHolder.videoViewContainer.addView(mVideoView);
                mController.addControlComponent(viewHolder.videoComponent);
                mVideoView.setDataSource(new DataSource(viewHolder.smallVideoBean.video_url));
                mVideoView.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
    }
}