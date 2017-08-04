package Oceanus.Tv.Service.CiManager;

import android.util.Log;


/**
 * Created by shen on 2016/8/16.
 */
public class CiManager {
    private static final String LOG_TAG = "CiManager";
    private static CiManager mObj_CiManager = null;
    public static  CiManager getInstance()
    {
        synchronized (CiManager.class)
            {
                if (mObj_CiManager == null)
                {
                    new CiManager();
                }
            }
        return mObj_CiManager;
    }
    private CiManager()
    {
        Log.d(LOG_TAG,"CiManager Created~");
        mObj_CiManager = this;
       // Connect();
    }
    /*
    static
    {
        Log.d(LOG_TAG,"CiManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_CiManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_CiManager library:\n" + e.toString());
        }
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
*/
    /*
    public CiManagerDefs.EN_MMI_TYPE getCIMMI_Type()
    {
        int cimmiType = getCIMMIType();
        try
        {
            return CiManagerDefs.EN_MMI_TYPE.values()[cimmiType];
        } catch (Exception e)
        {
            // TODO: handle exception
            return   CiManagerDefs.EN_MMI_TYPE.values()[0];
        }
    }
    public CiManagerDefs.EN_CARD_STATE getCICard_State()
    {
        int ciCardState = getCICardState();
        try
        {
            return CiManagerDefs.EN_CARD_STATE.values()[ciCardState];
        } catch (Exception e)
        {
            // TODO: handle exception
            return CiManagerDefs.EN_CARD_STATE.values()[0];
        }
    }
    public native void openBaseMenu();
    public native void closeBaseMenu();
    public native void enterMenu(int index);
    public native void backMenu();
    public native String getMenuTitle();
    public native String getMenuSubtitle();
    public native String getMenuBottom();
    public native int getMenuItemCount();
    public native String getMenuItemContent(int index);
    public native boolean isCIMenuOpen();
    public native boolean isCICardExists();
    public native String getEnqString() ;
    public native int getMenuHierarchy();
    public native int getCIPasswordLength();
    public native boolean answerEnq(String pwd, int enter);
    private native int getCIMMIType();
    private native int getCICardState();
    // public native CICamAppInfo getCICamAppInfo();
    private native void Connect();
    private native void Disconnect();
    */
}
