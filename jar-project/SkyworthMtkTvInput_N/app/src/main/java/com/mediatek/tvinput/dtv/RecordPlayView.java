/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mediatek.tvinput.dtv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.Metadata;
import android.media.SubtitleController;

import android.media.tv.TvTrackInfo;
import android.media.tv.TvTrackInfo.Builder;

import android.net.Uri;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Surface;
import android.widget.MediaController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.mediatek.MtkMediaPlayer;
import com.mediatek.MtkMediaPlayer.PlayerRole;
import com.mediatek.MtkMediaPlayer.PlayerSpeed;
import com.mediatek.MtkTrackInfo;

/**
 * Displays a record video file. The RecordPlayView class can load images from various sources (such
 * as resources or content providers), takes care of computing its measurement from the video so
 * that it can be used in any layout manager, and provides various display options such as scaling
 * and tinting.
 * <p>
 * <em>Note: RecordPlayView does not retain its full state when going into the
 * background.</em> In particular, it does not restore the current play state, play position,
 * selected tracks, or any subtitle tracks added via {@link #addSubtitleSource addSubtitleSource()}.
 * Applications should save and restore these on their own in
 * {@link android.app.Activity#onSaveInstanceState} and
 * {@link android.app.Activity#onRestoreInstanceState}.
 * <p>
 * Also note that the audio session id (from {@link #getAudioSessionId}) may change from its
 * previously returned value when the RecordPlayView is restored.
 */
public class RecordPlayView {
  private final String TAG = "(MtkTvInput)RecordPlayView";
  // settable by the client
  private Uri mUri;
  private Map<String, String> mHeaders;

  public static final int MTK_MEDIA_INFO_METADATA_UPDATE = MtkMediaPlayer.MEDIA_INFO_METADATA_UPDATE;
  public static final int MTK_MEDIA_INFO_VIDEO_RENDERING_START = 3;
  public static final int MTK_MEDIA_INFO_VIDEO_RATING_LOCKED = 1008;
  public static final int MTK_MEDIA_INFO_VIDEO_LOCKED = 2001;
  public static final int MTK_MEDIA_INFO_ON_REPLAY = MtkMediaPlayer.MEDIA_INFO_ON_REPLAY;
  public static final int MTK_MEDIA_INFO_VIDEO_REPLAY_DONE = MtkMediaPlayer.MEDIA_INFO_VIDEO_REPLAY_DONE;

  public static final int MTK_MEDIA_INFO_VIDEO_ENCODE_FORMAT_UNSUPPORT = MtkMediaPlayer.MEDIA_INFO_VIDEO_ENCODE_FORMAT_UNSUPPORT;
  public static final int MTK_MEDIA_INFO_AUDIO_ENCODE_FORMAT_UNSUPPORT = MtkMediaPlayer.MEDIA_INFO_AUDIO_ENCODE_FORMAT_UNSUPPORT;
  public static final int MTK_MEDIA_ERROR_FILE_NOT_SUPPORT = MtkMediaPlayer.MEDIA_ERROR_FILE_NOT_SUPPORT;
  public static final int MTK_MEDIA_ERROR_FILE_CORRUPT = MtkMediaPlayer.MEDIA_ERROR_FILE_CORRUPT;
  public static final int MTK_MEDIA_ERROR_OPEN_FILE_FAILED = MtkMediaPlayer.MEDIA_ERROR_OPEN_FILE_FAILED;
  public static final int MTK_MEDIA_ERROR_UNKNOWN = MediaPlayer.MEDIA_ERROR_UNKNOWN;





  // all possible internal states
  private static final int STATE_ERROR = -1;
  private static final int STATE_IDLE = 0;
  private static final int STATE_PREPARING = 1;
  private static final int STATE_PREPARED = 2;
  private static final int STATE_PLAYING = 3;
  private static final int STATE_PAUSED = 4;
  private static final int STATE_PLAYBACK_COMPLETED = 5;

  // mCurrentState is a RecordPlayView object's current state.
  // mTargetState is the state that a method caller intends to reach.
  // For instance, regardless the RecordPlayView object's current state,
  // calling pause() intends to bring the object to a target state
  // of STATE_PAUSED.
  private int mCurrentState = STATE_IDLE;
  private int mTargetState = STATE_IDLE;

