package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.view.KeyEvent;

import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvKeyEvent;
import com.mediatek.twoworlds.tv.MtkTvTeletext;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextPageBase;

import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.ITeletext;
import Oceanus.Tv.Service.TeletextManager.TeletextManagerDefinitions.EN_TTX_CMD;

/**
 * Created by sky057509 on 2017/3/10.
 */
public class TeletexImpl implements ITeletext{
    private static TeletexImpl mObj_This = null;
    private static MtkTvBanner mtkTvBanner = null;
    private static MtkTvTeletext mtkTvTeletext = null;
    private static List<MtkTvTeletextPageBase> MtkTvTTPageList = null;
    private boolean mbIsStart = false;
    private static MtkTvKeyEvent mtkTvKeyEvent = null;
    private TeletexImpl()
    {
        mtkTvKeyEvent = MtkTvKeyEvent.getInstance();
        mtkTvBanner = MtkTvBanner.getInstance();
        mtkTvTeletext = MtkTvTeletext.getInstance();
    }
    public static TeletexImpl getInstance()
    {
        if(mObj_This == null)
        {
            mObj_This = new TeletexImpl();
        }
        return mObj_This;
    }
    @Override
    public boolean EnableTeletext(boolean bIsEnable) {
        if(bIsEnable)
        {
            return mtkTvTeletext.start()==0;
        }
        else
        {
            return mtkTvTeletext.stop()==0;
        }
    }

    @Override
    public boolean IsTeletextExist() {
        return mtkTvBanner.isDisplayTtxIcon();
    }

    @Override
    public boolean TeletextPassCmd(EN_TTX_CMD Cmd) {
        int keycode = KeyCodeToCmd(Cmd);
        int nativeKeyCode = -1;
        if(keycode!=-1)
        {
            nativeKeyCode = mtkTvKeyEvent.androidKeyToDFBkey(keycode);
            mtkTvKeyEvent.sendKeyClick(nativeKeyCode);
            return true;
        }
        return false;
    }

    @Override
    public boolean IsTeletextStart() {
        return mbIsStart;
    }
    private int KeyCodeToCmd(EN_TTX_CMD cmd)
    {
        switch (cmd)
        {
            case E_TTX_KEY_UP:return KeyEvent.KEYCODE_DPAD_UP;
            case E_TTX_KEY_DOWN:return KeyEvent.KEYCODE_DPAD_DOWN;
            case E_TTX_KEY_RIGHT:return KeyEvent.KEYCODE_DPAD_RIGHT;
            case E_TTX_KEY_LEFT:return KeyEvent.KEYCODE_DPAD_LEFT;
            case E_TTX_KEY_ENTER:return KeyEvent.KEYCODE_DPAD_CENTER;
            case E_TTX_KEY_NUMBER_1: return  KeyEvent.KEYCODE_1;
            case E_TTX_KEY_NUMBER_2: return  KeyEvent.KEYCODE_2;
            case E_TTX_KEY_NUMBER_3: return  KeyEvent.KEYCODE_3;
            case E_TTX_KEY_NUMBER_4: return  KeyEvent.KEYCODE_4;
            case E_TTX_KEY_NUMBER_5: return  KeyEvent.KEYCODE_5;
            case E_TTX_KEY_NUMBER_6: return  KeyEvent.KEYCODE_6;
            case E_TTX_KEY_NUMBER_7: return  KeyEvent.KEYCODE_7;
            case E_TTX_KEY_NUMBER_8: return  KeyEvent.KEYCODE_8;
            case E_TTX_KEY_NUMBER_9: return  KeyEvent.KEYCODE_9;
            case E_TTX_KEY_NUMBER_0: return  KeyEvent.KEYCODE_0;
            case E_TTX_KEY_MIX:return KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
            case E_TTX_KEY_GOTO_INDEX:return KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
            case E_TTX_KEY_SIZE:return KeyEvent.KEYCODE_MEDIA_NEXT;
            case E_TTX_KEY_HOLD:return 231;
            case E_TTX_KEY_SUB_PAGE:return KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
            case E_TTX_KEY_REVEAL:return KeyEvent.KEYCODE_MEDIA_REWIND;
            case E_TTX_KEY_CANCEL:return KeyEvent.KEYCODE_MEDIA_STOP;
            case E_TTX_KEY_RED:return KeyEvent.KEYCODE_PROG_RED;
            case E_TTX_KEY_BLUE:return KeyEvent.KEYCODE_PROG_BLUE;
            case E_TTX_KEY_GREEN:return KeyEvent.KEYCODE_PROG_GREEN;
            case E_TTX_KEY_YELLOW:return KeyEvent.KEYCODE_PROG_YELLOW;
            default:return -1;
        }
    }
    protected void setTeletextStatus(boolean bIsStart)
    {
        mbIsStart = bIsStart;
    }
}
