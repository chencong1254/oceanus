package Oceanus.Tv.Service.TeletextManager;

import android.util.Log;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.TeletexImpl;
import Oceanus.Tv.Service.TeletextManager.TeletextManagerDefinitions.EN_TTX_CMD;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class TeletextManager {
    private static final String LOG_TAG = "TeletextManager";
    private static TeletextManager mObj_TeletextManager = null;
    private static TeletexImpl mObj_TeletexImpl = null;
    public static TeletextManager getInstance()
    {
        synchronized(TeletextManager.class)
        {
            if (mObj_TeletextManager == null)
                {
                    new TeletextManager();
                }
        }
        return mObj_TeletextManager;
    }
    private TeletextManager()
    {
        Log.d(LOG_TAG,"TeletextManager Created~");
        mObj_TeletextManager = this;
        mObj_TeletexImpl = TeletexImpl.getInstance();
        //Connect();
    }
    public boolean EnableTeletext(boolean bIsEnable)
    {
        mObj_TeletexImpl.EnableTeletext(bIsEnable);
        return false;
    }
    public boolean IsTeletextExist()
    {
        return mObj_TeletexImpl.IsTeletextExist();
    }
    public void TeletextPassCmd(EN_TTX_CMD cmd)
    {
        mObj_TeletexImpl.TeletextPassCmd(cmd);
    }
    /*
    static
    {
        Log.d(LOG_TAG,"TeletextManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_TeletextManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_TeletextManager library:\n" + e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    public native boolean checkTeletextClockSignal();
    public native boolean hasTeletextSignal();
    public native boolean isTeletextDisplayed();
    public native boolean openTeletext(int mode);
    private native void Connect();
    private native void Disconnect();
    */
}
