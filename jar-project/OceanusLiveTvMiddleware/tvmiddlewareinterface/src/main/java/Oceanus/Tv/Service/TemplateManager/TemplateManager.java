package Oceanus.Tv.Service.TemplateManager;

import android.util.Log;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class TemplateManager {
    private static final String LOG_TAG = "TemplateManager";
    private static TemplateManager mObj_TemplateManager = null;
    public static TemplateManager getInstance()
    {
        synchronized(TemplateManager.class)
        {
            if (mObj_TemplateManager == null)
            {
                    new TemplateManager();
            }
        }
        return mObj_TemplateManager;
    }
    private TemplateManager()
    {
        Log.d(LOG_TAG,"TemplateManager Created~");
        mObj_TemplateManager = this;
        Connect();
    }
    static
    {
        Log.d(LOG_TAG,"TemplateManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_TemplateManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_TemplateManager library:\n" + e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    private native void Connect();
    private native void Disconnect();
}
