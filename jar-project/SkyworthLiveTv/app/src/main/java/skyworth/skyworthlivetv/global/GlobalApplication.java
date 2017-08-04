package skyworth.skyworthlivetv.global;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.platform.IPlatformApplication;
import com.platform.IPlatformManager;
import com.platform.service.IPlatformService;

import skyworth.platformsupport.PlatformManager;
import skyworth.skyworthlivetv.osd.ui.inputSource.InputSourceDialog;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/4/26.
 */

public class GlobalApplication extends Application implements IPlatformApplication {
    static private Context GlobalApplication = null;
    static private IPlatformManager m_pPlatformManager = null;
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public GlobalApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
		GlobalApplication = this.getApplicationContext();
        Log.i(DEBUG_TAG,"GlobalApplication onCreate");
        m_pPlatformManager = PlatformManager.getInstance();
        m_pPlatformManager.onCreate(this);
    }
	public static Context getAppInstance(){
        return GlobalApplication;
    }
    @Override
    public void onPlatformServiceBind(IPlatformService service) {
        WindowManager wm =  (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        service.SetGlobalSourceDialog(InputSourceDialog.getInstance(this,wm));
    }
}
