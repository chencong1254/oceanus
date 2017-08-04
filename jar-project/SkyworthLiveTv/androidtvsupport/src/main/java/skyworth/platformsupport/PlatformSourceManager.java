package skyworth.platformsupport;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvInputInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.platform.IPlatformSourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;
import skyworth.androidtvsupport.R;
import skyworth.platformsupport.componentSupport.PlatformTvView;
import skyworth.platformsupport.util.SPUtil;

import static android.media.tv.TvInputInfo.TYPE_COMPOSITE;
import static android.media.tv.TvInputInfo.TYPE_TUNER;
import static com.platform.CommonDefinitions.DEBUG_TAG;
import static com.platform.CommonDefinitions.EMPTY;

/**
 * Created by sky057509 on 2017/4/26.
 */

public class PlatformSourceManager implements IPlatformSourceManager {
    public static final String TIF_OBJ = "TIF_OBJ";
    static final String CUR_TIF_SOURECE_ID_KEY = "cur_tif_sourece_id_key";
    static private PlatformSourceManager m_pThis = null;
    static private List<Source> m_pSourceList = null;
    static private Source m_pCurrentSource = null;
    static public IPlatformSourceManager getInstance()
    {
        if(m_pThis == null)
        {
            new PlatformSourceManager();
        }
        return m_pThis;
    }
    private PlatformSourceManager()
    {
        m_pThis = this;
        m_pSourceList = new ArrayList<>();
        RefreshSource();
        Log.d(DEBUG_TAG,"PlatformSourceManager Create!");
    }
    @Override
    public void RefreshSource()
    {
        GetSourceList();
        String currentSourceId = GetCurTifSourceIdBySP();
        Log.d(DEBUG_TAG,"currentSourceId : "+currentSourceId);
            for(int i=0;i<m_pSourceList.size();i++)
            {
                TvInputInfo tifsource = (TvInputInfo)m_pSourceList.get(i).getOtherInfo(TIF_OBJ);
                if(currentSourceId.compareTo(EMPTY)!=0)
                {
                    if(tifsource.getId().compareTo(currentSourceId) == 0)
                    {
                        m_pCurrentSource = m_pSourceList.get(i);
                        break;
                    }
                }
                else
                {
                    if(m_pSourceList.get(i).getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV)
                    {
                        m_pCurrentSource = m_pSourceList.get(i);
                        break;
                    }
                }
            }
    }
    private boolean TifSourceIsAtv(TvInputInfo info)
    {
        String SourceTifId = info.getId();
        String TifId = SourceTifId.substring(SourceTifId.length()-3,SourceTifId.length());
        return (info.getType() == TYPE_TUNER && TifId.equals(PlatformManager.getInstance().GetApplicationContext().getString(R.string.TIF_ATV_ID)));
    }
    private boolean TifSourceIsDtv(TvInputInfo info)
    {
        String SourceTifId = info.getId();
        String TifId = SourceTifId.substring(SourceTifId.length()-3,SourceTifId.length());
        return (info.getType() == TYPE_TUNER && TifId.equals(PlatformManager.getInstance().GetApplicationContext().getString(R.string.TIF_DVB_T_ID)));
    }
    private boolean TifSourceIsDvbc(TvInputInfo info)
    {
        String SourceTifId = info.getId();
        String TifId = SourceTifId.substring(SourceTifId.length()-3,SourceTifId.length());
        return (info.getType() == TYPE_TUNER && TifId.equals(PlatformManager.getInstance().GetApplicationContext().getString(R.string.TIF_DVB_C_ID)));
    }
    private boolean TifSourceIsDvbt(TvInputInfo info)
    {
        String SourceTifId = info.getId();
        String TifId = SourceTifId.substring(SourceTifId.length()-3,SourceTifId.length());
        return (info.getType() == TYPE_TUNER && TifId.equals(PlatformManager.getInstance().GetApplicationContext().getString(R.string.TIF_DVB_T_ID)));
    }
    private boolean TifSourceIsDvbs(TvInputInfo info)
    {
        String SourceTifId = info.getId();
        String TifId = SourceTifId.substring(SourceTifId.length()-3,SourceTifId.length());
        return (info.getType() == TYPE_TUNER && TifId.equals(PlatformManager.getInstance().GetApplicationContext().getString(R.string.TIF_DVB_S_ID)));
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected Source GetSourceByTifId(String inputId)
    {
        List<Source> sourceList = GetSourceList();
        for(Source source:sourceList)
        {
            TvInputInfo inputInfo = (TvInputInfo) source.getOtherInfo(TIF_OBJ);
            if(inputInfo.getId().compareTo(inputId) == 0)
            {
                return source;
            }
        }
        return null;
    }
    private boolean TifSourceIsAv(TvInputInfo info)
    {
        String SourceTifId = info.getId();
        String TifId = SourceTifId.substring(SourceTifId.length()-3,SourceTifId.length());
        return (info.getType() == TYPE_COMPOSITE && TifId.equals(PlatformManager.getInstance().GetApplicationContext().getString(R.string.TIF_AV_ID)));
    }
    private boolean TifSourceIsHDMI(TvInputInfo info,int port_id)
    {
        String SourceTifId = info.getId();
        String TifId = SourceTifId.substring(SourceTifId.length()-3,SourceTifId.length());
        return (info.getType() == TvInputInfo.TYPE_HDMI && TifId.equals(PlatformManager.getInstance().GetApplicationContext().getString(port_id)));
    }
    private boolean TifSourceIsHDMI_CEC(TvInputInfo info,int port_id) {
        String ParentId = info.getParentId();
        return ParentId != null && (info.getType() == TvInputInfo.TYPE_HDMI && ParentId.contains(PlatformManager.getInstance().GetApplicationContext().getString(port_id)));
    }
    private Source PutTifInfoIntoHdmiSource(TvInputInfo tvInputInfo, String name, Map<String,Source> hdmiMap)
    {
        Source source = null;
        if(TifSourceIsHDMI_CEC(tvInputInfo,R.string.TIF_HDMI1_ID))
        {
            source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI1,name);
        }
        else if(TifSourceIsHDMI_CEC(tvInputInfo,R.string.TIF_HDMI2_ID))
        {
            source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI2,name);
        }
        else if(TifSourceIsHDMI_CEC(tvInputInfo,R.string.TIF_HDMI3_ID))
        {
            source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI3,name);
        }
        if(source != null) {
            hdmiMap.put(tvInputInfo.getParentId(),source);
        }
        if(TifSourceIsHDMI(tvInputInfo,R.string.TIF_HDMI1_ID))
        {
            if(!hdmiMap.containsKey(tvInputInfo.getId()))
            {
                source =  new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI1,name);
            }
        }
        else if(TifSourceIsHDMI(tvInputInfo,R.string.TIF_HDMI2_ID))
        {
            if(!hdmiMap.containsKey(tvInputInfo.getId())) {
                source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI2, name);
            }
        }
        else if(TifSourceIsHDMI(tvInputInfo,R.string.TIF_HDMI3_ID))
        {
            if(!hdmiMap.containsKey(tvInputInfo.getId())) {
                source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI3, name);
            }
        }
        return source;
    }
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public List<Source> GetSourceList() {
        m_pSourceList.clear();
        Map<String,Source> hdmiMap = new HashMap<>();
        List<TvInputInfo> tvInputInfoList = PlatformManager.getInstance().GetTvInputManager().getTvInputList();
        for (TvInputInfo tvInputInfo : tvInputInfoList) {
            Log.d(DEBUG_TAG, "Tvinfo id: " + tvInputInfo.getId());
            String name = String.valueOf(tvInputInfo.loadCustomLabel(PlatformManager.getInstance().GetApplicationContext()));
            if (name.compareTo("null") == 0) {
                name = String.valueOf(tvInputInfo.loadLabel(PlatformManager.getInstance().GetApplicationContext()));
            }
            String TifSourceId = tvInputInfo.getId();
            Source source = null;
            if (!tvInputInfo.getId().contains("/HW") && !tvInputInfo.getId().contains("hdmi")) {
                source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION, name);
                source.putOtherInfo(TIF_OBJ, tvInputInfo);

            } else {
                if (TifSourceIsAtv(tvInputInfo)) {
                    source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV, name);
                } else if (TifSourceIsDvbt(tvInputInfo)) {
                    source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T, name);
                } else if (TifSourceIsDvbc(tvInputInfo)) {
                    source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C, name);
                } else if (TifSourceIsDvbs(tvInputInfo)) {
                    source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S, name);
                } else if (TifSourceIsDtv(tvInputInfo)) {
                    source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S, name);
                } else if (TifSourceIsDvbs(tvInputInfo)) {
                    source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S, name);
                } else if (TifSourceIsAv(tvInputInfo)) {
                    source = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_CVBS, name);
                } else if (TifSourceIsDtv(tvInputInfo)) {
                    continue;
                } else {
                    source = PutTifInfoIntoHdmiSource(tvInputInfo,name,hdmiMap);
                }
            }
            if (source != null) {
                source.putOtherInfo(TIF_OBJ, tvInputInfo);
                m_pSourceList.add(source);
            }
        }
        hdmiMap.clear();
        return m_pSourceList;
    }

    public void SaveCurTifSourceIdBySP(String inputID){
        Context context = PlatformManager.getInstance( ).GetApplicationContext( );
        SPUtil.getInstance(context).putString(CUR_TIF_SOURECE_ID_KEY,inputID);
    }


    public String GetCurTifSourceIdBySP(){
        Context context = PlatformManager.getInstance( ).GetApplicationContext( );
        return SPUtil.getInstance(context).getString(CUR_TIF_SOURECE_ID_KEY);
    }
    @Override
    public boolean SetSource(Source source) {
        Log.d(DEBUG_TAG,"Set source:" + source.getName() + " type: " + source.getType().toString());
        TvInputInfo tvInputInfo = (TvInputInfo) source.getOtherInfo(TIF_OBJ);
        PlatformTvView view = (PlatformTvView) PlatformManager.getInstance( ).GetPlatformView( );
        view.ResetView();
        if (source.getType( ) == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION) {
            Context activity = PlatformManager.getInstance().GetApplicationContext();
            boolean hasChannel = PlatformChannelManager.getInstance().hasTIFChannelInfoBySource(tvInputInfo.getId());
            if (hasChannel) {
                Log.d(DEBUG_TAG,"Send event to change to :" + tvInputInfo.getId());
                Tv_EventInfo source_info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SOURCE_CHANGE.ordinal(), (long)source.getType().ordinal());
                EventManager.getInstance().sendBroadcast(source_info);
                Tv_EventInfo signal_info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SCREEN_SERVER_MODE_CHANGE.ordinal(),(long) EN_SERVICE_STATUS.E_SERVICE_STATUS_HAS_SIGNAL.ordinal());
                EventManager.getInstance().sendBroadcast(signal_info);
            } else {
                Intent setupIntent = tvInputInfo.createSetupIntent( );
                Toast.makeText(activity, "Goto Setup!", Toast.LENGTH_LONG).show( );
                setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(setupIntent);
            }
        }else{
            Uri channelUri = Uri.parse("content://main");
            view.tune(tvInputInfo.getId(),channelUri);
            view.setStreamVolume((float) 1.0);
        }
        m_pCurrentSource = source;
        SaveCurTifSourceIdBySP(tvInputInfo.getId());
        return true;
    }
    @Override
    public boolean SetSource(EN_INPUT_SOURCE_TYPE type)
    {
        if(type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
        {
            Log.w(DEBUG_TAG,"please use other setSource!");
            return false;
        }
        TvInputInfo tvInputInfo = null;
        PlatformTvView view = (PlatformTvView) PlatformManager.getInstance( ).GetPlatformView( );
        Uri channelUri = Uri.parse("content://main");
        for(Source source:m_pSourceList)
        {
            if(source.getType() == type)
            {
                tvInputInfo = (TvInputInfo) source.getOtherInfo(TIF_OBJ);
                m_pCurrentSource = source;
                SaveCurTifSourceIdBySP(tvInputInfo.getId());
                break;
            }
        }
        if(tvInputInfo!=null)
        {
            view.reset();
            view.tune(tvInputInfo.getId(),channelUri);
            view.setStreamVolume((float) 1.0);
            return true;
        }
        return false;
    }

    @Override
    public int GetSourceNumber() {
        return m_pSourceList.size();
    }

    @Override
    public Source GetCurrentSource() {
        return m_pCurrentSource;
    }
}
