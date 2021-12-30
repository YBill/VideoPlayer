## ViewPlayer

#### 一、基本使用：

##### 1、导入module：

```
    implementation project(':baseplayer') // 播放器核心，必须引用
    // 控制器UI（内置了准备播放、进度控制等、手势滑动、播放完成、播放错误五个组件），需要则导入，也可以自定义
    implementation project(':player-ui') 
    implementation project(':players:ijk') // 解码器：IjkPlayer，需要则导入
    implementation project(':players:exo') // 解码器：ExoPlayer，需要则导入
```

##### 2、全局配置：

如果不设置则使用默认配置，每个播放器也可以自己设置

```
      VideoViewManager.getInstance().setConfig(VideoViewConfig.create()
                .xxx()
                .build());
```

##### 3、初始化：

在 Application 中设置全局配置，不设置则使用默认的，每个播放器还可以单独设置

```
        VideoViewManager.getInstance().setConfig(VideoViewConfig.create()
                .setLogEnabled(BuildConfig.DEBUG)
                .setPlayerFactory(IjkPlayerFactory.create())
                .setRenderViewFactory(TextureRenderViewFactory.create())
                .setScreenScaleType(AspectRatioType.AR_ASPECT_FIT_PARENT)
                .build());
```

##### 4、创建 VideoView

可以在布局中或代码中创建

```
   VideoView videoView = new VideoView(this);
   或
   <com.bill.baseplayer.base.VideoView
        android:id="@+id/video_player"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

##### 5、创建控制器

BaseVideoController 是基础控制器，需要通过 addControlComponent 添加不同组件，不需要可以不设置控制器

```
BaseVideoController videoController = = new BaseVideoController(this);
videoController.addControlComponent(IControlComponent);
videoView.setVideoController(videoController);
```

##### 6、设置播放资源

```
DataSource dataSource = new DataSource();
dataSource.mUrl = "https://xxx.mp4";
videoView.setDataSource(dataSource);
```

##### 7、播放

```
videoView.start();
```

#### 二、API：

##### 1、全局配置：

支持的配置如下：

```
/**
 * 是否打印日志，默认不打印
 */
public Builder setLogEnabled(boolean enableLog) {}

/**
 * 在移动环境下调用start()后是否继续播放，默认继续播放
 * 配合 VideoViewManager.getInstance().setPlayOnMobileNetwork(true) 使用（用户继续后应该如果不再需要提示）
 */
public Builder setPlayOnMobileNetwork(boolean playOnMobileNetwork) {}

/**
 * 是否监听设备方向来切换全屏/半屏，默认不开启
 */
public Builder setEnableOrientation(boolean enableOrientation) {}

/**
 * 设置进度管理器，用于保存播放进度
 */
public Builder setProgressManager(@Nullable IProgressManager progressManager) {}

/**
 * 设置解码器，默认使用MediaPlayer
 */
public Builder setPlayerFactory(CreateClsFactory<AbstractPlayer> playerFactory) {}

/**
 * 设置渲染器，默认使用TextureView
 */
public Builder setRenderViewFactory(CreateClsFactory<IRenderView> renderViewFactory) {}

/**
 * 设置视频比例 {@link AspectRatioType}
 */
public Builder setScreenScaleType(@AspectRatioType int screenScaleType) {}
```

##### 2、播放器 VideoView API：

- 设置播放背景颜色，默认是黑色

```
public void setPlayerBackgroundColor(@ColorInt int color) {}
```

- 设置视频资源

  DataSource 支持如下参数：

  - mUrl：视频网络地址
  - mHeaders：视频地址的请求头
  - mTitle：视频的标题，设置了可以在控制器组件中获取使用
  - mAssetsPath：assets下视频文件名
  - mRawId：raw下视频文件ID

```
public void setDataSource(DataSource dataSource) {}
```

- 设置音量

```
/**
 * @param v1 左声道音量
 * @param v2 右声道音量
 */
public void setVolume(float v1, float v2) {}
```

- 是否开启 AudioFocus 监听，开启后比如播放了例如网易云音乐歌曲，则视频会暂停

```
/**
 * 是否开启AudioFocus监听，默认开启，用于监听其它地方是否获取音频焦点，如果有其它地方获取了
 * 音频焦点，此播放器将做出相应反应，具体实现见{@link AudioFocusHelper}
 */
