package com.xiao.nicevideoplayer;

/**
 * Created by XiaoJianjun on 2017/5/5.
 * 视频播放器管理器.
 */
public class SimpleVideoPlayerManager {

    private SimpleVideoPlayer mVideoPlayer;

    private SimpleVideoPlayerManager() {
    }

    private static SimpleVideoPlayerManager sInstance;

    public static synchronized SimpleVideoPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new SimpleVideoPlayerManager();
        }
        return sInstance;
    }

    public SimpleVideoPlayer getCurrentNiceVideoPlayer() {
        return mVideoPlayer;
    }

    public void setCurrentNiceVideoPlayer(SimpleVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseNiceVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }

    public void suspendNiceVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying())) {
            mVideoPlayer.pause();
        }
    }

    public void resumeNiceVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused())) {
            mVideoPlayer.restart();
        }
    }

    public void releaseNiceVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public boolean onBackPressd() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isFullScreen()) {
                return mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                return mVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
