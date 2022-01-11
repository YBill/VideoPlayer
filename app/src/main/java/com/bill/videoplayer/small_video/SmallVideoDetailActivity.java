package com.bill.videoplayer.small_video;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.baseplayer.base.BaseVideoController;
import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.player.DataSource;
import com.bill.videoplayer.R;
import com.bill.videoplayer.cache.PreloadManager;
import com.bill.videoplayer.cache.VideoCacheManager;
import com.bill.videoplayer.event.SmallVideoDataEvent;
import com.bill.videoplayer.util.Utils;
import com.bill.videoplayer.util.VPLog;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author ywb
 * date 2022/12/24
 * desc 小视频详情页
 */
public class SmallVideoDetailActivity extends AppCompatActivity {

    private RecyclerView mVideoRv;
    private SmallVideoDetailAdapter mAdapter;
    private VideoLayoutManager mLayoutManager;
    private VideoView mVideoView;
    private BaseVideoController mController;
    private int mCurrentPosition = -1;

    private List<SmallVideoBean> mData;
    private int mIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImmersionBar.with(this).init();
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_small_video_detail);
        initView();
        initVideo();

        if (mIndex >= 0)
            mVideoRv.scrollToPosition(mIndex);
    }

    private void initVideo() {
        mVideoView = new VideoView(this);
        mController = new BaseVideoController(this);
//        mController.addControlComponent(new DebugInfoComponent(this));
        mVideoView.setVideoController(mController);
        mVideoView.setLooping(true);
    }

    private void initView() {
        mVideoRv = findViewById(R.id.rv_small_video_detail);
        mLayoutManager = new VideoLayoutManager(this);
        mVideoRv.setLayoutManager(mLayoutManager);
        mAdapter = new SmallVideoDetailAdapter(this, null);
        mAdapter.setData(mData);
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
            boolean isReverseScroll = mCurrentPosition > visibleItemPosition; // 是否反向滑动
            mVideoView.release(); // 停止上一个视频
            mCurrentPosition = visibleItemPosition;
            View holderView = mLayoutManager.findViewByPosition(mCurrentPosition);
            if (holderView != null) {
                // 播放器前先暂停缓存视频
                PreloadManager.getInstance().pausePreload();

                Utils.removeViewFormParent(mVideoView);
                SmallVideoDetailAdapter.SmallVideoHolder viewHolder = (SmallVideoDetailAdapter.SmallVideoHolder)
                        mVideoRv.getChildViewHolder(holderView);
                viewHolder.videoViewContainer.addView(mVideoView);
                mController.addControlComponent(viewHolder.videoComponent);

                String proxyUrl = PreloadManager.getInstance().getPlayUrl(viewHolder.smallVideoBean.video_url);
                VPLog.d("PreLoadCache", "proxyUrl：" + proxyUrl);
                // 传递position和方向，在准备完成后使用
                Map<String, Object> map = new HashMap<>();
                map.put("position", mCurrentPosition);
                map.put("isReverseScroll", isReverseScroll);
                DataSource dataSource = new DataSource(proxyUrl);
                dataSource.mOtherParams = map;
                mVideoView.setDataSource(dataSource);
                mVideoView.start();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onSmallVideoListDataEvent(SmallVideoDataEvent event) {
        if (mVideoRv == null) {
            mData = event.mList;
            mIndex = event.mIndex;
        } else {
            mAdapter.setData(mData);
            mAdapter.notifyDataSetChanged();
            mVideoRv.scrollToPosition(mIndex);
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
        EventBus.getDefault().unregister(this);
        if (mVideoView != null) {
            mVideoView.release();
        }

        VPLog.d("PreLoadCache", "清空缓存");
        PreloadManager.getInstance().removeAllPreloadTask();
        VideoCacheManager.getInstance().clearAllCache();
    }
}