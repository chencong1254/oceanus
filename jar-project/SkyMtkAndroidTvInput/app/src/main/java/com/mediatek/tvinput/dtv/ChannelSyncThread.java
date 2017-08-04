package com.mediatek.tvinput.dtv;

import android.content.ContentResolver;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;

import android.os.RemoteException;
import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;

import com.mediatek.twoworlds.tv.model.TvProviderChannelEventBase;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.model.TvProviderChannelInfoBase;
import com.mediatek.tvinput.dtv.ChannelPump;
import com.mediatek.tvinput.dtv.ProgramPump;
import com.mediatek.tvinput.dtv.CurrentChannelPump;
import com.mediatek.tvinput.dtv.OneChannelPump;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.SparseArray;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;

import android.media.tv.TvContract;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvStreamConfig;

import com.mediatek.tvinput.Channel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

class ChannelCallbackHandler extends MtkTvTVCallbackHandler {
    private static String TAG = "MtkTvInput(ChannelCallbackHandler)";
    private ChannelSyncThread channelSyncThread;

    public static final int SCAN_Unknow     = 0;
    public static final int SCAN_COMPLETE   = 1;
    public static final int SCAN_PROGRESS   = 2;
    public static final int SCAN_CANCEL     = 4;
    public static final int SCAN_ABORT      = 8;

    public ChannelCallbackHandler(ChannelSyncThread channelSyncThread) {
        this.channelSyncThread = channelSyncThread;
    }

    /**
     * This method is used to notify scan information. Please override this function and perform your behavior. This
     * method is used to notify scan apk.
     *
     * @param msg_id
     *            0: Scan message Unknow 1: Scan message COMPLETE 2: Scan message PROGRESS 4: Scan message CANCEL 8:
     *            Scan message ABORT
     *
     * @param scanProgress
     *            , scan progress,correct range is 0-100
     * @param channelNum
     *            , channel num find by scan,correct range is 0-9999
     * @param argv4
     *            , reserved,not used
     *
     * @return 0: callback success. others: callback fail.
     */
    @Override
    public int notifyScanNotification(int msg_id, int scanProgress, int channelNum, int argv4) throws RemoteException {
        Log.d(TAG, "notifyScanNotification: msg_id=" + msg_id + ", scanProgress=" + scanProgress
             + ", channelNum=" + channelNum + ", argv4=" + argv4);
        
        if((msg_id == SCAN_PROGRESS) &&
            (scanProgress>0) &&
            (scanProgress<100))
        {
            if(channelSyncThread.getMonitorChannelUpdateStatus() == true)
            {
                Log.d(TAG, "Scanprogress, current everyMonitor=" + channelSyncThread.getMonitorChannelUpdateStatus() + ", set everyMonitor to false. ");
                channelSyncThread.stopMonitorChannelUpdate();
            }
        }

        if(((msg_id == SCAN_CANCEL) || (msg_id == SCAN_COMPLETE)) &&
            (channelNum>0))
        {
            Log.d(TAG, "channelSyncThread.notifyResetSync(), msg_id:" + msg_id + ", channelNum:" + channelNum);
            channelSyncThread.notifyResetSync();
        }

        if(((msg_id == SCAN_CANCEL) || (msg_id == SCAN_COMPLETE))
            && (channelNum == 0))
        {
            Log.d(TAG, "set everyMonitor to true, msg_id:" + msg_id + ", channelNum:" + channelNum);
            channelSyncThread.startMonitorChannelUpdate();
        }

        return 0;
    }

    @Override
    public int notifyTvproviderUpdateMsg(int svlId, int count, int[] eventType, int[] svlRecId) throws RemoteException {
        Log.d(TAG, "sync notifyTvproviderUpdateMsg: svlId = " + svlId + ", count = " + count+ "\n");
        channelSyncThread.notifyTvprovider(svlId, count, eventType, svlRecId);
        return 0;
    }

    @Override
    public int notifySvlIdUpdateMsg(int svlId, int reason, int data) throws RemoteException {
        Log.d(TAG, "sync notifySvlIdUpdateMsg: svlId = " + svlId + "\n");
        channelSyncThread.notifySvlIdUpdate(svlId);
        return 0;
    }

