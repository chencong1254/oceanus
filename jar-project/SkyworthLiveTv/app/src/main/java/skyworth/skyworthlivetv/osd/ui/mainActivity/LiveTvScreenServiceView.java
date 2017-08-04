package skyworth.skyworthlivetv.osd.ui.mainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;
import skyworth.platformsupport.PlatformManager;
import skyworth.platformsupport.componentSupport.PlatformTvScreenServerView;
import skyworth.skyworthlivetv.R;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/6/5.
 */

public class LiveTvScreenServiceView extends FrameLayout {
    private RelativeLayout screen_service_status = null;
    private TextView signalStatus = null;
    private ProgressBar loading = null;
    private ImageView screen_background = null;
    private PlatformTvScreenServerView PlatformScreenView = null;
    private LiveTvScreenActivity MainActivity = null;
    public LiveTvScreenServiceView(Context context) {
        super(context);
        InitView(context);
    }

    public LiveTvScreenServiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public LiveTvScreenServiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    public LiveTvScreenServiceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        InitView(context);
    }
    private void InitView(Context context)
    {
        MainActivity = (LiveTvScreenActivity) context;
        LayoutInflater.from(context).inflate(R.layout.screen_service_view,this);
        PlatformScreenView = new PlatformTvScreenServerView(context);
        PlatformScreenView.setVisibility(INVISIBLE);
        screen_service_status = (RelativeLayout) findViewById(R.id.screen_service_status);
        signalStatus = (TextView) findViewById(R.id.signal);
        loading = (ProgressBar) findViewById(R.id.screen_service_loading);
        screen_background = (ImageView) findViewById(R.id.screen_service_background);
    }
    public void ProcessTvEvent(Tv_EventInfo eventInfo)
    {
        EN_OSYSTEM_EVENT_LIST event = EN_OSYSTEM_EVENT_LIST.values()[eventInfo.getEventType()];
        switch (event)
        {
            case E_SYSTEM_EVENT_CHANNEL_CHANGE:
            {
                if(PlatformManager.getInstance().GetChannelManager().GetCurrentChannel().getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
                {

                }
            }
            break;
            case E_SYSTEM_EVENT_SCREEN_SERVER_MODE_CHANGE:
            {
                EN_SERVICE_STATUS status = EN_SERVICE_STATUS.values()[(int) eventInfo.getInfoNumber()];
                RefreshSignalStatus(status);
            }
            break;
            default:break;
        }
    }
    public void RefreshSignalStatus(EN_SERVICE_STATUS status)
    {
        Log.d(DEBUG_TAG,"RefreshSignalStatus: " + status.toString());
        switch (status)
        {
            case E_SERVICE_STATUS_AUDIO_ONLY:
            {
                signalStatus.setText(R.string.AudioOnly);
                signalStatus.setVisibility(VISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_DATA_ONLY:
            {
                signalStatus.setText(R.string.DataOnly);
                signalStatus.setVisibility(VISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_HAS_SIGNAL:
            {
                screen_service_status.setVisibility(INVISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                signalStatus.setVisibility(INVISIBLE);
            }
            break;
            case E_SERVICE_STATUS_LOCK_SIGNAL:
            {
                signalStatus.setText(R.string.LockSignal);
                loading.setVisibility(INVISIBLE);
                signalStatus.setVisibility(VISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_NO_CI:
            {
                signalStatus.setText(R.string.NoCiCard);
                signalStatus.setVisibility(VISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_NO_SIGNAL:
            {
                signalStatus.setText(R.string.NoSignal);
                loading.setVisibility(INVISIBLE);
                signalStatus.setVisibility(VISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_UNSUPPORT_SIGNAL:
            {
                signalStatus.setText(R.string.UnSupport);
                signalStatus.setVisibility(VISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_UNSTABLE:
            {
                signalStatus.setText(R.string.UnStable);
                signalStatus.setVisibility(VISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_NO_CHANNEL:
            {
                signalStatus.setText(R.string.NoChannel);
                signalStatus.setVisibility(VISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            case E_SERVICE_STATUS_APP_BUFFING:
            {
                if(PlatformScreenView.hasChannelLogo())
                {
                    Log.d(DEBUG_TAG,"##########hasChannelLogo############");
                }
                signalStatus.setVisibility(INVISIBLE);
                loading.setVisibility(VISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
            default:
            {
                signalStatus.setText(R.string.UnknowSignal);
                signalStatus.setVisibility(VISIBLE);
                loading.setVisibility(INVISIBLE);
                screen_background.setVisibility(INVISIBLE);
                screen_service_status.setVisibility(VISIBLE);
            }
            break;
        }
        this.bringToFront();
    }

}
