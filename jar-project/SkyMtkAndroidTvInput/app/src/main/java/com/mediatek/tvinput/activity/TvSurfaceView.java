/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.mediatek.tvinput.activity;

import android.content.Context;
import android.media.tv.TvInputManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static com.mediatek.tvinput.DebugTag.DEBUG_TAG;

/**
 * SurfaceView to show TV through TvInputServiceSession.
 *
 * @hide
 */
public class TvSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
  private TvInputManager.Session mSession;
  private boolean mInputAvailable;
  private Surface mSurface;

  public TvSurfaceView(Context context) {
    super(context);
    init();
  }

  public TvSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TvSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    getHolder().addCallback(this);
  }

  public void setTvInputSession(TvInputManager.Session session) {
    Log.d(DEBUG_TAG, "setTvInputSession session=" + session);
    release();
    mSession = session;
    if (mSession == null) {
      return;
    }
    mInputAvailable = true;
    setSurface();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (changed) {
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.d(DEBUG_TAG, "surfaceChanged(holder=" + holder + ", format=" + format + ", width=" + width
        + ", height=" + height + ")");
    if (holder.getSurface() == mSurface) {
      return;
    }
    if (mSurface != holder.getSurface()) {
      mSurface = holder.getSurface();
      if (mInputAvailable) {
        setSurface();
      }
    }
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    mSurface = holder.getSurface();
    Log.d(DEBUG_TAG, "surfaceCreated(mSurface=" + mSurface + ")");
    if (mInputAvailable) {
      setSurface();
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    mSurface = null;
    clearSurface();
  }

  public void updateSessionAvailability(boolean available) {
    if (mInputAvailable == available) {
      return;
    }
    if (available) {
      mInputAvailable = true;
      setSurface();
    } else {
      clearSurface();
      mInputAvailable = false;
    }
  }

  private void release() {
    clearSurface();
    mSession = null;
    mInputAvailable = false;
  }

  private void setSurface() {
    Log.d(DEBUG_TAG, "setSurface " + mSurface + " mInputAvailable=" + mInputAvailable);
    if (mSession == null || !mInputAvailable || mSurface == null) {
      return;
    }
    mSession.setSurface(mSurface);
  }

  // It calls setSurface and waits until Callback is received. So it may take some time
  // up to SET_SURFACE_TIMEOUT_MILLIS.
  private void clearSurface() {
    if (mSession == null || !mInputAvailable) {
      return;
    }
  }
}
