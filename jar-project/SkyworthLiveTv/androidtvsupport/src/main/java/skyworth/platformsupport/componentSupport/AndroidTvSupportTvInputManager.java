package skyworth.platformsupport.componentSupport;

import android.content.Context;
import android.media.tv.TvInputManager;

/**
 * Created by sky057509 on 2017/5/2.
 */

public class AndroidTvSupportTvInputManager {
    static private TvInputManager m_pTIF = null;
    static private AndroidTvSupportTvInputManager m_pThis = null;
    static public AndroidTvSupportTvInputManager getInstance(Context context)
    {
        if(m_pThis==null)
        {
            m_pThis = new AndroidTvSupportTvInputManager(context);
        }
        return m_pThis;
    }
    private AndroidTvSupportTvInputManager(Context context)
    {
        if(context!=null)
        {
            m_pTIF = (TvInputManager)context.getSystemService(Context.TV_INPUT_SERVICE);
        }
    }
    public TvInputManager getTIF()
    {
        return m_pTIF;
    }
}