    @Override
    /* callback to recieve channelList mode change from Linux worlds. */
    public int notifyListModeUpdateMsg(int oldMode, int newMode) throws RemoteException {
        Log.d(TAG, "sync notifyListModeUpdateMsg: oldMode = " + oldMode + ", newMode = " + newMode);
        channelSyncThread.notifyListModeChange(oldMode, newMode);
        return 0;
    }

    /**
     * This method is used to notify the one channel list scan information. 
     *
     * @param msg_id
     *            0: Scan message Unknow 1: Scan message COMPLETE 2: Scan message PROGRESS 4: Scan message CANCEL 8:
     *            Scan message ABORT
     *
     * @param scanProgress
     *            , scan progress,correct range is 0-100
     * @param channelNum
     *            , channel num find by scan,correct range is 0-9999
     * @param argv4
     *            , reserved,not used
     *
     * @return 0: callback success. others: callback fail.
     */
    @Override
    public int notifyOclScanInfo(int msg_id, int channelNum)
    {
        Log.d(TAG, "informScanInfo msg_id=" + msg_id + ", channelNum=" + channelNum);
        channelSyncThread.notifyScanInfo(msg_id, channelNum); 
        return 0;
    }
}

public class ChannelSyncThread extends DataSyncThread {
    public  static  String TAG = "MtkTvInput(ChannelSyncThread)";
    public  static  final String tvprovidrVer = "00100";
    private Handler mHandler;
    private MtkTvConfig mtkConfig;
    private ProgramPump pp;
    private TunerInputService service;
    private ChannelCallbackHandler tvCallback = null;

    private CurrentChannelPump ccp = null;
    private OneChannelPump ocp = null;
    private ChannelPump cp = null;

    public static boolean monitorChannelUpdate = false;
    public static int currentSvlId = 0;
    private static int channelListMode = 0;     /* 0 is currentChannelList, 1 is oneChannelListMode */

    public static final int EVENT_EVERY_NOTIFY  = 1000;
    public static final int EVENT_SCAN_NOTIFY   = 2000;     /* For current channel list */
    public static final int EVENT_SVLID_UPDATE  = 3000;
    public static final int EVENT_MODE_CHANGE   = 4000;
    public static final int EVENT_SCAN_INFO     = 5000;     /* For one channel list */

    public static final int UPDATTE_EVENT_TYPE_CLEAN_DB     = 1;
    public static final int UPDATTE_EVENT_TYPE_LOAD_DB      = 2;
    public static final int UPDATTE_EVENT_TYPE_INSERT_REC   = 3;
    public static final int UPDATTE_EVENT_TYPE_UPDATE_REC   = 4;
    public static final int UPDATTE_EVENT_TYPE_DELETE_REC   = 5;

    public static final int SVL_ID_ANTENNA  = 1;
    public static final int SVL_ID_CABLE    = 2;

    public static final int SCAN_INFO_START     = 0;        /* For one channel list, informed by android ap. */
    public static final int SCAN_INFO_COMPLETE  = 1;
    public static final int SCAN_INFO_CANCEL    = 2;

    public static final int CHANNELLIST_MODE_CURRENT = 0;   /* current channellist mode */
    public static final int CHANNELLIST_MODE_ONE     = 1;   /* one channellist mode */

    public ChannelSyncThread(String name, ContentResolver contentResolver,TunerInputService service) {
        super(name, contentResolver);
        this.service = service;
        Log.d(TAG, "constructed function" );
        tvCallback = new ChannelCallbackHandler(this);        
        this.pp = new ProgramPump(contentResolver);
        this.ccp = new CurrentChannelPump(service, pp, contentResolver);
        this.ocp = new OneChannelPump(service, pp, contentResolver);
    }

    public void startMonitorChannelUpdate() 
    {
        monitorChannelUpdate = true;
        Log.d(TAG,"startMonitor, set monitorChannelUpdate to true." );
    }

    public void stopMonitorChannelUpdate() 
    {
        monitorChannelUpdate = false;
        Log.d(TAG,"stopMonitor, set monitorChannelUpdate to false." );
    }

    public boolean getMonitorChannelUpdateStatus() 
    {
        return monitorChannelUpdate;
    }
    
