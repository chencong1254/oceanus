package skyworth.platformsupport.componentSupport;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.tv.TvContract;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.platform.ui.IPlatformTvScreenServerView;

import java.net.URISyntaxException;

import Oceanus.Tv.Service.ChannelManager.Channel;
import skyworth.androidtvsupport.R;
import skyworth.platformsupport.PlatformChannelManager;

import static com.platform.CommonDefinitions.DEBUG_TAG;
import static skyworth.platformsupport.PlatformSourceManager.TIF_OBJ;

/**
 * Created by sky057509 on 2017/6/13.
 */

public class PlatformTvScreenServerView extends RelativeLayout implements IPlatformTvScreenServerView {
    public static final String EXTRA_APP_LINK_CHANNEL_URI = "app_link_channel_uri";
    private ImageView Preview = null;
    private ImageView Ad = null;
    private Intent mAppLinkIntent = null;
    public PlatformTvScreenServerView(Context context) {
        super(context);
        InitView(context);
    }

    public PlatformTvScreenServerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public PlatformTvScreenServerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    public PlatformTvScreenServerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        InitView(context);
    }
    void InitView(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.androidtv_ad,this);
        Preview = (ImageView) findViewById(R.id.image_preview);
        Ad = (ImageView) findViewById(R.id.image_ad);
    }

    @Override
    public boolean showChannelAd() {
        return false;
    }

    @Override
    public void showChannelLogo() {

    }

    @Override
    public void hideChannelLogo() {

    }

    @Override
    public boolean hasChannelLogo() {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if (currentChannel != null) {
            TifChannelInfo channelInfo = (TifChannelInfo) currentChannel.getDtvAttr().getOtherInfo(TIF_OBJ);
            return channelInfo.getChannelLogo()!=null;
        }
        return false;
    }

    @Override
    public Intent getIntent()
    {
        PackageManager pm = this.getContext().getPackageManager();
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if (currentChannel != null) {
            TifChannelInfo channelInfo  = (TifChannelInfo) currentChannel.getDtvAttr().getOtherInfo(TIF_OBJ);
            String mAppLinkIntentUri = channelInfo.getAppLinkIntentUri();
            if (!TextUtils.isEmpty(channelInfo.getAppLinkText()) && !TextUtils.isEmpty(mAppLinkIntentUri)) {
                try {
                    Intent intent = Intent.parseUri(mAppLinkIntentUri, Intent.URI_INTENT_SCHEME);
                    if (intent.resolveActivityInfo(pm, 0) != null) {
                        intent.putExtra(EXTRA_APP_LINK_CHANNEL_URI, TvContract.buildChannelUri(channelInfo.getId()).toString());
                        return intent;
                    } else {
                        Log.w(DEBUG_TAG, "No activity exists to handle : " + mAppLinkIntentUri);
                        return null;
                    }
                } catch (URISyntaxException e) {
                    Log.w(DEBUG_TAG, "Unable to set app link for " + mAppLinkIntentUri, e);
                    return null;
                }
            }
            /*
            if (channelInfo.getPackageName().equals(PlatformManager.getInstance().GetApplicationContext().getPackageName())) {
                return;
            }
            mAppLinkIntent = pm.getLeanbackLaunchIntentForPackage(mPackageName);
            if (mAppLinkIntent != null) {
                mAppLinkIntent.putExtra(EXTRA_APP_LINK_CHANNEL_URI,
                        getUri().toString());
                mAppLinkType = APP_LINK_TYPE_APP;
            }
            */
        }
        return null;
    }
    @Override
    public boolean showChannelSpecialView() {
       PackageManager pm = this.getContext().getPackageManager();
       Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if (currentChannel != null) {
            TifChannelInfo channelInfo  = (TifChannelInfo) currentChannel.getDtvAttr().getOtherInfo(TIF_OBJ);
            String mAppLinkIntentUri = channelInfo.getAppLinkIntentUri();
            if (!TextUtils.isEmpty(channelInfo.getAppLinkText()) && !TextUtils.isEmpty(mAppLinkIntentUri)) {
                try {
                    Intent intent = Intent.parseUri(mAppLinkIntentUri, Intent.URI_INTENT_SCHEME);
                    if (intent.resolveActivityInfo(pm, 0) != null) {
                        intent.putExtra(EXTRA_APP_LINK_CHANNEL_URI, TvContract.buildChannelUri(channelInfo.getId()).toString());
                        return true;
                    } else {
                        Log.w(DEBUG_TAG, "No activity exists to handle : " + mAppLinkIntentUri);
                        return false;
                    }
                } catch (URISyntaxException e) {
                    Log.w(DEBUG_TAG, "Unable to set app link for " + mAppLinkIntentUri, e);
                    return false;
                }
            }
            /*
            if (channelInfo.getPackageName().equals(PlatformManager.getInstance().GetApplicationContext().getPackageName())) {
                return;
            }
            mAppLinkIntent = pm.getLeanbackLaunchIntentForPackage(mPackageName);
            if (mAppLinkIntent != null) {
                mAppLinkIntent.putExtra(EXTRA_APP_LINK_CHANNEL_URI,
                        getUri().toString());
                mAppLinkType = APP_LINK_TYPE_APP;
            }
            */
        }
        return false;
    }
}
