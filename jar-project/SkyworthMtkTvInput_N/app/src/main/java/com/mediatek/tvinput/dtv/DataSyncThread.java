
package com.mediatek.tvinput.dtv;

//import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;//TODO Need twoworld API

import android.content.ContentResolver;
import android.util.Log;
import android.os.HandlerThread;
import android.os.Process;

public abstract class DataSyncThread extends Thread /* implements ISyncNotify */{
  protected static String TAG = "MtkTvInput(DataSyncThread)";
  private volatile boolean stop = false;
  protected ContentResolver contentResolver;
  protected HandlerThread mThread = null;
  private volatile boolean monitor = true;

  // protected MtkTvTVCallbackHandler tvCallback = null;//TODO Need twoworld API

  protected abstract void processSync();

  public DataSyncThread(String name, ContentResolver contentResolver) {
    super(name);
    this.contentResolver = contentResolver;
  }

  @Override
  public void run() {
    mThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_DEFAULT);
    mThread.start();

    processSync();
    Log.d(TAG, "Stop thread " + getName());
  }

  public boolean isMonitor() {
    return monitor;
  }

  public synchronized void setMonitor(boolean ismonitor) {
    this.monitor = ismonitor;
  }

  public void Stop() {
    Log.d(TAG,
        "Stop, HandlerThread = " + mThread.getName() + ", HandlerThread = " + mThread.getId());

    mThread.quit();
    mThread = null;
  }

  public boolean isStop() {
    return stop;
  }

  public synchronized void setStop(boolean isStop) {
    this.stop = isStop;
  }
}