public void setEnableAudioFocus(boolean enableAudioFocus) {}
```

- 设置循环播放，默认不循环，注意循环播放重播后没有回调

```
public void setLooping(boolean looping) {}
```

- 设置进度管理器，用于保存播放进度，默认不处理进度，可实现 IProgressManager 接口，处理逻辑

```
public void setProgressManager(@Nullable IProgressManager progressManager) {}
```

- 开始播放则调到指定位置

```
public void seekPositionWhenPlay(int position) {}
```

- 设置播放器解码器，默认使用 MediaPlayer

  可以引入 players:ijk 使用 IjkPlayerFactory 或引入 players:exo 使用 ExoPlayerFactory

  也可以继承 CreateClsFactory<AbstractPlayer>，实现自己的解码器

```
public void setPlayerFactory(CreateClsFactory<AbstractPlayer> playerFactory) {}
```

- 设置播放器渲染器，默认使用 TextureView

  可以 SurfaceRenderViewFactory

  也可以继承 CreateClsFactory<IRenderView>，实现自己的渲染器

```
public void setRenderViewFactory(CreateClsFactory<IRenderView> renderViewFactory) {}
```

- 设置视频比例

```
public void setScreenScaleType(@AspectRatioType int screenScaleType) {}
```

视频比例支持如下六种方式：

```
public @interface AspectRatioType {
    int AR_ASPECT_FIT_PARENT = 0; // fitCenter (without clip)
    int AR_ASPECT_FILL_PARENT = 1; // centerCrop (may clip)
    int AR_ASPECT_WRAP_CONTENT = 2; // center (without stretch) , use video width and height
    int AR_MATCH_PARENT = 3; // fitXY (may stretch)
    int AR_16_9_FIT_PARENT = 4; // 16:9 (without stretch)
    int AR_4_3_FIT_PARENT = 5; // 4:3 (without stretch)

}
```

- 设置控制器，传null表示移除控制器

```
public void setVideoController(@Nullable BaseVideoController mediaController) {}
```

- 获取控制器

```
public BaseVideoController getVideoController() {}
```

- 添加播放器监听

  包括播放器的状态(普通、全屏、小窗) 和 播放状态(播放、暂停等)

```
public void addVideoStateChangeListener(@NonNull OnVideoStateChangeListener listener) {}
```

- 移除播放器监听

```
public void removeVideoStateChangeListener(@NonNull OnVideoStateChangeListener listener) {}
```

- 其他 API

```
/*
* 开始播放，注意：调用此方法后必须调用{@link #release()}释放播放器，否则会导致内存泄漏
*/
public void start() {}

/*
* 暂停播放
*/
public void pause() {}

/*
* 继续播放
*/
public void resume() {}

/*
* 释放播放器
*/
public void release() {}

/*
* 重新播放
* @param resetPosition 是否从头开始播放
*/
public void replay(boolean resetPosition) {}

/*
* 获取视频总时长
*/
public long getDuration() {}

/*
* 获取当前播放的位置
*/
public long getCurrentPosition() {}

/*
* 跳转到播放位置
*/
public void seekTo(long pos) {}

/*
* 是否处于播放状态
*/
public boolean isPlaying() {}

/*
* 获取当前缓冲百分比
*/
public int getBufferedPercentage() {}

/*
* 开始播放，注意：调用此方法后必须调用{@link #release()}释放播放器，否则会导致内存泄漏
*/
public void start() {}

/*
* 设置静音
*/
public void setMute(boolean isMute) {}

/*
* 是否处于静音状态
*/
public boolean isMute() {}

/*
* 设置播放速度
*/
public void setSpeed(float speed) {}

/*
* 获取播放速度
*/
public float getSpeed() {}

/*
* 获取视频宽高,其中width: mVideoSize[0], height: mVideoSize[1]
*/
public int[] getVideoSize() {}

/*
* 进入全屏
*/
public void enterFullScreen() {}

/*
* 退出全屏
*/
public void exitFullScreen() {}

/*
* 判断是否处于全屏状态
*/
public boolean isFullScreen() {}

/*
* 进入小窗
*/
public void enterTinyScreen() {}

/*
* 退出小窗
*/
public void exitTinyScreen() {}

/*
* 是否小窗
*/
public boolean isTinyScreen() {}

/*
* 设置小窗播放容器
*/
public void setTinyScreenView(ViewGroup tinyScreenContainerView) {}
```

##### 3、控制器 BaseVideoController API：

- 添加控制组件，最后面添加的在最上面，合理组织添加顺序，可让ControlComponent位于不同的层级

  可以引入 player-ui ，内置了准备播放、进度控制等、手势滑动、播放完成、播放错误五个组件

  也可以自定义组件，实现 IControlComponent 接口，内部所有控制器状态都是共享的

```
public void addControlComponent(IControlComponent... components) {}
```

- 获取控制器中的某个组件

```
/*
* @param key 根据 {@link IControlComponent#getKey()} 获取
*/
public IControlComponent getControlComponent(String key) {}
```

- 移除控制组件

```
public void removeControlComponent(IControlComponent component) {}
```

- 移除所有控制组件

```
public void clearControlComponents() {}
```

- 设置控制器自动隐藏时间，默认5s

```
public void setAutoHideCountdown(int autoHideCountdown) {}
```

- 是否自动旋转，默认不自动旋转

```
public void setEnableOrientation(boolean enableOrientation) {}
```

- 显示控制器

```
public void show() {}
```

- 隐藏控制器

```
public void hide() {}
```

- 设置锁定控制器

```
public void setLocked(boolean locked) {}
```

- 控制器是否锁定

```
public boolean isLocked() {}
```

- 开始刷新进度，注意：需在STATE_PLAYING时调用才会开始刷新进度

```
public void startProgress() {}
```

- 停止刷新进度

```
public void stopProgress() {}
```

