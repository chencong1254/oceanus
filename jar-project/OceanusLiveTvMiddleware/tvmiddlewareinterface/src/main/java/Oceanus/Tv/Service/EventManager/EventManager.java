package Oceanus.Tv.Service.EventManager;

import android.util.Log;

import com.mediatek.twoworlds.tv.TVCallback;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.IEventAdapter;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;

/**
 * Created by heji@skyworth.com on 2016/7/7.
 */

public final class EventManager {

    private static final String LOG_TAG = "OSrv_EventService";
    private static EventManager mOSrv_EventService = null;
    private static IEventAdapter mObj_EventAdapter = null;
    private static TVCallback mObj_TvCallBack = null;
    public static EventManager getInstance()
    {
        synchronized(EventManager.class)
            {
                if (mOSrv_EventService == null)
                {
                    new EventManager();
                    mObj_EventAdapter = IEventAdapter.getInstance();
                    mObj_TvCallBack.registerCallback(mObj_EventAdapter);

                }
            }
        return mOSrv_EventService;
    }
    private EventManager()
    {
        Log.d(LOG_TAG,"OSrv_EventService Created~");
        mOSrv_EventService = this;
        mObj_TvCallBack = new TVCallback();
        Connect();
    }
    static
    {
        Log.d(LOG_TAG,"EventManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_EventManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_EventManager library:\n" + e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mObj_TvCallBack.unregisterCallback(mObj_EventAdapter);
        Disconnect();
    }
    public void  registeEventListener(Tv_EventListener listener, EN_OSYSTEM_EVENT_LIST event)
    {
        RegisteEventListener(listener,listener.getListenerName(),event.ordinal());
    }
    public void unregisteEventListener(Tv_EventListener listener,EN_OSYSTEM_EVENT_LIST event)
    {
        UnregisteEventListener(listener.getListenerName(),event.ordinal());
    }
    public void sendEvent(Tv_EventInfo info, String listener_name)
    {
        SendEvent(info,listener_name);
    }
    public void sendBroadcast(Tv_EventInfo info)
    {
        SendBroadcast(info);
    }
    private native void SendEvent(Tv_EventInfo info, String listener_name);
    private native void SendBroadcast(Tv_EventInfo info);
    private native void RegisteEventListener(Tv_EventListener listener, String name, int event);
    private native void UnregisteEventListener(String listener_name, int event);
    private native void Connect();
    private native void Disconnect();
}
