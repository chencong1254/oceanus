package Oceanus.Tv.Service.PvrManager;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class PvrManager {
    private static final String LOG_TAG = "PvrManager";
    private static PvrManager mObj_PvrManager = null;
    private static boolean b_isRecord = false;
    private static boolean b_isTimeShift = false;
    public static PvrManager getInstance()
    {
        synchronized(PvrManager.class)
            {
                if (mObj_PvrManager == null)
                {
                    new PvrManager();
                }
            }
        return mObj_PvrManager;
    }
    private PvrManager()
    {
        Log.d(LOG_TAG,"PvrManager Created~");
        mObj_PvrManager = this;
        //Connect();
    }
    /*
    static
    {
        Log.d(LOG_TAG,"PvrManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_PvrManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_PvrManager library:\n" + e.toString());
        }
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    */
    public boolean setStorgePath(String filePath)
    {
        return true;
    }
    public String getCurrentStorgePath()
    {
        return "unknow";
    }
    public boolean startTimeShift(Channel channel)
    {
        switch (channel.getType())
        {
            case E_SERVICE_DTV_DVBC:
            case E_SERVICE_DTV_DVBS:
            case E_SERVICE_DTV_DVBT:
            case E_SERVICE_DTV_ISDB:
            {
                //can be timeshift!
                b_isTimeShift = true;
            }
            return true;
            default:break;
        }
        return false;
    }
    public boolean stopTimeShift()
    {
        b_isTimeShift = false;
        return true;
    }
    public boolean isTimeShifting()
    {
        return b_isTimeShift;
    }
    public boolean startRecord(Channel channel)
    {
        switch (channel.getType())
        {
            case E_SERVICE_DTV_DVBC:
            case E_SERVICE_DTV_DVBS:
            case E_SERVICE_DTV_DVBT:
            case E_SERVICE_DTV_ISDB:
            {
                //can be record!
                b_isRecord = true;
            }
            return true;
            default:break;
        }
        return false;
    }

    public boolean stopRecord()
    {
        return true;
    }
    public boolean isRecording()
    {
        return b_isRecord;
    }
    public boolean playRecord(PvrRecord record)
    {
        return true;
    }
    public List<PvrRecord> getRecordList()
    {
        List<PvrRecord> list = new ArrayList<PvrRecord>();
        return list;
    }
    public boolean renameRecord(PvrRecord record,String newName)
    {
        record.renameFile(newName);
        return true;
    }
    public boolean jumpRecord(PvrRecord record,long time)
    {
        record.setDuringTime(time);
        return true;
    }
    public boolean jumpTimeShift(long time)
    {
        return true;
    }
    public boolean setABRepeat(long time_a,long time_b)
    {
        return true;
    }
    public boolean setPlayPause(boolean bIsPlay)
    {
        if(bIsPlay)
            {
                if(b_isTimeShift)
                {
                    //play timeshift
                }
                else
                {//for record

                }
            }
        else
            {
                if(b_isTimeShift)
                {
                    //pause timeshift
                }
                else
                {//for record

                }
            }
        return true;
    }
/*
    private native void Connect();
    private native void Disconnect();
    */
}