    public String getInputId(TunerInputService service, int mBrdcstType)
    {
        if (ChannelPump.currentSourceIdx == 0)
        {
            if (mBrdcstType == 1)        /* anology channel*/
            {
                return ChannelPump.ServiceInputIdAtv_ex;
            }
            else
            {
                return ChannelPump.ServiceInputIdDtv_ex;
            }
        }
        else
        {
            if (mBrdcstType == 1)        /* anology channel*/
            {
                return ChannelPump.ServiceInputIdAtv;
            }
            else
            {
                return ChannelPump.ServiceInputIdDtv;
            }
        }
    }

    /* ################################## Event handle ################################ */
    /* Just for normal scan. other than oneChannelList scan.*/
    public void scanNotifyHandle()
    {
        Log.d(TAG, "Enter scanNotifyHandle, channelListMode:" + channelListMode);
        
        if (channelListMode == CHANNELLIST_MODE_ONE)
        {   
            Log.d(TAG, "now channelListMode is OCL. don't handle the normal msg.");
            return ;
        }
        mtkConfig = MtkTvConfig.getInstance();
        int svlId = mtkConfig.getConfigValue(MtkTvConfigType.CFG_BS_SVL_ID);
        this.cp.resetSync(svlId,contentResolver);
    }
    
    public void svlIdUpdateHandle(int oldSvlId, int newSvlId)
    {
        Log.d(TAG, "Enter svlIdUpdateHandle, oldSvlId:" + oldSvlId + ", newSvlId:" + newSvlId);
        
        if (oldSvlId == newSvlId)
        {
            return;
        }

        mtkConfig = MtkTvConfig.getInstance();
        int svlIdCheck = mtkConfig.getConfigValue(MtkTvConfigType.CFG_BS_SVL_ID);
        if (newSvlId != svlIdCheck)
        {
            /* Based on newSvlId.*/
            Log.d(TAG, "ERROR: svlId from config is different from newSvlId, newSvlId:" + newSvlId + ", svlIdCheck:" + svlIdCheck); 
        }

        switch(channelListMode)
        {
            case CHANNELLIST_MODE_CURRENT: 
            {
                this.cp.resetSync(newSvlId, contentResolver);
                break;
            }

            case CHANNELLIST_MODE_ONE:
            {
                if (((oldSvlId == SVL_ID_ANTENNA)&&(newSvlId == SVL_ID_CABLE))||
                    ((oldSvlId == SVL_ID_CABLE)&&(newSvlId == SVL_ID_ANTENNA)))
                {
                    /* do noting. */
                }
                else
                {
                    this.cp.resetSync(newSvlId, contentResolver);
                }
                break;
            }

            default:
                Log.d(TAG, "ERROR: the current channellist mode is :" + channelListMode);
        }
    }

    public void modeChangeHandle(int currentMode, int newMode)
    {
        Log.d(TAG, "Enter modeChangeHandle, currentMode:" + currentMode + ", newMode:" + newMode);
        
        if (currentMode != newMode)
        {
            switch(currentMode)
            {
                case CHANNELLIST_MODE_CURRENT:
                    cp = this.ccp;
                    Log.d(TAG, "Set cp to ccp:current channelpump. ");
                    break;
                case CHANNELLIST_MODE_ONE:
                    cp = this.ocp;
                    Log.d(TAG, "Set cp to ocp:one channelpump. ");
                    break;
                default:
                    Log.d(TAG, "ERROR, currentMode shouldn't be " + currentMode);
            }
            cp.deactive();      /* oldMode deactive. wait to implecate.*/
            Log.d(TAG, "Done: cp.deactive(). ");

            switch(newMode)
            {
                case CHANNELLIST_MODE_CURRENT:
                    cp = this.ccp;
                    Log.d(TAG, "Set cp to ccp:current channelpump. ");
                    break;
                case CHANNELLIST_MODE_ONE:
                    cp = this.ocp;
                    Log.d(TAG, "Set cp to ocp:one channelpump. ");
                    break;
                default:
                    Log.d(TAG, "ERROR, newMode shouldn't be " + newMode);
            }
            cp.active();      /* oldMode deactive.  wait to implecate. */
            Log.d(TAG, "Done: cp.active(). ");
        }
        else
        {
            Log.d(TAG, "Do noting: currentMode is equal newMode:" + newMode);
        }
    }

