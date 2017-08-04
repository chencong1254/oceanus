package com.platform.ui;

import android.content.Intent;

/**
 * Created by sky057509 on 2017/6/13.
 */

public interface IPlatformTvScreenServerView {
    boolean showChannelAd();
    void showChannelLogo();
    void hideChannelLogo();
    boolean hasChannelLogo();
    Intent getIntent();
    boolean showChannelSpecialView();
}