  // All the stuff we need for playing and showing a video
  private com.mediatek.MtkMediaPlayer mMediaPlayer = null;
  private Surface mSurface = null;
  // private SurfaceHolder mSurfaceHolder = null;
  private String mCurrentPath = null;
  private Context mContext = null;
  private int mAudioSession;
  private int mVideoWidth;
  private int mVideoHeight;
  private int mSurfaceWidth;
  private int mSurfaceHeight;
  private final MediaController mMediaController = null;
  private OnCompletionListener mOnCompletionListener;
  private OnPreparedListener mOnPreparedListener;
  private int mCurrentBufferPercentage;
  private OnErrorListener mOnErrorListener;
  private OnInfoListener mOnInfoListener;
  private OnSeekCompleteListener mOnSeekCompleteListener;
  private int mSeekWhenPrepared; // recording the seek position while preparing
  private boolean mCanPause;
  private boolean mCanSeekBack;
  private boolean mCanSeekForward;

  public RecordPlayView(Context context) {
    mContext = context;
    initRecordPlayView();
  }

  private void initRecordPlayView() {
    mVideoWidth = 0;
    mVideoHeight = 0;

    // mPendingSubtitleTracks = new Vector<Pair<InputStream, MediaFormat>>();
    mCurrentState = STATE_IDLE;
    mTargetState = STATE_IDLE;
  }

  /**
   * Sets video path.
   *
   * @param path the path of the video.
   */
  public void setVideoPath(String path) {
    Log.d(TAG, "setVideoPath: path = " + path);
    mCurrentPath = path;
    setVideoURI(Uri.fromFile(new File(path)));
  }

  /**
   * Sets video URI.
   *
   * @param uri the URI of the video.
   */
  public void setVideoURI(Uri uri) {
    Map<String, String> mHeaders = new HashMap<String, String>();
    mHeaders.put("X-tv-output-path", "OUTPUT_VIDEO_MAIN");
    Log.d("setVideoURI", "mHeaders(X-tv-output-path, OUTPUT_VIDEO_MAIN)");
    setVideoURI(uri, mHeaders);
  }

  /**
   * Sets video URI using specific headers.
   *
   * @param uri the URI of the video.
   * @param headers the headers for the URI request. Note that the cross domain redirection is
   *          allowed by default, but that can be changed with key/value pairs through the headers
   *          parameter with "android-allow-cross-domain-redirect" as the key and "0" or "1" as the
   *          value to disallow or allow cross domain redirection.
   */
  public void setVideoURI(Uri uri, Map<String, String> headers) {
    mUri = uri;
    mHeaders = headers;
    mSeekWhenPrepared = 0;
    openVideo();
  }

  /**
   * Adds an external subtitle source file (from the provided input stream.) Note that a single
   * external subtitle source may contain multiple or no supported tracks in it. If the source
   * contained at least one track in it, one will receive an
   * {@link MediaPlayer#MEDIA_INFO_METADATA_UPDATE} info message. Otherwise, if reading the source
   * takes excessive time, one will receive a {@link MediaPlayer#MEDIA_INFO_SUBTITLE_TIMED_OUT}
   * message. If the source contained no supported track (including an empty source file or null
   * input stream), one will receive a {@link MediaPlayer#MEDIA_INFO_UNSUPPORTED_SUBTITLE} message.
   * One can find the total number of available tracks using {@link MediaPlayer#getTrackInfo()} to
   * see what additional tracks become available after this method call.
   *
   * @param is input stream containing the subtitle data. It will be closed by the media framework.
   * @param format the format of the subtitle track(s). Must contain at least the mime type (
   *          {@link MediaFormat#KEY_MIME}) and the language ({@link MediaFormat#KEY_LANGUAGE}) of
   *          the file. If the file itself contains the language information, specify "und" for the
   *          language.
   */
  /*
   * public void addSubtitleSource(InputStream is, MediaFormat format) { if (mMediaPlayer == null) {
   * mPendingSubtitleTracks.add(Pair.create(is, format)); } else { try {
   * mMediaPlayer.addSubtitleSource(is, format); } catch (IllegalStateException e) {
   * mInfoListener.onInfo( mMediaPlayer, MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE, 0); } } }
   */

  // private Vector<Pair<InputStream, MediaFormat>> mPendingSubtitleTracks;