    /* Just for the scan info under one channellist mode.*/
    public void scanInfoHandle(int msg_id, int channelNum)
    {
        Log.d(TAG, "scanInfoHandle start: msg_id = " + msg_id + ", channelNum = " + channelNum);
        
        switch (msg_id)
        {
            case SCAN_INFO_START:
            {
               if(getMonitorChannelUpdateStatus() == true)
               {
                   Log.d(TAG, "Scan started.");
                   stopMonitorChannelUpdate();
               } 
               break;
            }

            case SCAN_INFO_COMPLETE:
            case SCAN_INFO_CANCEL:
            {
                if (channelNum == 0)
                {
                    startMonitorChannelUpdate();
                }
                else if (channelNum > 0)
                {
                    int svlId = mtkConfig.getConfigValue(MtkTvConfigType.CFG_BS_SVL_ID);
                    cp.resetSync(svlId, contentResolver);
                    startMonitorChannelUpdate();
                }
                else
                {
                    Log.d(TAG, "ERROR, SCAN_COMPLETE or SCAN_INFO_CANCEL, but channelNum:" + channelNum);
                }
                break;
            }

            default:
               Log.d(TAG, "ERROR, SCAN info, msg_id:" + msg_id + ", channelNum:" + channelNum); 
        } 
        Log.d(TAG, "scanInfoHandle end: msg_id = " + msg_id + ", channelNum = " + channelNum);
    }

