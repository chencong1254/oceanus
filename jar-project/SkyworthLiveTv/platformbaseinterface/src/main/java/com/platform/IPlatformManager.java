package com.platform;

import android.content.Context;

import com.platform.service.IPlatformService;
import com.platform.ui.IPlatformView;

/**
 * Created by sky057509 on 2017/4/26.
 */

public interface IPlatformManager {
    IPlatformService GetService();
    IPlatformSourceManager GetSourceManager();
    IPlatformChannelManager GetChannelManager();
    IPlatformSubtitleManager GetSubtitleManager();
    IPlatformEpgManager GetEpgManager();
    Context GetApplicationContext();
    IPlatformView GetPlatformView();
    void onCreate(Context ApplicationContext);
    void onDestory();
}