  public void stopPlayback() {
    Log.d(TAG, "stopPlayback");
    if (mMediaPlayer != null) {
      Log.d(TAG, "stopPlayback call mMediaPlayer.stop and mMediaPlayer.release");
      mMediaPlayer.stop();
      // mMediaPlayer.release();
      // mMediaPlayer = null;
      mCurrentState = STATE_IDLE;
      mTargetState = STATE_IDLE;
      // AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
      // am.abandonAudioFocus(null);
    }
  }

  private void openVideo() {
    Log.d(TAG, "openVideo");
    if (mUri == null || mSurface == null) {
      // not ready for playback just yet, will try again later
      Log.d(TAG, "openVideo: " + "mUri == null || mSurface == null, return");
      return;
      }
    // we shouldn't clear the target state, because somebody might have
    // called start() previously
    release(false);

    // AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    // am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    try {
      mMediaPlayer = new com.mediatek.MtkMediaPlayer();

      if (mAudioSession != 0) {
        mMediaPlayer.setAudioSessionId(mAudioSession);
      } else {
        mAudioSession = mMediaPlayer.getAudioSessionId();
      }
      mMediaPlayer.setOnPreparedListener(mPreparedListener);
      mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
      mMediaPlayer.setOnCompletionListener(mCompletionListener);
      mMediaPlayer.setOnErrorListener(mErrorListener);
      mMediaPlayer.setOnInfoListener(mInfoListener);
      mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
      mMediaPlayer.setOnSeekCompleteListener(mSeekCompletionListener);
      mCurrentBufferPercentage = 0;
      mMediaPlayer.setPlayerType(6);
      Log.d(TAG, "openVideo: " + "mMediaPlayer.setDataSource, mUri = " + mUri + ", mHeaders ="
          + mHeaders);
      mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
      mMediaPlayer.setPlayerRole(PlayerRole.ROLE_VIDEO_PLAYBACK);

      if (mCurrentPath != null) {
        if (mCurrentPath.toLowerCase().endsWith(".pvr")) {
          mMediaPlayer.setSvctxPath("ANDR_PVR");
        }
        else if (mCurrentPath.toLowerCase().endsWith(".tshift")) {
          mMediaPlayer.setSvctxPath("ANDR_TIMESHIFT");
        }
        }
      // mMediaPlayer.setDisplay(mSurfaceHolder);
      mMediaPlayer.setSurface(mSurface);
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      // mMediaPlayer.setScreenOnWhilePlaying(true);
      Log.d(TAG, "openVideo: " + "mMediaPlayer.prepareAsync");
      mMediaPlayer.prepareAsync();

      // we don't set the target state here either, but preserve the
      // target state that was there before.
      mCurrentState = STATE_PREPARING;
      // Log.d(TAG, "openVideo: mCurrentState = STATE_PREPARING" + ", call start()");
      // start();
      // attachMediaController();
    } catch (IOException ex) {
      Log.w(TAG, "Unable to open content: " + mUri, ex);
      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;
      Log.e(TAG, "openVideo: " + "onError(MediaPlayer.MEDIA_ERROR_UNKNOWN)");
      mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
      return;
    } catch (IllegalArgumentException ex) {
      Log.w(TAG, "Unable to open content: " + mUri, ex);
      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;
      Log.e(TAG, "openVideo: " + "onError(MediaPlayer.MEDIA_ERROR_UNKNOWN)");
      mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
      return;
    } finally {
      // mPendingSubtitleTracks.clear();
    }
  }

  /*
   * public void setMediaController(MediaController controller) { if (mMediaController != null) {
   * mMediaController.hide(); } mMediaController = controller; attachMediaController(); } private
   * void attachMediaController() { if (mMediaPlayer != null && mMediaController != null) {
   * mMediaController.setMediaPlayer(this); View anchorView = this.getParent() instanceof View ?
   * (View)this.getParent() : this; mMediaController.setAnchorView(anchorView);
   * mMediaController.setEnabled(isInPlaybackState()); } }
   */

  MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
      new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
          mVideoWidth = mp.getVideoWidth();
          mVideoHeight = mp.getVideoHeight();
          if (mVideoWidth != 0 && mVideoHeight != 0) {
            // getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            // requestLayout();
          }
        }
      };

  MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
    @Override
    public void onPrepared(MediaPlayer mp) {
      Log.d(TAG, "mPreparedListener, onPrepared: ");
      mCurrentState = STATE_PREPARED;

      // Get the capabilities of the player for this stream
      Metadata data = mp.getMetadata(MediaPlayer.METADATA_ALL,
          MediaPlayer.BYPASS_METADATA_FILTER);

      if (data != null) {
        mCanPause = !data.has(Metadata.PAUSE_AVAILABLE)
            || data.getBoolean(Metadata.PAUSE_AVAILABLE);
        mCanSeekBack = !data.has(Metadata.SEEK_BACKWARD_AVAILABLE)
            || data.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE);
        mCanSeekForward = !data.has(Metadata.SEEK_FORWARD_AVAILABLE)
            || data.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE);
      } else {
        mCanPause = mCanSeekBack = mCanSeekForward = true;
      }

      Log.d(TAG, "mPreparedListener, onPrepared: mCanPause = " + mCanPause + ", mCanSeekBack = "
          + mCanSeekBack + ", mCanSeekForward = " + mCanSeekForward);

      if (mOnPreparedListener != null) {
        Log.d(TAG, "mPreparedListener, onPrepared: call mOnPreparedListener.onPrepared ");
        mOnPreparedListener.onPrepared(mMediaPlayer);
      }

      if (mMediaController != null) {
        mMediaController.setEnabled(true);
      }

      mVideoWidth = mp.getVideoWidth();
      mVideoHeight = mp.getVideoHeight();
      Log.d(TAG, "mPreparedListener, onPrepared: (mVideoWidth =  " + mVideoWidth
          + ",mVideoHeight = " + mVideoHeight + ")");
      int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be changed after seekTo()
                                              // call
      if (seekToPosition != 0) {
        Log.d(TAG, "mPreparedListener, onPrepared: seekTo(seekToPosition =  " + seekToPosition
            + ")");
        seekTo(seekToPosition);
      }
      if (mVideoWidth != 0 && mVideoHeight != 0) {
        Log.d(TAG,
            "mPreparedListener, onPrepared: (mVideoWidth != 0 && mVideoHeight != 0)");
        // Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
        // getHolder().setFixedSize(mVideoWidth, mVideoHeight);
        // if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
          // We didn't actually change the size (it was already at the size
          // we need), so we won't get a "surface changed" callback, so
          // start the video here instead of in the callback.
        if (mTargetState == STATE_PLAYING) {
          Log.d(TAG,
              "mPreparedListener, onPrepared: (mVideoWidth != 0 && mVideoHeight != 0)"
                  + ", (mTargetState == STATE_PLAYING) start()");
          start();
          if (mMediaController != null) {
            mMediaController.show();
          }
        } else if (!isPlaying() &&
            (seekToPosition != 0 || getCurrentPosition() > 0)) {
          Log.d(TAG,
              "mPreparedListener, onPrepared: (mVideoWidth != 0 && mVideoHeight != 0)"
                  + ", (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0))");
          if (mMediaController != null) {
            // Show the media controls when we're paused into a video and make 'em stick.
            mMediaController.show(0);
          }
        }
      } else {
        // We don't know the video size yet, but should start anyway.
        // The video size might be reported to us later.
        if (mTargetState == STATE_PLAYING) {
          Log.d(TAG, "mPreparedListener, onPrepared: start()");
          start();
        }
      }
    }
  };

  private final MediaPlayer.OnCompletionListener mCompletionListener =
      new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
          Log.d(TAG, "mCompletionListener, onCompletion: ");
          mCurrentState = STATE_PLAYBACK_COMPLETED;
          mTargetState = STATE_PLAYBACK_COMPLETED;
          if (mMediaController != null) {
            mMediaController.hide();
          }
          if (mOnCompletionListener != null) {
            Log.d(TAG,
                "mCompletionListener, onCompletion: call mOnCompletionListener.onCompletion ");
            mOnCompletionListener.onCompletion(mMediaPlayer);
          }
        }
  };

  private final MediaPlayer.OnInfoListener mInfoListener =
      new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
          if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(mp, what, extra);
          }
          return true;
        }
      };

  private final MediaPlayer.OnErrorListener mErrorListener =
      new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
          Log.d(TAG, "Error: " + what + "," + extra);
          mCurrentState = STATE_ERROR;
          mTargetState = STATE_ERROR;
          if (mMediaController != null) {
            mMediaController.hide();
          }

          /* If an error handler has been supplied, use it and finish. */
          if (mOnErrorListener != null) {
            if (mOnErrorListener.onError(mMediaPlayer, what, extra)) {
              return true;
            }
          }
          return true;
        }
      };

  private final MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
      new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
          mCurrentBufferPercentage = percent;
        }
      };

  private final MediaPlayer.OnSeekCompleteListener mSeekCompletionListener =
      new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
          if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(mp);
          }
        }
      };

  /**
   * Register a callback to be invoked when the media file is loaded and ready to go.
   *
   * @param l The callback that will be run
   */
  public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)
  {
    mOnPreparedListener = l;
  }

  /**
   * Register a callback to be invoked when the end of a media file has been reached during
   * playback.
   *
   * @param l The callback that will be run
   */
  public void setOnCompletionListener(OnCompletionListener l)
  {
    mOnCompletionListener = l;
  }

  /**
   * Register a callback to be invoked when an error occurs during playback or setup. If no listener
   * is specified, or if the listener returned false, VideoView will inform the user of any errors.
   *
   * @param l The callback that will be run
   */
  public void setOnErrorListener(OnErrorListener l)
  {
    mOnErrorListener = l;
  }

  /**
   * Register a callback to be invoked when an informational event occurs during playback or setup.
   *
   * @param l The callback that will be run
   */
  public void setOnInfoListener(OnInfoListener l) {
    mOnInfoListener = l;
  }

  public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
    mOnSeekCompleteListener = l;
  }

  public void setSurface(Surface surface) {
    Log.d(TAG, "setSurface: surface = " + surface);
    mSurface = surface;
    if (mSurface != null) {
      openVideo();
    } else {
      release(true);
    }
  }

  public void surfaceChanged() {
    if (mMediaPlayer != null)
    {
      mMediaPlayer.setSurface(null);
      mMediaPlayer.setSurface(mSurface);
    }
  }
  /*
   * SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
   * @Override public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
   * mSurfaceWidth = w; mSurfaceHeight = h; boolean isValidState = (mTargetState == STATE_PLAYING);
   * boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h); if (mMediaPlayer != null &&
   * isValidState && hasValidSize) { if (mSeekWhenPrepared != 0) { seekTo(mSeekWhenPrepared); }
   * start(); } }
   * @Override public void surfaceCreated(SurfaceHolder holder) { mSurfaceHolder = holder;
   * openVideo(); }
   * @Override public void surfaceDestroyed(SurfaceHolder holder) { // after we return from this we
   * can't use the surface any more mSurfaceHolder = null; if (mMediaController != null)
   * mMediaController.hide(); release(true); } };
   */

  /*
   * release the media player in any state
   */
  private void release(boolean cleartargetstate) {
    Log.d(TAG, "release, cleartargetstate = " + cleartargetstate);
    if (mMediaPlayer != null) {
      Log.d(TAG, "release, cleartargetstate = " + cleartargetstate
          + ", call mMediaPlayer.reset and mMediaPlayer.release");
      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
      // mPendingSubtitleTracks.clear();
      mCurrentState = STATE_IDLE;
      if (cleartargetstate) {
        mTargetState = STATE_IDLE;
      }
      // AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
      // am.abandonAudioFocus(null);
    }
  }

  /*
   * @Override public boolean onTouchEvent(MotionEvent ev) { if (isInPlaybackState() &&
   * mMediaController != null) { toggleMediaControlsVisiblity(); } return false; }
   * @Override public boolean onTrackballEvent(MotionEvent ev) { if (isInPlaybackState() &&
   * mMediaController != null) { toggleMediaControlsVisiblity(); } return false; }
   * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { boolean isKeyCodeSupported =
   * keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode !=
   * KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE && keyCode !=
   * KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode !=
   * KeyEvent.KEYCODE_ENDCALL; if (isInPlaybackState() && isKeyCodeSupported && mMediaController !=
   * null) { if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode ==
   * KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) { if (mMediaPlayer.isPlaying()) { pause();
   * mMediaController.show(); } else { start(); mMediaController.hide(); } return true; } else if
   * (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) { if (!mMediaPlayer.isPlaying()) { start();
   * mMediaController.hide(); } return true; } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP ||
   * keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) { if (mMediaPlayer.isPlaying()) { pause();
   * mMediaController.show(); } return true; } else { toggleMediaControlsVisiblity(); } } return
   * true; // return super.onKeyDown(keyCode, event); }
   */
  private void toggleMediaControlsVisiblity() {
    if (mMediaController.isShowing()) {
      mMediaController.hide();
    } else {
      mMediaController.show();
    }
  }

  public void start() {
    Log.d(TAG, "start:");
    if (isInPlaybackState()) {
      Log.d(TAG, "start: call mMediaPlayer.start, mCurrentState = STATE_PLAYING");
      mMediaPlayer.start();
      mCurrentState = STATE_PLAYING;
    }
    Log.d(TAG, "start: mTargetState = STATE_PLAYING");
    mTargetState = STATE_PLAYING;
  }

  public void setPlayMode(PlayerSpeed speed) {
    Log.d(TAG, "setPlayMode:");
    if (isInPlaybackState()) {
      Log.d(TAG, "setPlayMode: call mMediaPlayer.setPlayMode, mCurrentState = STATE_PLAYING");
      mMediaPlayer.setPlayMode(speed);
      mCurrentState = STATE_PLAYING;
    }
    Log.d(TAG, "setPlayMode: mTargetState = STATE_PLAYING");
    mTargetState = STATE_PLAYING;
  }

  public void pause() {
    Log.d(TAG, "pause:");
    if (isInPlaybackState()) {
      if (mMediaPlayer.isPlaying()) {
        Log.d(TAG, "pause: call mMediaPlayer.pause, mCurrentState = STATE_PAUSED");
        mMediaPlayer.pause();
        mCurrentState = STATE_PAUSED;
      }
    }
    Log.d(TAG, "pause: mTargetState = STATE_PAUSED");
    mTargetState = STATE_PAUSED;
  }

  public void suspend() {
    Log.d(TAG, "suspend: call release(false)");
    release(false);
  }

  public void resume() {
    Log.d(TAG, "resume: call openVideo()");
    openVideo();
  }

  public int getDuration() {
    if (isInPlaybackState()) {
      return mMediaPlayer.getDuration();
    }

    return -1;
  }

  public int getCurrentPosition() {
    if (isInPlaybackState()) {
      return mMediaPlayer.getCurrentPosition();
    }
    return 0;
  }

  public void seekTo(int msec) {
    Log.d(TAG, "seekTo:");
    if (isInPlaybackState()) {
      Log.d(TAG, "seekTo: call mMediaPlayer.seekTo(msec = " + msec + "), mSeekWhenPrepared = 0");
      mMediaPlayer.seekTo(msec);
      mSeekWhenPrepared = 0;
    } else {
      Log.d(TAG, "seekTo:, mSeekWhenPrepared = " + msec);
      mSeekWhenPrepared = msec;
    }
  }

  public int setUnLockPin(int pin) {
    Log.d(TAG, "setUnLockPin(pin = " + pin + ")");
    if (mMediaPlayer != null) {
      Log.d(TAG, "setUnLockPin: call mMediaPlayer.setUnLockPin(pin = " + pin
          + ")");
      return mMediaPlayer.setUnLockPin(pin);
    }
    return -1;
  }

  /**
   * This API will turn on subtitle track.
   *
   * @param value 1:open, 0:close.
   * @return true:sucees, false:fail.
   */

  public boolean onSubtitleTrack(int value)
  {
    if (mMediaPlayer != null) {
      if (value == 1) {
        return mMediaPlayer.onSubtitleTrack();
      } else if (value == 0) {
        return mMediaPlayer.offSubtitleTrack();
      }
    }
    return false;
  }

  public boolean selectTrack(int type, String trackId) {
    if (mMediaPlayer != null) {
      int trackIndex = 0;
      int trackIdIndex = 0;
      int mtkTrackType = -1;
      MtkTrackInfo[] tracks = mMediaPlayer.mtkGetTrackInfo();

      if(type == TvTrackInfo.TYPE_AUDIO) {
        mtkTrackType = MtkTrackInfo.MEDIA_TRACK_TYPE_AUDIO;
      } else if(type == TvTrackInfo.TYPE_SUBTITLE) {
        mtkTrackType = MtkTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT;
      } else if (type == TvTrackInfo.TYPE_VIDEO) {
        mtkTrackType = MtkTrackInfo.MEDIA_TRACK_TYPE_VIDEO;
      }

      if (tracks != null && tracks.length > 0) {
        for (MtkTrackInfo info : tracks) {
          if (info.getTrackType() == mtkTrackType) {
            Log.d(TAG, "selectTrack mtkTrackType trackIdIndex = " + trackIdIndex);
            if (trackIdIndex == Integer.parseInt(trackId)) {
              Log.d(TAG, "selectTrack mtkTrackType call mMediaPlayer.selectTrack(trackIndex = "
                  + trackIndex + ")");
              mMediaPlayer.selectTrack(trackIndex);
              return true;
            }
            trackIdIndex++;
          }
          trackIndex++;
        }
      }
      Log.d(TAG, "selectTrack mMediaPlayer!=null, return false");
      return false;
    }
    Log.d(TAG, "selectTrack mMediaPlayer==null, return false");
    return false;
  }

  public List<TvTrackInfo> getAllTrackInfo() {
    List<TvTrackInfo> track_list = new ArrayList<TvTrackInfo>();
    if (mMediaPlayer != null) {
      MtkTrackInfo[] tracks = mMediaPlayer.mtkGetTrackInfo();
      MtkTrackInfo audio_base = null;
      MtkTrackInfo subtitle_base = null;
      int trackIndex = 0;

      if (tracks != null && tracks.length > 0) {
        trackIndex = 0;
        for (MtkTrackInfo info : tracks) {
          if (info.getTrackType() == MtkTrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
            Log.d(TAG, "getAllTrackInfo MEDIA_TRACK_TYPE_AUDIO index = " + trackIndex);
            // audio_base = info;
            if (info.getFormat() != null) {
              Log.d(TAG, "getAllTrackInfo MEDIA_TRACK_TYPE_AUDIO info = " + info.toString());
            }
            {
              TvTrackInfo audioTrack = new TvTrackInfo.Builder(TvTrackInfo.TYPE_AUDIO,
                  Integer.toString(trackIndex))
                  .setLanguage(info.getLanguage())
                  .build();
              track_list.add(audioTrack);
            }
            trackIndex++;
          }
        }
      }

      if (tracks != null && tracks.length > 0) {
        trackIndex = 0;
        for (MtkTrackInfo info : tracks) {
          if (info.getTrackType() == MtkTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT ||
              info.getTrackType() == MtkTrackInfo.MEDIA_TRACK_TYPE_SUBTITLE) {
            Log.d(TAG,
                "getAllTrackInfo MEDIA_TRACK_TYPE_TIMEDTEXT/MEDIA_TRACK_TYPE_SUBTITLE index = "
                    + trackIndex);
            // subtitle_base = info;
            if (info.getFormat() != null) {
              Log.d(TAG,
                  "getAllTrackInfo MEDIA_TRACK_TYPE_TIMEDTEXT/MEDIA_TRACK_TYPE_SUBTITLE info = "
                      + info.toString());
            }
            {
              TvTrackInfo subtitleTrack = new TvTrackInfo.Builder(TvTrackInfo.TYPE_SUBTITLE,
                  Integer.toString(trackIndex))
                  .setLanguage(info.getLanguage())
                  .build();
              track_list.add(subtitleTrack);
            }
            trackIndex++;
          }
        }
      }
      return track_list;
    }
    return null;
  }

  public boolean isPlaying() {
    return isInPlaybackState() && mMediaPlayer.isPlaying();
  }

  public int getBufferPercentage() {
    if (mMediaPlayer != null) {
      return mCurrentBufferPercentage;
    }
    return 0;
  }

  private boolean isInPlaybackState() {
    return (mMediaPlayer != null &&
        mCurrentState != STATE_ERROR &&
        mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
  }

  public boolean canPause() {
    return mCanPause;
  }

  public boolean canSeekBackward() {
    return mCanSeekBack;
  }

  public boolean canSeekForward() {
    return mCanSeekForward;
  }

  public int getAudioSessionId() {
    if ((mAudioSession == 0)
        && (mMediaPlayer != null)) {
      // MediaPlayer foo = new MediaPlayer();
      // mAudioSession = foo.getAudioSessionId();
      // foo.release();
      mAudioSession = mMediaPlayer.getAudioSessionId();
    }
    return mAudioSession;
  }
}
