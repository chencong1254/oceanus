package skyworth.platformsupport.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.platform.service.IPlatformService;

import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.EventManager.Tv_EventListener;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/2.
 */

public class PlatformService extends Service implements IPlatformService{
    private final static String ListenerName = "PlatformServiceEventListener";
    private ServiceTvEventListener m_pListener = null;
    private ServiceBinder m_binder = null;
    private AlertDialog m_pSourceDialog = null;
    private boolean b_IsSourceDialogShow = false;
    private class ServiceTvEventListener extends Tv_EventListener
    {
        public ServiceTvEventListener(String listenerName) {
            super(listenerName);
        }

        @Override
        protected void onEvnet(Tv_EventInfo tv_eventInfo) {
            EN_OSYSTEM_EVENT_LIST event = EN_OSYSTEM_EVENT_LIST.values()[tv_eventInfo.getEventType()];
            switch (event)
            {

            }
        }
    }
    @Override
    public void ShowInputSource() {
        if(m_pSourceDialog!=null)
        {
            if(b_IsSourceDialogShow)
            {
                b_IsSourceDialogShow = false;
                m_pSourceDialog.dismiss();
            }
            else
            {
                m_pSourceDialog.show();
                b_IsSourceDialogShow = true;
            }
        }
        else
        {
            Log.e(DEBUG_TAG,"Input Source not create success!!!!");
        }
    }

    @Override
    public void SetGlobalSourceDialog(AlertDialog sourceDialog) {
        m_pSourceDialog = sourceDialog;
    }

    public class ServiceBinder extends Binder {

        public PlatformService getService(){
            return PlatformService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(DEBUG_TAG, "PlatformService-->onCreate()");
        m_binder = new ServiceBinder();
        m_pListener = new ServiceTvEventListener(ListenerName);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(DEBUG_TAG, "PlatformService-->onStart()");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(DEBUG_TAG, "PlatformService-->onDestroy()");
        if(m_pListener!=null)
        {

        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(DEBUG_TAG,"PlatformService-->onBind");
        return m_binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(DEBUG_TAG, "PlatformService-->onUnbind()");
        return super.onUnbind(intent);
    }
}
