package com.kinotor.tiar.kinotor.ui.reclam;

import android.content.Context;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiar on 08.2018.
 */
public class SampleVideoPlayer extends VideoView {

  /**
   * Interface for alerting caller of video completion.
   */
  public interface OnVideoCompletedListener {

    /**
     * Called when the current video has completed playback to the end of the video.
     */
    void onVideoCompleted();
  }

  private final List<OnVideoCompletedListener> mOnVideoCompletedListeners = new ArrayList<>(1);

  public SampleVideoPlayer(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public SampleVideoPlayer(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SampleVideoPlayer(Context context) {
    super(context);
    init();
  }

  private void init() {
    MediaController mediaController = new MediaController(getContext());
    mediaController.setAnchorView(this);

    // Set OnCompletionListener to notify our listeners when the video is completed.
    super.setOnCompletionListener(mediaPlayer -> {
      // Reset the MediaPlayer.
      // This prevents a race condition which occasionally results in the media
      // player crashing when switching between videos.
      mediaPlayer.reset();
      mediaPlayer.setDisplay(getHolder());

      for (OnVideoCompletedListener listener : mOnVideoCompletedListeners) {
        listener.onVideoCompleted();
      }
    });

    // Set OnErrorListener to notify our listeners if the video errors.
    super.setOnErrorListener((mp, what, extra) -> {

      // Returning true signals to MediaPlayer that we handled the error. This will
      // prevent the completion handler from being called.
      return true;
    });
  }

  @Override
  public void setOnCompletionListener(OnCompletionListener listener) {
    // The OnCompletionListener can only be implemented by SampleVideoPlayer.
    throw new UnsupportedOperationException();
  }

  public void play() {
    start();
  }

  public void addVideoCompletedListener(OnVideoCompletedListener listener) {
    mOnVideoCompletedListeners.add(listener);
  }
}
