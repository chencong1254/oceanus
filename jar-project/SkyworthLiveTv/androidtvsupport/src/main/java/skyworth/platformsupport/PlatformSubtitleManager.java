package skyworth.platformsupport;

import android.media.tv.TvTrackInfo;
import android.util.Log;

import com.platform.IPlatformSubtitleManager;

import java.util.ArrayList;
import java.util.List;

import skyworth.platformsupport.componentSupport.PlatformTvView;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/26.
 */

public class PlatformSubtitleManager implements IPlatformSubtitleManager {
    private static PlatformSubtitleManager m_pThis = null;
    private static boolean b_IsEnable = false;
    private static int currentIndex = 0;
    PlatformTvView view = null;
    public static PlatformSubtitleManager getInstance()
    {
        if(m_pThis == null)
        {
            m_pThis = new  PlatformSubtitleManager();
        }
        return m_pThis;
    }
    private PlatformSubtitleManager()
    {
        view = (PlatformTvView) PlatformManager.getInstance().GetPlatformView();
    }

    @Override
    public boolean EnableSubtitle(boolean bEnable) {
        if(IsSubtitleExist())
        {
            currentIndex = 0;
            if(bEnable)
            {
                b_IsEnable = bEnable;
                return SelectSubtitle(0);
            }
            else
            {
                b_IsEnable = false;
                view.selectTrack(TvTrackInfo.TYPE_SUBTITLE,null);
                return true;
            }
        }
        b_IsEnable = false;
        view.selectTrack(TvTrackInfo.TYPE_SUBTITLE,null);
        return false;
    }

    @Override
    public boolean SelectSubtitle(int index) {
        List<TvTrackInfo> infoList = view.getTracks(TvTrackInfo.TYPE_SUBTITLE);
        if(index>=0&& index<infoList.size())
        {
            currentIndex = index;
            view.setCaptionEnabled(true);
            view.selectTrack(TvTrackInfo.TYPE_SUBTITLE,infoList.get(index).getId());
            return true;
        }
        return false;
    }

    @Override
    public List<String> GetSubtitleList() {
        List<TvTrackInfo> infoList = view.getTracks(TvTrackInfo.TYPE_SUBTITLE);
        List<String> resualt = new ArrayList<>();
        for(TvTrackInfo info:infoList)
        {
            Log.d(DEBUG_TAG,"lanaguage : + " + info.getLanguage());
            resualt.add(info.getLanguage());
        }
        return resualt;
    }

    @Override
    public String GetSubtitleInfoById(int id) {
        List<String> list = GetSubtitleList();
        if(list.size()>0)
        {
            return list.get(id);
        }
        return null;
    }

    @Override
    public boolean IsSubtitleExist() {
        List<TvTrackInfo> infoList = view.getTracks(TvTrackInfo.TYPE_SUBTITLE);
        if(infoList!=null)
        {
            if(infoList.size()>0)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean IsSubtitleEnable() {
        return b_IsEnable;
    }

    @Override
    public int GetCurrentSubtitleIndex() {
        return currentIndex;
    }
}
