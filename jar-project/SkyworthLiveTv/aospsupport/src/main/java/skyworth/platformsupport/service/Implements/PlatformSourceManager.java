package skyworth.platformsupport.service.Implements;

import com.platform.IPlatformSourceManager;

import java.util.List;

import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManager;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

/**
 * Created by sky057509 on 2017/4/26.
 */

public class PlatformSourceManager implements IPlatformSourceManager {
    static private PlatformSourceManager m_pThis = null;
    static private SourceManager m_pMwSourceManager = null;
    static public PlatformSourceManager getInstance(SourceManager sourceManager)
    {
        if(m_pThis == null)
        {
            m_pThis = new PlatformSourceManager(sourceManager);
        }
        return m_pThis;
    }
    private PlatformSourceManager(SourceManager sourceManager)
    {
        m_pMwSourceManager = sourceManager;
    }
    @Override
    public List<Source> GetSourceList() {
        return null;
    }

    @Override
    public boolean SetSource(Source source) {
        return false;
    }

    @Override
    public boolean SetSource(EN_INPUT_SOURCE_TYPE type) {
        return false;
    }

    @Override
    public int GetSourceNumber() {
        return 0;
    }

    @Override
    public void RefreshSource() {

    }

    @Override
    public Source GetCurrentSource() {
        return null;
    }
}
