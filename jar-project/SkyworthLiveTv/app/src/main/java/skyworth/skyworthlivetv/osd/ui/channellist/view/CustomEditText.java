package skyworth.skyworthlivetv.osd.ui.channellist.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by yangxiong on 2017/5/18.
 */

public class CustomEditText extends EditText {
    private String str;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean   isInterceptKeyUp = true;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        int action = event.getAction( );
        if (action == KeyEvent.ACTION_UP && isInterceptKeyUp){
            isInterceptKeyUp =false;
            return true;
        }
        //
        Log.e("keyboard", "action" + action);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.setFocusable(false);
            clearFocus( );
            isInterceptKeyUp =true;
        }
        return super.onKeyPreIme(keyCode, event);
    }


}