    public void everyNotifyHandle(TvProviderChannelEventBase event)
    {
        Log.d(TAG,"Enter everyNotifyHandle");
        int i = 0;
        int svlId = event.getSvlId();
        int count = event.getCount();
        int[] eventType = event.getEventType();
        int[] svlRecId = event.getSvlRecId();
        
        for(i = 0; i < count; i++)
        {
            switch(eventType[i])
            {
                case UPDATTE_EVENT_TYPE_INSERT_REC :
                {
                    Log.d(TAG,"begin INSERT_REC: "+"count="+count+", svlId="+svlId+", svlRecId="+svlRecId[i]+", eventType[i]="+eventType[i]);
                    TvProviderChannelInfoBase t = new TvProviderChannelInfoBase();
                    t = MtkTvChannelList.getInstance().getTvproviderBySvlRecId(svlId,svlRecId[i]);
                    if(t != null)
                    {
                        /* Fake channel on us is: browsable==0 && brdcst_type==1(NTSC) .Fake channel on us don't sync.*/
                        if ((cp.isUsRegion == false)||(t.getBrowsableMask() != 0)||(t.getGoogBrdcstType() != 1))
                        {
                            Channel c = new Channel.Builder()
                                .setType(cp.GoogleBrdcstTypeToString(t.getGoogBrdcstType()))
                                .setInputId(getInputId(service, t.getbroadcastType()))
                                .setTransportStreamId(t.getTransportStreamId())
                                .setProgramNumber(t.getProgramNumber())
                                .setOriginalNetworkId(t.getOriginalNetworkId())
                                .setDisplayNumber(t.getChannelNumber())
                                .setDisplayName(t.getDisplayName())
                                .setBrowsable(t.getBrowsableMask() == 1)
                                .setLocked(t.getLockedMask() == 1)
                                .setServiceType(cp.serviceTypeToString(t.getServiceType()))
                                .setData((tvprovidrVer + ","
                                    + String.format("%05d", t.getSvlId()) + ","
                                    + String.format("%05d", t.getSvlRecId()) + ","
                                    + String.format("%010d", (0xFFFFFFFFL & t.getChannelId())) + ","
                                    + String.format("%010d", (0xFFFFFFFFL & t.getHashcode()))).getBytes())
                                .build();
                            contentResolver.insert(TvContract.Channels.CONTENT_URI, c.toContentValues());
                            Log.d(TAG, "##### Insert End, LockedMask:" + t.getLockedMask()
                                + " BrowsableMask:" + t.getBrowsableMask()
                                + " GoogleTYPE:" + t.getGoogBrdcstType()
                                + " isUsRegion:" + cp.isUsRegion);
                        }
                    }
                    break;
                }
                case UPDATTE_EVENT_TYPE_UPDATE_REC :
                {
                    Log.d(TAG,"begin UPDATE_REC: "+"count="+count+", svlId="+svlId+", svlRecId="+svlRecId[i]+", eventType[i]="+eventType[i]);
                    TvProviderChannelInfoBase t = MtkTvChannelList.getInstance().getTvproviderBySvlRecId(svlId,svlRecId[i]);
                    if(t != null)
                    {
                        String[] projection = {
                            TvContract.Channels._ID,
                            TvContract.Channels.COLUMN_TYPE,
                            TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
                            TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,
                            TvContract.Channels.COLUMN_SERVICE_ID,
                            TvContract.Channels.COLUMN_DISPLAY_NUMBER,
                            TvContract.Channels.COLUMN_DISPLAY_NAME,
                            TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
                            TvContract.Channels.COLUMN_SERVICE_TYPE};

                        String selection = "substr(cast("+TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA+" as varchar),7,11)=?";
                        String[] selectionArgs = { String.format("%05d",svlId)+ "," + String.format("%05d",svlRecId[i])};
                        Cursor mCursor = contentResolver.query(TvContract.Channels.CONTENT_URI, projection, selection, selectionArgs, null);
                        if(mCursor == null)
                        {
                            Log.d(TAG, "cursor not loaded " );
                            throw new IllegalStateException("Cursor not loaded");
                        }
                        else
                        {
                            if(mCursor.getCount()< 1)
                            {
                                Log.d(TAG,"mCursor.getCount() <=1, "+mCursor.getCount());
                                /* Fake channel on us is: browsable==0 && brdcst_type==1(NTSC) .Fake channel on us don't sync.*/
                                if ((cp.isUsRegion == false)||(t.getBrowsableMask() != 0)||(t.getGoogBrdcstType() != 1))
                                {
                                    Channel c = new Channel.Builder()
                                        .setType(cp.GoogleBrdcstTypeToString(t.getGoogBrdcstType()))
                                        .setInputId(getInputId(service, t.getbroadcastType()))
                                        .setTransportStreamId(t.getTransportStreamId())
                                        .setProgramNumber(t.getProgramNumber())
                                        .setOriginalNetworkId(t.getOriginalNetworkId())
                                        .setDisplayNumber(t.getChannelNumber())
                                        .setDisplayName(t.getDisplayName())
                                        .setBrowsable(t.getBrowsableMask() == 1)
                                        .setLocked(t.getLockedMask() == 1)
                                        .setServiceType(cp.serviceTypeToString(t.getServiceType()))
                                        .setData((tvprovidrVer + "," + String.format("%05d", t.getSvlId()) + ","
                                            + String.format("%05d", t.getSvlRecId()) + ","
                                            + String.format("%010d",(0xFFFFFFFFL & t.getChannelId())) + ","
                                            + String.format("%010d", (0xFFFFFFFFL & t.getHashcode()))).getBytes()).build();

                                    contentResolver.insert(TvContract.Channels.CONTENT_URI, c.toContentValues());
                                    Log.d(TAG, "##### Update1 End, LockedMask:" + t.getLockedMask()
                                        + " BrowsableMask:" + t.getBrowsableMask()
                                        + " GoogleTYPE:" + t.getGoogBrdcstType()
                                        + " isUsRegion:" + cp.isUsRegion);
                                }
                            }
                            else
                            {
                                Log.d(TAG,"mCursor.getCount()>1 "+mCursor.getCount());
                                int oldPosition = mCursor.getPosition();
                                mCursor.moveToFirst();
                                /* Fake channel on us is: browsable==0 && brdcst_type==1(NTSC) .Fake channel on us don't sync.*/
                                if ((cp.isUsRegion == false)||(t.getBrowsableMask() != 0)||(t.getGoogBrdcstType() != 1))
                                {
                                    ContentValues values = new ContentValues();
                                    values.put(TvContract.Channels.COLUMN_INPUT_ID, getInputId(service, t.getbroadcastType()));
                                    values.put(TvContract.Channels.COLUMN_TYPE, cp.GoogleBrdcstTypeToString(t.getGoogBrdcstType()));
                                    values.put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, t.getOriginalNetworkId());
                                    values.put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, t.getTransportStreamId());
                                    values.put(TvContract.Channels.COLUMN_SERVICE_ID, t.getProgramNumber());
                                    values.put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, t.getChannelNumber());
                                    values.put(TvContract.Channels.COLUMN_DISPLAY_NAME, t.getDisplayName());
                                    values.put(TvContract.Channels.COLUMN_BROWSABLE, t.getBrowsableMask());
                                    values.put(TvContract.Channels.COLUMN_LOCKED, t.getLockedMask());
                                    values.put(TvContract.Channels.COLUMN_SERVICE_TYPE, cp.serviceTypeToString(t.getServiceType()));
                                    values.put(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
                                        (tvprovidrVer + "," + String.format("%05d", svlId) + ","
                                        + String.format("%05d", svlRecId[i]) + ","
                                        + String.format("%010d", (0xFFFFFFFFL & t.getChannelId())) + ","
                                        + String.format("%010d", (0xFFFFFFFFL & t.getHashcode()))).getBytes());

                                    String where = TvContract.Channels._ID + " = ? ";
                                    String[] selectionArgs2 = { mCursor.getString(mCursor.getColumnIndex(TvContract.Channels._ID)) };
                                    contentResolver.update(TvContract.Channels.CONTENT_URI, values, where, selectionArgs2);

                                    mCursor.moveToPosition(oldPosition);
                                    Log.d(TAG, "##### Update2 End, LockedMask:" + t.getLockedMask()
                                        + " BrowsableMask:" + t.getBrowsableMask()
                                        + " GoogleTYPE:" + t.getGoogBrdcstType()
                                        + " isUsRegion:" + cp.isUsRegion);
                                }
                            }
                            mCursor.close();
                        }
                    }
                    break;
                }
                case UPDATTE_EVENT_TYPE_DELETE_REC :
                {
                    Log.d(TAG,"begin DELETE_REC: "+"count="+count+", svlId="+svlId+", svlRecId="+svlRecId[i]+", eventType[i]="+eventType[i]);
                    String[] projection = {
                        TvContract.Channels._ID,
                        TvContract.Channels.COLUMN_TYPE,
                        TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
                        TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,
                        TvContract.Channels.COLUMN_SERVICE_ID,
                        TvContract.Channels.COLUMN_DISPLAY_NUMBER,
                        TvContract.Channels.COLUMN_DISPLAY_NAME,
                        TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
                        TvContract.Channels.COLUMN_SERVICE_TYPE};

                    String selection = "substr(cast("+TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA+" as varchar),7,11)=?";
                    String[] selectionArgs = { String.format("%05d",svlId)+ "," + String.format("%05d",svlRecId[i])};
                    Cursor mCursor = contentResolver.query(TvContract.Channels.CONTENT_URI, projection, selection, selectionArgs, null);

                    if(mCursor == null)
                    {
                        Log.d(TAG, "cursor not loaded " );
                        throw new IllegalStateException("Cursor not loaded");
                    }
                    else
                    {
                        if(mCursor.getCount()< 1)
                        {
                            Log.d(TAG, "can not find the record from TV provider " );
                        }
                        else
                        {
                            int oldPosition = mCursor.getPosition();
                            mCursor.moveToFirst();
                            pp.deleteChannelProgramBySvlRecId(svlId , svlRecId[i]);
                            String selection2 = "substr(cast("+TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA+" as varchar),7,11)=?" ;
                            String[] selectionArgs2 = { String.format("%05d",svlId)+ "," + String.format("%05d",svlRecId[i])};

                            contentResolver.delete(TvContract.Channels.CONTENT_URI,selection2, selectionArgs2);
                            mCursor.moveToPosition(oldPosition);
                            Log.d(TAG,"end DELETE_REC"+"svlId " + svlId + " count" + count+ " eventType[i]" + eventType[i] + " svlRecId" +svlRecId[i]);
                        }
                        mCursor.close();
                    }
                    break;
                }
                
