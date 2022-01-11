package com.bill.videoplayer.small_video;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.videoplayer.cache.PreloadManager;
import com.bill.videoplayer.util.VPLog;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * author ywb
 * date 2022/1/11
 * desc
 */
public abstract class CacheSmallVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected List<SmallVideoBean> mData;

    private int mCurrentPosition = -1;

    public CacheSmallVideoAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getAdapterPosition();
        if (position == mCurrentPosition) {
            return;
        }

        boolean isReverseScroll = position < mCurrentPosition;
        VPLog.d("PreLoadCache", "当前position = " + position + ", 当前方向 = " + isReverseScroll);

        // 删除，前后各保留五条
        int startPos = position - 5;
        int endPos = position + 5;
        VPLog.d("PreLoadCache", "删除安全范围：" + startPos + " ~ " + endPos);
        PreloadManager.getInstance().removePreloadTaskAndDiskOutOfRange(startPos, endPos);

        if (isReverseScroll) {
            for (int i = position - 1; i >= Math.max(position - 2, 0); i--) {
                String url = getVideoUrl(i);
                if (!TextUtils.isEmpty(url)) {
                    PreloadManager.getInstance().addPreloadTask(url, i, false);
                }
            }
        } else {
            for (int i = position + 1; i <= Math.min(position + 3, getItemCount()); i++) {
                String url = getVideoUrl(i);
                if (!TextUtils.isEmpty(url)) {
                    PreloadManager.getInstance().addPreloadTask(url, i, false);
                    preloadImg(i);
                }
            }
        }

        mCurrentPosition = position;

    }

    private String getVideoUrl(int position) {
        if (mData == null) return null;

        if (position < 0 || position >= mData.size()) {
            return null;
        }
        SmallVideoBean videoItemBean = mData.get(position);
        if (videoItemBean != null) {
            return videoItemBean.video_url;
        }
        return null;
    }

    private void preloadImg(int position) {
        if (mData == null) return;

        if (position < 0 || position >= mData.size()) {
            return;
        }

        SmallVideoBean videoItemBean = mData.get(position);
        if (videoItemBean == null) {
            return;
        }

        String url = videoItemBean.cover;

        if (TextUtils.isEmpty(url))
            return;

        if (mContext == null || (mContext instanceof Activity && ((Activity) mContext).isFinishing())) {
            return;
        }

        Glide.with(mContext).load(url).preload();
    }

}
