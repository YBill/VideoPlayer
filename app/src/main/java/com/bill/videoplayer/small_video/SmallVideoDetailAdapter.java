package com.bill.videoplayer.small_video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.videoplayer.R;
import com.bill.videoplayer.listener.CustomItemClickListener;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频详情页Adapter
 */
public class SmallVideoDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final CustomItemClickListener<SmallVideoBean> mClickListener;
    private List<SmallVideoBean> mData;

    public SmallVideoDetailAdapter(Context context, CustomItemClickListener<SmallVideoBean> clickListener) {
        mContext = context;
        this.mClickListener = clickListener;
    }

    public void setData(List<SmallVideoBean> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_small_video_detail, parent, false);
        return new SmallVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SmallVideoHolder) holder).update(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class SmallVideoHolder extends RecyclerView.ViewHolder {

        public final FrameLayout videoViewContainer;
        public SmallVideoComponent videoComponent;
        private AppCompatImageView coverIv;
        public SmallVideoBean smallVideoBean;

        public SmallVideoHolder(@NonNull View itemView) {
            super(itemView);
            videoViewContainer = itemView.findViewById(R.id.video_view_container);
            videoComponent = itemView.findViewById(R.id.component_small_video);
            coverIv = videoComponent.getCoverIv();
        }

        private void update(final int position) {
            smallVideoBean = mData.get(position);

            Glide.with(mContext)
                    .load(smallVideoBean.cover)
                    .placeholder(R.drawable.small_video_placeholder_img)
                    .into(coverIv);

            itemView.setOnClickListener(v -> {
                if (mClickListener != null)
                    mClickListener.onClick(smallVideoBean, position);
            });
        }

    }

}
