package Oceanus.Tv.ITvFunctionInterface.TvFunction;


import android.content.Context;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import Oceanus.Tv.ITvFunctionInterface.IVGA;

/**
 * Created by xeasy on 2017/1/11.
 */

public class VGAImpl implements IVGA {
    private static VGAImpl mObj_This = null;
    private MtkTvConfig mConfig;
    private Context mContext;
    private VGAImpl(Context context) {
        mContext = context;
        mConfig = MtkTvConfig.getInstance();
    }
    public static VGAImpl getInstance(Context context)
    {
        if(mObj_This == null)
        {
            mObj_This = new VGAImpl(context);
            return mObj_This;
        }
        else
        {
            return mObj_This;
        }
    }
    @Override
    public boolean setVgaAutoAdjust() {
        Log.d("Oceanus","setVgaAutoAdjust");
//        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.AUTO_ADJUST,value);
        return false;
    }
    @Override
    public boolean setVgaHPosition(int ucPosition) {
        Log.d("Oceanus","setVgaHPosition ucPosition ="+ucPosition);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.HPOSITION,ucPosition);
        return true;
    }

    @Override
    public boolean setVgaVPosition(int ucPosition) {
        Log.d("Oceanus","setVgaVPosition ucPosition ="+ucPosition);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.VPOSITION,ucPosition);
        return true;
    }

    @Override
    public boolean setVgaPhase(int ucValue) {
        Log.d("Oceanus","setVgaPhase ucValue ="+ucValue);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.PHASE,ucValue);
        return true;
    }

    @Override
    public boolean setVgaClock(int ucValue) {
        Log.d("Oceanus","setVgaClock ucValue ="+ucValue);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.CLOCK,ucValue);
        return true;
    }

    @Override
    public int getVgaHPosition() {
        int vgaHpos = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.HPOSITION);
        Log.d("Oceanus","getVgaHPosition:"+vgaHpos);
        return vgaHpos;
    }
    @Override
    public int getVgaVPosition() {
        int vgaVpos = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.VPOSITION);
        Log.d("Oceanus","getVgaVPosition:"+vgaVpos);
        return vgaVpos;
    }

    @Override
    public int getVgaPhase() {
        int vgaPhase = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.PHASE);
        Log.d("Oceanus","getVgaPhase:"+vgaPhase);
        return vgaPhase;
    }
    @Override
    public int getVgaClock() {
        int vgaClock = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.CLOCK);
        Log.d("Oceanus","getVgaClock:"+vgaClock);
        return vgaClock;
    }
}