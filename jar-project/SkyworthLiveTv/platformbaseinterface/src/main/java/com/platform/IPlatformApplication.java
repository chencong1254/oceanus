package com.platform;

import com.platform.service.IPlatformService;

/**
 * Created by sky057509 on 2017/5/22.
 */

public interface IPlatformApplication {
    void onPlatformServiceBind(IPlatformService service);
}
