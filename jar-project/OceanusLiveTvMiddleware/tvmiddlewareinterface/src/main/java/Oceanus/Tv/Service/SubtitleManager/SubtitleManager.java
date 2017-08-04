package Oceanus.Tv.Service.SubtitleManager;

import android.util.Log;

import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.ISubtitle;
import Oceanus.Tv.ITvFunctionInterface.TvFunction.SubtitleImpl;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class SubtitleManager{
    private static final String LOG_TAG = "SubtitleManager";
    private static SubtitleManager mObj_SubtitleManager = null;
    private static ISubtitle mObj_SubtitleInterface = null;
    private static boolean b_IsEnable = false;
    private static int currentIndex = 0;
    public static SubtitleManager getInstance()
    {
        synchronized(SubtitleManager.class)
            {
                if (mObj_SubtitleManager == null)
                {
                    new SubtitleManager();
                }
            }
        return mObj_SubtitleManager;
    }
    private SubtitleManager()
    {
        Log.d(LOG_TAG,"SubtitleManager Created~");
        mObj_SubtitleManager = this;
        mObj_SubtitleInterface = SubtitleImpl.getInstance();
        //Connect();
    }
    public boolean EnableSubtitle(boolean bEnable)
    {
        if(mObj_SubtitleInterface.EnableSubtitle(bEnable))
        {
            currentIndex = 0;
            b_IsEnable = bEnable;
            return true;
        }
        return false;
    }
    public boolean SelectSubtitle(int index)
    {
        if( mObj_SubtitleInterface.SelectSubtitle(index))
        {
            currentIndex = index;
            return true;
        }
        return false;
    }
    public List<String> GetSubtitleList()
    {
        return mObj_SubtitleInterface.GetSubtitleList();
    }
    public String GetSubtitleInfoById(int id)
    {
       return mObj_SubtitleInterface.GetSubtitleList().get(id);
    }
    public boolean IsSubtitleExist()
    {
        return mObj_SubtitleInterface.IsSubtitleExist();
    }
    public boolean IsSubtitleEnable()
    {
        return b_IsEnable;
    }
    public int GetCurrentSubtitleIndex()
    {
        return currentIndex;
    }
    /*
    static
    {
        Log.d(LOG_TAG,"SubtitleManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_SubtitleManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_SubtitleManager library:\n" + e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    public native boolean openSubtitle(int index);
    public native boolean closeSubtitle();
    public native String getSubtitleInfo();

    private native void Connect();
    private native void Disconnect();
    */
}
