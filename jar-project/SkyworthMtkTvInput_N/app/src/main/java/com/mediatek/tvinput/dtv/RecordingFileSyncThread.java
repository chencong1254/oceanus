package com.mediatek.tvinput.dtv;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvContract;
import android.content.ContentResolver;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.ContentObserver;

//import com.mediatek.tvinput.dtv.RecordingFilePump;
import com.mediatek.tvinput.dtv.TunerInputService;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.MtkTvRecordBase;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordNotifyMsgType;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordDeviceStatus;
import com.mediatek.twoworlds.tv.MtkTvPvrBrowserBase;
import com.mediatek.twoworlds.tv.model.MtkTvPvrBrowserItemBase;

class RecordingCallbackHandler extends MtkTvTVCallbackHandler {
    private static String TAG = "MtkTvInput(RFCallbackHandler)";
    private final RecordingFileSyncThread recordingSyncThread;

    public RecordingCallbackHandler(RecordingFileSyncThread recordingSyncThread) {
        this.recordingSyncThread = recordingSyncThread;
    }
    
    public int notifyRecordNotification(int updateType, int argv1, int argv2)
            throws RemoteException {
        Log.d(TAG, "notifyRecordNotification type=" + updateType + " argv1="
                + argv1 + " argv2=" + argv2);
        switch(RecordNotifyMsgType.values()[updateType])
        {
            case RECORD_PVR_NTFY_VIEW_INSERT_PVR_FILE:
                Log.d(TAG, "receive pvr file");
                recordingSyncThread.notifyInsertFile();
                break;
            case RECORD_PVR_NTFY_VIEW_STATUS_STOPPED:
                Log.d(TAG, "receive recording stop");
                recordingSyncThread.notifyRecordingStopped();
                break;
            case RECORD_PVR_NTFY_VIEW_UPDATE_EVERY_5_SECONDS:
                Log.d(TAG, "receive updated 5 seconds "+ argv1 + " " + argv2);
                recordingSyncThread.notifyUpdateDuration(argv1, argv2);
                break;
            case RECORD_PVR_NTFY_VIEW_STRG_STATUS_CHANGE:
                Log.d(TAG, "receive device change msg");
                recordingSyncThread.notifyDeviceChange(argv1);
                break;
            default:
                break;
        }
        return 0;
    }
}

class RecordingFileSyncThread extends DataSyncThread {
    public static String TAG = "MtkTvInput(RFSyncThread)";
    public static final String tvprovidrVer = "00100";
    private final static int MSG_NOTIFY_DEVICE_CHANGE = 0;
    private final static int MSG_NOTIFY_INSERT_FILE = 1;
    private final static int MSG_NOTIFY_RECORDING_STOPPED = 2;
    private final static int MSG_NOTIFY_UPDATE_DURATION = 3;
    private final static int MSG_NOTIFY_CONTENT_UPDATE = 4;
    private final static int MSG_DELAY_TIME = 1000;
    private Handler mHandler;
    private final TunerInputService service;
    private RecordingCallbackHandler recordingCallback = null;

    private class RecordingFileContentObserver extends ContentObserver {
        public RecordingFileContentObserver(Handler handler){
            super(handler);
        }
        
        @Override
        public void onChange(boolean selfChange){
            Log.d(TAG, "find content change message");
            notifyContentUpdate();
        }
            
    }

    public RecordingFileSyncThread(String name, ContentResolver contentResolver, TunerInputService service){
        super(name, contentResolver);
        this.service = service;
        recordingCallback = new RecordingCallbackHandler(this);
        Log.d(TAG, "constructed function");
    }
    
    @Override
    protected void processSync() {
        Looper.prepare();
        if (mThread == null) {
            Log.d(TAG, "mThread == null");
            return;
        }

        final RecordingFilePump rp = new RecordingFilePump();
    
        mHandler = new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "enter handle message " + msg.what);
        
                switch (msg.what)
                {
                    case MSG_NOTIFY_DEVICE_CHANGE:
                        rp.resetSync(contentResolver);
                        break;
                    case MSG_NOTIFY_INSERT_FILE:
                        //rp.insertRecordingFile(contentResolver);
                        break;
                    case MSG_NOTIFY_RECORDING_STOPPED:
                        //rp.updateRecordingFile(contentResolver);
                        break;
                    case MSG_NOTIFY_UPDATE_DURATION:
                        //rp.updateRecordingFileDuration(contentResolver, (int)msg.arg1, (int)msg.arg2);
                        break;
                    case MSG_NOTIFY_CONTENT_UPDATE:
                        rp.mergeSync(contentResolver);
                        break;
                    default:
                        break;
                }
            }
        };        

        contentResolver.registerContentObserver(TvContract.RecordedPrograms.CONTENT_URI, 
            true, new RecordingFileContentObserver(mHandler));
    }

    public void notifyDeviceChange(int devStatus)
    {
        Message msg = Message.obtain();
        msg.what = MSG_NOTIFY_DEVICE_CHANGE;
        msg.arg1 = msg.arg2 = devStatus;
        Log.d(TAG, "mHandler.sendMessage(msg) msg.what= " + msg.what);
        mHandler.sendMessage(msg);
    }
    
    public void notifyInsertFile(){
        Message msg = Message.obtain();
        msg.what = msg.arg1 = msg.arg2 = MSG_NOTIFY_INSERT_FILE;
        Log.d(TAG, "mHandler.sendMessage(msg) msg.what= " + msg.what);
        mHandler.sendMessage(msg);
    }
    public void notifyRecordingStopped(){
        Message msg = Message.obtain();
        msg.what = msg.arg1 = msg.arg2 = MSG_NOTIFY_RECORDING_STOPPED;
        Log.d(TAG, "mHandler.sendMessage(msg) msg.what= " + msg.what);
        mHandler.sendMessage(msg);
    }
    public void notifyUpdateDuration(int handle, int duration){
        Message msg = Message.obtain();
        msg.what = MSG_NOTIFY_UPDATE_DURATION;
        msg.arg1 = handle;
        msg.arg2 = duration;
        Log.d(TAG, "mHandler.sendMessage(msg) msg.what= " + msg.what);
        mHandler.sendMessage(msg);
    }
    public void notifyContentUpdate(){
        Message msg = Message.obtain();
        msg.what = msg.arg1 = msg.arg2 = MSG_NOTIFY_CONTENT_UPDATE;
        Log.d(TAG, "mHandler.sendMessage(msg) msg.what= " + msg.what);
        mHandler.sendMessage(msg);
    }    
};
