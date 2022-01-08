package com.bill.videoplayer.small_video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.videoplayer.R;
import com.bill.videoplayer.listener.CustomItemClickListener;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频列表Adapter
 */
public class SmallVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final CustomItemClickListener<SmallVideoBean> mClickListener;
    private List<SmallVideoBean> mData;

    public SmallVideoListAdapter(Context context, CustomItemClickListener<SmallVideoBean> clickListener) {
        mContext = context;
        this.mClickListener = clickListener;
    }

    public void setData(List<SmallVideoBean> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_small_video_list, parent, false);
        return new SmallVideoListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SmallVideoListHolder) holder).update(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class SmallVideoListHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView coverIv;
        private final AppCompatTextView titleTv;

        public SmallVideoListHolder(@NonNull View itemView) {
            super(itemView);
            coverIv = itemView.findViewById(R.id.iv_video_cover);
            titleTv = itemView.findViewById(R.id.tv_video_title);
        }

        private void update(final int position) {
            final SmallVideoBean bean = mData.get(position);

            titleTv.setText(bean.title);
            Glide.with(mContext)
                    .load(bean.cover)
                    .placeholder(R.drawable.default_placeholder_img)
                    .into(coverIv);

            itemView.setOnClickListener(v -> {
                if (mClickListener != null)
                    mClickListener.onClick(bean, position);
            });
        }

    }

}
