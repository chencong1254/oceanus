package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvSubtitle;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.ISubtitle;

/**
 * Created by sky057509 on 2017/3/10.
 */
public class SubtitleImpl implements ISubtitle {
    private static SubtitleImpl mObj_This = null;
    private MtkTvSubtitle mObjMtkSubtitle = null;
    public static SubtitleImpl getInstance()
    {
        if(mObj_This==null)
        {
            mObj_This = new SubtitleImpl();
        }
        return mObj_This;
    }
    private SubtitleImpl()
    {
        super();
        mObjMtkSubtitle = MtkTvSubtitle.getInstance();
    }
    @Override
    public boolean EnableSubtitle(boolean bEnable) {
        if(IsSubtitleExist())
        {
            if(bEnable)
            {
                SelectSubtitle(0);
            }
            else
            {
                MtkTvSubtitle.getInstance().playStream(0xFF);//set subtitle off
                //MtkTvConfig mTvCfg = MtkTvConfig.getInstance();
                //mTvCfg.setLanguage(MtkTvConfigType.CFG_SUBTITLE_SUBTITLE_LANG, MtkTvConfigTypeBase.S639_CFG_SUBTITLE_LANG_OFF);//set digital subtitle lang off
            }
            return true;
        }
        Log.e("Oceanus","Current channel has no subtitle!");
        return false;
    }

    @Override
    public boolean SelectSubtitle(int index) {
        if(IsSubtitleExist())
        {
            if(index > mObjMtkSubtitle.nfySubtitle_trackNum)
            {
                Log.e("Oceanus","index :" + index + "is out of range!");
                return false;
            }
            if(mObjMtkSubtitle.playStream(index) == 0)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> GetSubtitleList() {
        int ret = mObjMtkSubtitle.getTracks();
        if(0 == ret && mObjMtkSubtitle.nfySubtitle_trackList != null)
        {
            List<String> SubtitleList = new ArrayList<String>();
            for(int i = 0; i < mObjMtkSubtitle.nfySubtitle_trackList.length; i++){
                SubtitleList.add(i,mObjMtkSubtitle.nfySubtitle_trackList[i].trackLanguage);
            }
            return SubtitleList;
        }
        return null;
    }

    @Override
    public boolean IsSubtitleExist() {
        int ret = mObjMtkSubtitle.getTracks();
        if(ret == 0)
        {
            if(mObjMtkSubtitle.nfySubtitle_trackNum>0)
            {
                return true;
            }
        }
        return false;
    }
}
