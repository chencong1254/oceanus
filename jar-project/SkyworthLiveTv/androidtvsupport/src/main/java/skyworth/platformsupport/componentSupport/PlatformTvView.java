package skyworth.platformsupport.componentSupport;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputManager;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.platform.ui.IPlatformView;

import java.util.List;

import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/4/28.
 */

    public class PlatformTvView extends TvView implements IPlatformView{
    public class CallBack extends TvInputCallback
    {
        public CallBack() {
            super();
        }

        @Override
        public void onConnectionFailed(String inputId) {
            super.onConnectionFailed(inputId);
            Log.d(DEBUG_TAG,"TvInputCallback----------->onConnectionFailed["+inputId+"]!");
        }

        @Override
        public void onDisconnected(String inputId) {
            super.onDisconnected(inputId);
            Log.d(DEBUG_TAG,"TvInputCallback----------->onDisconnected["+inputId+"]!");
        }

        @Override
        public void onChannelRetuned(String inputId, Uri channelUri) {
            super.onChannelRetuned(inputId, channelUri);
            Log.d(DEBUG_TAG,"TvInputCallback----------->onChannelRetuned["+inputId+"]! Uri: " + channelUri );
        }

        @Override
        public void onTracksChanged(String inputId, List<TvTrackInfo> tracks) {
            super.onTracksChanged(inputId, tracks);
            Log.d(DEBUG_TAG,"TvInputCallback----------->onTracksChanged["+inputId+"]!");
        }

        @Override
        public void onTrackSelected(String inputId, int type, String trackId) {
            Log.d(DEBUG_TAG,"onTrackSelected: INPUT: " + inputId);
            Log.d(DEBUG_TAG,"type: " + type);
            Log.d(DEBUG_TAG,"tarckId: " + trackId);
            super.onTrackSelected(inputId, type, trackId);
        }

        @Override
        public void onVideoSizeChanged(String inputId, int width, int height) {
            Log.d(DEBUG_TAG,"TvInputCallback----------->onVideoSizeChanged["+inputId+"]!");
            Log.d(DEBUG_TAG,"TvInputCallback----------->onVideoSizeChanged["+width+"/"+height+"]!");
        }

        @Override
        public void onVideoAvailable(String inputId) {
            Log.d(DEBUG_TAG,"TvInputCallback----------->onVideoAvailable["+inputId+"]!");
            if(inputId.contains("/HW"))
            {
                return;
            }
            Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SCREEN_SERVER_MODE_CHANGE.ordinal(), EN_SERVICE_STATUS.E_SERVICE_STATUS_HAS_SIGNAL.ordinal());
            EventManager.getInstance().sendBroadcast(info);
        }

        @Override
        public void onVideoUnavailable(String inputId, int reason) {
            Log.d(DEBUG_TAG,"TvInputCallback----------->onVideoUnavailable["+inputId + "["+reason+"]"+"]!");
            EN_SERVICE_STATUS status = EN_SERVICE_STATUS.E_SERVICE_STATUS_UNKNONW_STATE;
            switch (reason) {
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING: {
                    status = EN_SERVICE_STATUS.E_SERVICE_STATUS_APP_BUFFING;
                }
                break;
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL: {
                    status = EN_SERVICE_STATUS.E_SERVICE_STATUS_UNSTABLE;
                }
                break;
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_BUFFERING: {
                    status = EN_SERVICE_STATUS.E_SERVICE_STATUS_APP_BUFFING;
                }
                break;
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY: {
                    status = EN_SERVICE_STATUS.E_SERVICE_STATUS_AUDIO_ONLY;
                }
                break;
                default:
                    break;
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN:
                    break;
            }
            Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SCREEN_SERVER_MODE_CHANGE.ordinal(),status.ordinal());
            EventManager.getInstance().sendBroadcast(info);
        }

        @Override
        public void onContentAllowed(String inputId) {
            Log.d(DEBUG_TAG,"TvInputCallback----------->onContentAllowed["+inputId+"]!");
        }

        @Override
        public void onContentBlocked(String inputId, TvContentRating rating) {
            super.onContentBlocked(inputId, rating);
            Log.d(DEBUG_TAG,"TvInputCallback----------->onContentBlocked["+inputId+"]!");
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onTimeShiftStatusChanged(String inputId, int status) {
            super.onTimeShiftStatusChanged(inputId, status);
            Log.d(DEBUG_TAG,"TvInputCallback----------->onTimeShiftStatusChanged["+inputId+"]!");
        }
    }
    public PlatformTvView(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void InitView()
    {
        setZOrderMediaOverlay(false);
        setCallback(new CallBack());
    }
    public void BindLayout(FrameLayout layout)
    {
        ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        this.requestLayout();
        layout.addView(this,layoutParams);
    }
    public void ResetView()
    {
        this.reset();
    }
    public PlatformTvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public PlatformTvView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
