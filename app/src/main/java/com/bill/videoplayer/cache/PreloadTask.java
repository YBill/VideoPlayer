package com.bill.videoplayer.cache;

import com.bill.videoplayer.util.VPLog;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * author ywb
 * date 2021/1/11
 * desc
 */
public class PreloadTask implements Runnable {

    /**
     * 原始地址
     */
    public String mRawUrl;

    /**
     * 列表中的位置
     */
    public int mPosition;

    /**
     * 预加载大小
     */
    public long mPreloadLength = -1;

    /**
     * VideoCache服务器
     */
    public HttpProxyCacheServer mCacheServer;

    /**
     * 是否被取消
     */
    private boolean mIsCanceled;

    /**
     * 是否正在预加载
     */
    private boolean mIsExecuted;

    private final static Map<String, Integer> blackMap = new HashMap<>();

    /**
     * 将预加载任务提交到线程池，准备执行
     */
    public void executeOn(ExecutorService executorService) {
        if (mIsExecuted) return;
        mIsExecuted = true;
        executorService.submit(this);
    }

    /**
     * 取消预加载任务
     */
    public void cancel() {
        if (mIsExecuted) {
            mIsCanceled = true;
        }
    }

    @Override
    public void run() {
        if (!mIsCanceled) {
            start();
        }
        mIsExecuted = false;
        mIsCanceled = false;
    }

    /**
     * 开始预加载
     */
    private void start() {
//        if (isItABlacklist(mRawUrl)) return;
        VPLog.i("PreLoadCache", "预加载开始：" + mPosition);
        HttpURLConnection connection = null;
        try {
            String proxyUrl = mCacheServer.getProxyUrl(mRawUrl);
            URL url = new URL(proxyUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(5_000);
            InputStream in = new BufferedInputStream(connection.getInputStream());
            int length;
            int read = -1;
            byte[] bytes = new byte[8 * 1024];
            boolean isLoadAll = true;
            while ((length = in.read(bytes)) != -1) {
                read += length;
                if (mIsCanceled || (mPreloadLength >= 0 && read >= mPreloadLength)) {
                    if (mIsCanceled) {
                        VPLog.i("PreLoadCache", "预加载取消：" + mPosition + " 读取数据：" + read + " Byte");
                    } else {
                        VPLog.i("PreLoadCache", "预加载成功：" + mPosition + " 读取数据：" + read + " Byte");
                    }
                    isLoadAll = false;
                    break;
                }
            }
            if (isLoadAll)
                VPLog.i("PreLoadCache", "此文件下载全部下载：" + mPosition);
        } catch (Exception e) {
            VPLog.i("PreLoadCache", "预加载异常：" + mPosition + " 异常信息：" + e.getMessage());
//            addToBlacklist(mRawUrl);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            VPLog.i("PreLoadCache", "预加载结束: " + mPosition);
        }
    }

    private void addToBlacklist(String url) {
        // 加入黑名单
        Integer currentFailNum = blackMap.get(url);
        if (currentFailNum == null) {
            blackMap.put(url, 1);
        } else {
            blackMap.put(url, currentFailNum + 1);
        }
    }

    private boolean isItABlacklist(String url) {
        // 如果失败2次说明这个地址可能有问题，就不缓存了
        Integer failNum = blackMap.get(url);
        if (failNum != null && failNum > 2) {
            VPLog.i("PreLoadCache", "拒绝此次预加载：" + mPosition);
            return true;
        }
        return false;
    }

}

