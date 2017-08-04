
package com.mediatek.tvinput.dtv;

import android.content.ContentResolver;
import android.os.RemoteException;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class ProgramCallbackHandler extends MtkTvTVCallbackHandler {
  private static String TAG = "MtkTvInput(ProgramCallbackHandler)";
  private final ProgramSyncThread programSyncThread;

  public ProgramCallbackHandler(ProgramSyncThread programSyncThread) {
    this.programSyncThread = programSyncThread;
  }

  @Override
  public int notifyEventNotification(int updateType, int argv1, int argv2, long argv3)
      throws RemoteException {
    Log.d(TAG, "notifyEventNotification " + updateType + " " + argv1 + " " + argv2 + " " + argv3);
    programSyncThread.notifyEvent(updateType, argv1, argv2, argv3);
    return 0;
  }
}

public class ProgramSyncThread extends DataSyncThread {
  private static String TAG = "MtkTvInput(ProgramlSyncThread)";
  private ProgramPump programPump = null;
  private ProgramCallbackHandler tvCallback = null;
  private Handler mHandler;
  private boolean b_time_start = false;
  private final TunerInputService service;
  // private Timer timer ;
  private final ScheduledExecutorService pool;
  private final MtkTvTimeBase mTime = MtkTvTime.getInstance();
  private final TimerTask task = new TimerTask() {
    @Override
    public void run() {
      try
      {
        if (mThread == null || mHandler == null) {
          Log.d(TAG, "mThread or mHandler is null.wrong ");
          return;
        }

        Log.d(TAG, "  timer is triggerd again. ***********************");
        Message msg = Message.obtain();
        msg.obj = "8";
        mHandler.sendMessage(msg);
      }
      catch (RuntimeException e)
      {
        e.printStackTrace();
      }
    }
  };

  public ProgramSyncThread(String name, ContentResolver contentResolver, TunerInputService service){

    super(name, contentResolver);
    Log.d(TAG, "ProgramSyncThread begin constrcut<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    this.service = service;
    tvCallback = new ProgramCallbackHandler(this);
    programPump = new ProgramPump(contentResolver, this.service);
    // timer = new Timer();
    pool = Executors.newScheduledThreadPool(1);

  }

  @Override
  protected void processSync() {
    Log.d(TAG, "processSync begin<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    Looper.prepare();

    if (mThread == null) {
      Log.d(TAG, "mThread == null");
      return;
    }

    mHandler = new Handler(mThread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        String msgId = (String) msg.obj;
        Log.d(TAG, ">>>>>>>>>>>>>>>get a message id " + msgId);

        // active window changed,sync it first
        if (msgId.equals("4"))
        {
          Log.d(TAG, "current PF program update,the index  " + msg.arg2);
          programPump.syncPFProgramByChannel(msg.arg1, msg.arg2);
        }
        else if (msgId.equals("5"))
        {
          Log.d(TAG, "notify active window update svl " + msg.arg1 + "channel id" + msg.arg2);
          programPump.syncActiveWindowProgramByChannel(msg.arg2);
        }
        else if (msgId.equals("6"))
        {
          Log.d(TAG, "notifyEvent is ative window changed " + msg.arg1);
          programPump.syncActiveWindowProgram(msg.arg1);
        }
        else if (msgId.equals("7"))
        {
          Log.d(TAG, "currrent channel is changed,need update current PF ");
          programPump.syncPFProgramByChannel(msg.arg1, 1);
          programPump.syncPFProgramByChannel(msg.arg1, 0);
        }
        else if (msgId.equals("8"))
        {
          Log.d(TAG, "need to expire data.timer message is received ");
          MtkTvTimeFormatBase timeFormat = mTime.getBroadcastTime();
          programPump.deleteChannelProgrambytime(timeFormat.toMillis());
        }

        if (isStop())
        {
          Log.d(TAG, "need exit thread >>>>>>>>>>");
          exit();
        }
        else
        {
          if (b_time_start == false)
          {
            Log.d(TAG, "schedule a timer lalalalalala^^^^^^^^^^^^^^^^^^^" + getName());
            pool.scheduleAtFixedRate(task, 0, 3, TimeUnit.HOURS);
            b_time_start = true;
          }
          Log.d(TAG, "process Sync task@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + getName());
        }
      }
    };
  }

  public void notifyEvent(int updateType, int argv1, int argv2, long argv3) {
    Log.d(TAG, "notifyEvent " + updateType);

    if (mThread == null || mHandler == null) {
      Log.d(TAG, "mThread or mHandler is null.wrong ");
      return;
    }

    if (updateType == 6) {
      mHandler.removeMessages(0x1234);
      // remove message in the message queue
      Log.d(
          TAG,
          "active window is set again, "
          + "remove all the message in the queue which belong to last active window ");
    }

    Log.d(TAG, "notifyEvent is recerived, " + updateType + "." + argv1 + "." + argv2 + "." + argv3);
    Message msg = Message.obtain();
    msg.what = 0x1234;
    msg.arg1 = argv1;
    msg.arg2 = argv2;
    msg.obj = Integer.toString(updateType);
    mHandler.sendMessage(msg);

    Log.d(TAG, "send message finished ");

    // TODO Auto-generated method stub
  }

  public void notifyChannel(int channelId) {
    Log.d(TAG,
        "notifyChannel will update pf for this channelid  " + channelId);

    // TODO Auto-generated method stub
    if (mThread == null || mHandler == null) {
      Log.d(TAG, "mThread or mHandler is null.wrong ");
      return;
    }

    Message msg = Message.obtain();
    msg.arg1 = channelId;
    msg.obj = "7";
    mHandler.sendMessage(msg);

    Log.d(TAG, "send channnel message finished ");

  }

  public void notifyChannel(MtkTvChannelInfoBase mtkTvChannelInfo) {
    Log.d(TAG,
        "notifyChannel will update pf for this channelid  " + mtkTvChannelInfo.getChannelId());

    // TODO Auto-generated method stub
    if (mThread == null || mHandler == null) {
      Log.d(TAG, "mThread or mHandler is null.wrong ");
      return;
    }

    Message msg = Message.obtain();
    msg.arg1 = mtkTvChannelInfo.getChannelId();
    msg.obj = "7";
    mHandler.sendMessage(msg);

    Log.d(TAG, "send channnel message finished ");

  }

  public void exit() {
    Log.d(TAG, "exit thread***********************");
    if (mThread != null) {
      // timer.cancel();
      pool.shutdown();
      mThread.quit();
      mThread = null;
      Log.d(TAG, "exit thread,quit ***********************");
    }
  }

  /*
   * public void setTimer(){ Log.d(TAG,"set a timer to do expire ***********************");
   * timer.schedule(new TimerTask(){ public void run(){ if (mThread == null || mHandler == null) {
   * Log.d(TAG,"mThread or mHandler is null.wrong "); return; }
   * Log.d(TAG,"  timer is triggerd again. ***********************"); Message msg =
   * Message.obtain(); msg.obj = "8"; mHandler.sendMessage(msg); } },1000); }
   */

}
