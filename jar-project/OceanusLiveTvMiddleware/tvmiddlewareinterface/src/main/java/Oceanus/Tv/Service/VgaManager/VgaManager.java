package Oceanus.Tv.Service.VgaManager;

import android.content.Context;
import android.util.Log;
import Oceanus.Tv.ITvFunctionInterface.TvFunction.VGAImpl;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class VgaManager {
    private static final String LOG_TAG = "VgaManager";
    private static VgaManager mObj_VgaManager = null;
    private static VGAImpl mInterface_Vga = null;
    public static VgaManager getInstance()
    {
        if (mObj_VgaManager == null)
        {
            synchronized(VgaManager.class)
            {
                if (mObj_VgaManager == null)
                {
                    new VgaManager();
                }
            }
        }
        return mObj_VgaManager;
    }
    private VgaManager()
    {
        Log.d(LOG_TAG,"VgaManager Created~");
        mObj_VgaManager = this;
    }
    public void init(Context mContext)
    {
        mInterface_Vga = VGAImpl.getInstance(mContext);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
    public boolean setVgaAutoAdjust()
    {
        return mInterface_Vga.setVgaAutoAdjust();
    }
    public boolean setVgaHPosition(int ucPosition)
    {
        return mInterface_Vga.setVgaHPosition(ucPosition);
    }
    public boolean setVgaVPosition(int ucPosition)
    {
        return mInterface_Vga.setVgaVPosition(ucPosition);
    }
    public boolean setVgaPhase(int ucValue)
    {
        return mInterface_Vga.setVgaPhase(ucValue);
    }
    public boolean setVgaClock(int ucValue)
    {
        return mInterface_Vga.setVgaClock(ucValue);
    }
    public int getVgaHPosition()
    {
        return mInterface_Vga.getVgaHPosition();
    }
    public int getVgaVPosition()
    {
        return mInterface_Vga.getVgaVPosition();
    }
    public int getVgaPhase()
    {
        return mInterface_Vga.getVgaPhase();
    }
    public int getVgaClock()
    {
        return mInterface_Vga.getVgaClock();
    }
}