                case UPDATTE_EVENT_TYPE_CLEAN_DB :
                {
                    Log.d(TAG,"begin CLEAN_DB: "+"count="+count+", svlId="+svlId+", svlRecId="+svlRecId[i]+", eventType[i]="+eventType[i]);
                    pp.deleteChannelProgram(svlId);
                    ArrayList<ContentProviderOperation> opsdelete = new ArrayList<ContentProviderOperation>();
                    opsdelete.add(ContentProviderOperation.newDelete(TvContract.Channels.CONTENT_URI)
                        .withSelection("substr(cast("+TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA+" as varchar),7,5)=? ",
                        new String[]{String.format("%05d",svlId)}).build());
                    try{
                        contentResolver.applyBatch(TvContract.AUTHORITY,opsdelete);
                    }catch(RemoteException e){
                    
                    }catch(OperationApplicationException e){
                    
                    }
                    /* If the total channel is greater than 1250. the applyBatch can success ? */
                    break;
                }
                
                case UPDATTE_EVENT_TYPE_LOAD_DB :
                {
                    Log.d(TAG,"begin LOAD_DB: "+"count="+count+", svlId="+svlId+", svlRecId="+svlRecId[i]+", eventType[i]="+eventType[i]);
                    mtkConfig = MtkTvConfig.getInstance();
                    cp.resetSync(svlId, contentResolver);       /* the same behavior under different mode */
                    Log.d(TAG,"after EVENT_EVERY_NOTIFY");
                    break;
                }
                
