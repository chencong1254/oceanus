package skyworth.skyworthlivetv.osd.ui.info;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.product.adpater.infobar.InfoBarViewAdapter;

import skyworth.skyworthlivetv.R;

import static skyworth.skyworthlivetv.global.GlobalDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/23.
 */

public class SimpleInfo {
    private InfoBarView m_pView = null;
    private InfoBarViewAdapter m_pAdapter = null;
    private TextView channelNumber = null;
    private TextView channelName = null;
    private TextView channelType = null;
    private TextView info_subtitle = null;
    private TextView currentChannelTime = null;
    private ImageView icon_subtitle = null;
    private ImageView icon_cc_tt = null;
    private ImageView iconType = null;
    private ImageView iconScramble = null;
    private ImageView iconFav = null;
    private ImageView iconLock = null;
    private ImageView iconGinga = null;
    SimpleInfo(InfoBarView view, InfoBarViewAdapter adapter)
    {
        m_pView = view;
        m_pAdapter = adapter;
        channelNumber = (TextView) m_pView.findViewById(R.id.info_channel_num);
        channelName = (TextView) m_pView.findViewById(R.id.info_channel_name);
        channelType = (TextView) m_pView.findViewById(R.id.info_channel_servicetype);
        info_subtitle = (TextView)m_pView.findViewById(R.id.info_channel_sttl);
        currentChannelTime = (TextView)m_pView.findViewById(R.id.info_channel_timeinfo);
        icon_subtitle = (ImageView) m_pView.findViewById(R.id.info_channel_infoicon_subtitle);
        icon_cc_tt = (ImageView) m_pView.findViewById(R.id.info_channel_infoicon_cc_tt);
        iconType = (ImageView) m_pView.findViewById(R.id.info_channel_infoicon_type);
        iconScramble = (ImageView) m_pView.findViewById(R.id.info_channel_infoicon_scrambl);
        iconFav = (ImageView) m_pView.findViewById(R.id.info_channel_infoicon_fav);
        iconLock = (ImageView) m_pView.findViewById(R.id.info_channel_infoicon_lock);
        iconGinga = (ImageView) m_pView.findViewById(R.id.info_channel_infoicon_ginga);
    }
    public void RefreshTeletext()
    {
        int id = m_pAdapter.IsHasCCOrTT();
        if(id!=0)
        {
            icon_cc_tt.setBackgroundResource(m_pAdapter.IsHasCCOrTT());
            icon_cc_tt.setVisibility(View.VISIBLE);
        }
        else
        {
            icon_cc_tt.setVisibility(View.GONE);
        }
    }
    private void RefreshSubtitle()
    {
        if(m_pAdapter.IsHasSubtitle())
        {
            icon_subtitle.setVisibility(View.VISIBLE);
            String info = m_pAdapter.GetCurrentSubtitleInfo();
            if(info!=null)
            {
                Log.d(DEBUG_TAG,"INFO: " + info);
                info_subtitle.setText(info);
            }
            else
            {
                info_subtitle.setText(m_pView.getContext().getString(R.string.space));
            }
        }
        else
        {
            icon_subtitle.setVisibility(View.GONE);
            info_subtitle.setText(m_pView.getContext().getString(R.string.space));
        }
    }
    private void SetImages()
    {
        Drawable drawable = m_pAdapter.GetServiceTypeImage();
        if(drawable!=null)
        {
            iconType.setImageDrawable(drawable);
            iconType.setVisibility(View.VISIBLE);
        }
        else
        {
            iconType.setVisibility(View.INVISIBLE);
        }
        iconFav.setVisibility(m_pAdapter.IsFav()?View.VISIBLE:View.GONE);
        iconLock.setVisibility(m_pAdapter.IsLocked()?View.VISIBLE:View.GONE);
        iconGinga.setVisibility(m_pAdapter.HasGinga()?View.VISIBLE:View.GONE);
        RefreshSubtitle();
        //iconScramble.setVisibility(m_pAdapter.IsScramble()?View.VISIBLE:View.GONE);
    }
    void Refresh()
    {
        Log.d(DEBUG_TAG,"SimpleInfo------------------->Refresh");
        channelNumber.setText(String.valueOf(m_pAdapter.GetCurrentChannelNumber()));
        channelName.setText(m_pAdapter.GetCurrenChannelName());
        currentChannelTime.setText(m_pAdapter.GetCurrentTime());
        SetImages();
        m_pView.postDelayed(new Runnable() {
            @Override
            public void run() {
                channelType.setText(m_pAdapter.GetCurrentChannelServiceTypeName());
                RefreshTeletext();
            }
        },1500);
    }
}
