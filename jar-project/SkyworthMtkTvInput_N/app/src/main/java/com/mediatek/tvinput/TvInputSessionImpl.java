
package com.mediatek.tvinput;

import android.net.Uri;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;

public abstract class TvInputSessionImpl {
  /**
   * Called when the session is released.
   */
  public abstract void onRelease();

  /**
   * Sets the {@link Surface} for the current input session on which the TV input renders video.
   *
   * @param surface {@link Surface} an application passes to this TV input session.
   * @return {@code true} if the surface was set, {@code false} otherwise.
   */
  public abstract boolean onSetSurface(Surface surface);

  /**
   * Sets the relative volume of the current TV input session to handle the change of audio focus by
   * setting.
   *
   * @param volume Volume scale from 0.0 to 1.0.
   */
  public abstract void onSetVolume(float volume);

  /**
   * Tunes to a given channel.
   *
   * @param channelUri The URI of the channel.
   * @return {@code true} the tuning was successful, {@code false} otherwise.
   */
  public abstract boolean onTune(Uri channelUri);

  /**
   * Called when an application requests to create the overlay view.
   *
   * @return a view attached to the overlay window
   */
  public View onAttachOverlayView() {

    return null;
  }

  /**
   * Called when an application requests to remove the overlay view.
   */
  public void onDetachOverlayView() {
  }

  /**
   * Dispatches a key event to its subclass.
   *
   * @param event The key event to be dispatched.
   * @return true if the event was handled, false otherwise.
   */
  public boolean dispatchKeyEvent(KeyEvent event) {
    return false;
  }
}