                default:
                {
                    Log.d(TAG,"Default: "+"count="+count+", svlId="+svlId+", svlRecId="+svlRecId[i]+", eventType[i]="+eventType[i]);
                    break;
                }
            }
        }
    }


    /*################################## notify for internal message ################################*/
    public void notifyChannel(int condition, int reason, int data) 
    {
        Log.d(TAG, "channelList notify " + condition + " " + reason + " " + data);
    }

    public void notifyResetSync()
    {
        Message msg = Message.obtain();
        msg.what = msg.arg1 = msg.arg2 = EVENT_SCAN_NOTIFY;
        Log.d(TAG, "notifyResetSync: mHandler.sendMessage(msg) msg.what= " + msg.what);
        mHandler.sendMessage(msg);
    }

    public void notifyTvprovider(int svlId, int count, int[] eventType, int[] svlRecId) 
    {
        if(getMonitorChannelUpdateStatus()== false)
        {
            Log.d(TAG,"notifyTvprovider, Monitor is stopped, return directly. " );
            return;
        }

        for(int i = 0; i < count; i++)
        {
            Log.d(TAG,"count:" + count + ", svlId:" + svlId + ", svlRecId:" +svlRecId[i] + ", eventType:" + eventType[i]);
        }

        Log.d(TAG, "count:"+ count + ", svlId:"+ svlId + ", currentSvlId:" + currentSvlId + ", channelListMode:" + channelListMode);
        if (channelListMode == 1)
        {
            /* one channel list mode */
            if ((svlId != SVL_ID_ANTENNA)&&(svlId != SVL_ID_CABLE))
            {
                Log.d(TAG, "ocl, svlId is not equal the ID in tvprovider. return.");
                return;
            }
        }
        else if (channelListMode == 0)
        {
            /* current channel list mode */
            if (svlId != currentSvlId)
            {
                Log.d(TAG, "current, svlId is not equal the ID in tvprovider. return.");
                return ;
            }
        }

        TvProviderChannelEventBase event = new TvProviderChannelEventBase();
        event.setSvlId(svlId);
        event.setCount(count);
        event.setEventType(eventType);
        event.setSvlRecId(svlRecId);

        Message msg = Message.obtain();
        msg.what = msg.arg1 = msg.arg2 = EVENT_EVERY_NOTIFY;
        msg.obj = event;
        mHandler.sendMessage(msg);
    }

    public void notifySvlIdUpdate(int newSvlId) 
    {
        if(currentSvlId != newSvlId)
        {
            Message msg = Message.obtain();
            msg.what = EVENT_SVLID_UPDATE;
            msg.arg1 = currentSvlId;
            msg.arg2 = newSvlId;
            currentSvlId = newSvlId;
            Log.d(TAG, "mHandler.sendMessage(msg) msg.what= " + msg.what + ", msg.arg1=" + msg.arg1 + ", msg.arg2=" + msg.arg2);
            mHandler.sendMessage(msg);
        }
        else
        {
            Log.d(TAG, "notifySvlIdUpdate, do nothing: newId is the same with currentSvlId:" + newSvlId);
        }
    }

    public void notifyListModeChange(int oldMode, int newMode)
    {
        if (channelListMode != newMode)
        {
            Message msg = Message.obtain();
            msg.what = EVENT_MODE_CHANGE;
            msg.arg1 = oldMode;
            msg.arg2 = newMode;
            channelListMode = newMode;
            Log.d(TAG, "mHandler.sendMessage(msg) msg.what=" + msg.what + ", msg.arg1=" + msg.arg1 + ", msg.arg2=" + msg.arg2);
            mHandler.sendMessage(msg);
        }
        else
        {
            Log.d(TAG, "notifyListModeChange, do nothing: newMode is the same with current channelListMode:" + newMode);
        }  
    }

    public void notifyScanInfo(int msg_id, int channelNum)
    {
        /* msg only supply two args */
        Message msg = Message.obtain();
        msg.what = EVENT_SCAN_INFO;
        msg.arg1 = msg_id;
        msg.arg2 = channelNum;
        Log.d(TAG, "mHandler.sendMessage(msg) msg.what=" + msg.what + ", msg.arg1=" + msg.arg1 + ", msg.arg2=" + msg.arg2);
        mHandler.sendMessage(msg);
    }

    @Override
    protected void processSync() {
        Log.d(TAG, "###### processSync go go go" );
        
        stopMonitorChannelUpdate();
        mtkConfig = MtkTvConfig.getInstance();
        currentSvlId = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_BS_SVL_ID);
        channelListMode = MtkTvChannelList.getInstance().getChannelListMode();
        Log.d(TAG, "###### processSync: currentSvlId = " + currentSvlId +", channelListMode = " + channelListMode );
        
        switch(channelListMode)
        {
            case CHANNELLIST_MODE_CURRENT:      /* current channellist mode */
                this.cp = ccp;
                break;
            case CHANNELLIST_MODE_ONE:          /* one channellist mode */
                this.cp = ocp;
                break;
            default:
                this.cp = ccp;
        }
        cp.mergeSync(contentResolver, currentSvlId);
        startMonitorChannelUpdate();

        /* 
        while (!isStop()) {
        Log.d(TAG, "process Sync task ******guolei*********" + getName());
        try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */

        if (mThread == null) 
        {
            Log.d(TAG, "mThread == null");
            return ;
        }

        mHandler = new Handler(mThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                Log.d(TAG,"###### enter handle message");
                switch(msg.what)
                {
                    case EVENT_SCAN_NOTIFY:
                    {
                        Log.d(TAG,"Case: EVENT_SCAN_NOTIFY");
                        stopMonitorChannelUpdate();
                        scanNotifyHandle();
                        startMonitorChannelUpdate();
                        break;
                    }

                    case EVENT_SVLID_UPDATE:
                    {
                        Log.d(TAG,"Case: EVENT_SVLID_UPDATE");
                        stopMonitorChannelUpdate();
                        svlIdUpdateHandle(msg.arg1, msg.arg2);
                        startMonitorChannelUpdate();
                        break;
                    }

                    case EVENT_MODE_CHANGE:
                    {
                        Log.d(TAG,"Case: EVENT_MODE_CHANGE:");
                        stopMonitorChannelUpdate();
                        modeChangeHandle(msg.arg1, msg.arg2);
                        startMonitorChannelUpdate();
                        break;
                    }

                    case EVENT_SCAN_INFO:
                    {
                        Log.d(TAG,"Case: EVENT_SCAN_INFO");
                        scanInfoHandle(msg.arg1, msg.arg2);
                        break;
                    }

                    case EVENT_EVERY_NOTIFY:
                    {
                        Log.d(TAG,"Case: EVENT_EVERY_NOTIFY");
                        TvProviderChannelEventBase event = (TvProviderChannelEventBase)msg.obj;
                        everyNotifyHandle(event);
                        break;
                    }

                    default:
                    {
                        Log.d(TAG," Default msg.what" + msg.what);
                        break;
                    }
                }
            }
        };
    }
}
