package skyworth.skyworthlivetv.osd.ui.info;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.product.KeyMap;
import com.product.adpater.infobar.InfoBarViewAdapter;

import skyworth.skyworthlivetv.R;

import static skyworth.skyworthlivetv.global.GlobalDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/22.
 */

public class InfoBarView extends RelativeLayout{
    private int COUNT_TIME = 500;//ms
    private int HIDE_TIME = 6000;//ms
    private final static int MSG_VIEW_HIDE_COUNT_DOWN = 0;
    private final static int MSG_VIEW_REFRESH_COUNT_DOWN = 1;
    private boolean b_IsShow = false;
    private SimpleInfo simpleInfo = null;
    private DetailedInfo detailedInfo = null;
    private Handler m_pViewHandler = new Handler()
    {
        int currentShownTime = 0;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1)
            {
                case MSG_VIEW_HIDE_COUNT_DOWN:
                {
                    if(!b_IsShow)
                    {
                        currentShownTime = 0;
                        break;
                    }
                    currentShownTime += COUNT_TIME;
                    if(currentShownTime<HIDE_TIME)
                    {
                        Log.d(DEBUG_TAG,"currentShownTime: " + currentShownTime);
                        detailedInfo.RefreshSingleInfo();
                        Message new_msg = new Message();
                        new_msg.arg1 = MSG_VIEW_HIDE_COUNT_DOWN;
                        this.sendMessageDelayed(new_msg,COUNT_TIME);
                    }
                    else
                    {
                        Hide();
                        currentShownTime = 0;
                    }
                }
                break;
                case MSG_VIEW_REFRESH_COUNT_DOWN:
                {
                    currentShownTime =0;
                }
                break;
            }
        }
    };
    public InfoBarView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.livetv_infobar,this);
        InfoBarViewAdapter adApter = new InfoBarViewAdapter(getContext());
        simpleInfo = new SimpleInfo(this,adApter);
        detailedInfo = new DetailedInfo(this,adApter);
    }
    public InfoBarView(Context context) {
        super(context);
    }
    public boolean IsShow()
    {
        return b_IsShow;
    }
    private void Refresh()
    {
        Log.d(DEBUG_TAG,"Info bar -------------->Refresh");
        simpleInfo.Refresh();
        detailedInfo.Refresh();
    }
    public void Show()
    {
        if(!b_IsShow)
        {

            Log.d(DEBUG_TAG,"Info bar -------------->Show");
            bringToFront();
            this.setVisibility(VISIBLE);
            simpleInfo.Refresh();
            detailedInfo.Refresh();
            b_IsShow = true;
            setFocusable(true);
            setFocusableInTouchMode(true);
            Message msg = new Message();
            msg.arg1 = MSG_VIEW_HIDE_COUNT_DOWN;
            m_pViewHandler.sendMessageDelayed(msg,COUNT_TIME);
        }
        else
        {
            Refresh();
        }
    }
    public void Hide()
    {
        if(b_IsShow)
        {
            Log.d(DEBUG_TAG,"Info bar -------------->Hide");
            b_IsShow = false;
            detailedInfo.Hide();
            setFocusable(false);
            setFocusableInTouchMode(false);
        }
    }
@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.ACTION_DOWN == event.getAction()) {
            Message msg = new Message();
            msg.arg1 = MSG_VIEW_REFRESH_COUNT_DOWN;
            m_pViewHandler.sendMessage(msg);
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                    keyCode == KeyEvent.KEYCODE_DPAD_RIGHT )
            {
                return detailedInfo.onKeyDown(keyCode);
            }
            else if(keyCode == KeyMap.KEYCODE_SUBTITLE)
            {

            }
            else if(keyCode == KeyMap.KEYCODE_BACK)
            {
                Hide();
                return true;
            }
            else if(keyCode == KeyMap.KEYCODE_MENU)
            {
                Hide();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
