package Oceanus.Tv.Service.SourceManager;

import android.content.Context;
import android.util.Log;

import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.SourceImpl;
import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class SourceManager {
    private static final String LOG_TAG = "SourceManager";
    private static SourceManager mObj_SourceManager = null;
    private static SourceImpl mInterface_Source = null;
    public static SourceManager getInstance()
    {
        synchronized(SourceManager.class)
            {
                if (mObj_SourceManager == null)
                {
                    new SourceManager();
                }
            }
        return mObj_SourceManager;
    }
    private SourceManager()
    {
        Log.d(LOG_TAG,"SourceManager Created~");
        mObj_SourceManager = this;
        //Connect();
    }
    public void init(Context mContext)
    {
        mInterface_Source = SourceImpl.getInstance(mContext);
        mInterface_Source.getSourceList();
    }
    /*
    static
    {
        Log.d(LOG_TAG,"ChannelManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_SourceManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_SourceManager library:\n" + e.toString());
        }
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    */
    public List<Source> getSourceList()
    {
        return mInterface_Source.getSourceList();
    }
    public boolean setSource(EN_INPUT_SOURCE_TYPE type)
    {
        List<Source> sourceList =  mInterface_Source.getSourceList();
        for(int i= 0;i<sourceList.size();i++)
        {
            if(sourceList.get(i).getType()== type)
            {
                return setSource(sourceList.get(i));
            }
        }
        return false;
    }
    public boolean setSource(Source source)
    {
        if(mInterface_Source.setSource(source))
        {
            Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SOURCE_CHANGE.ordinal(),source.getType().ordinal());
            EventManager.getInstance().sendBroadcast(info);
            return true;
        }
        return  false;
    }
    public Source getCurSource()
    {
        return mInterface_Source.getCurrentSource();
    }
    public int getSourceNumber()
    {
        return mInterface_Source.getSourceNumber();
    }
    /*
    public EN_INPUT_SOURCE_TYPE getBootSource()
    {
        return mInterface_Source.getPowerOnSource();
    }
    public boolean setBootSource(EN_INPUT_SOURCE_TYPE source)
    {
        return setPowerOnSource(source.ordinal());
    }
    */

    /*
    private native String getJstrSourceList();
    private native boolean setSource(int source);
    private native int getCurrentSource();
    private native int getPowerOnSource();
    private native boolean setPowerOnSource(int source);
    private native int getCurrentSignalState();

    private native void Connect();
    private native void Disconnect();
    */
}
