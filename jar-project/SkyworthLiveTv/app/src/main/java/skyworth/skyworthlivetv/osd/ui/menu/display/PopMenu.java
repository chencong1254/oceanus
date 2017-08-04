package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.SkyScreenParams;
import skyworth.skyworthlivetv.osd.common.data.DataUtil;
import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.RangeData;
import skyworth.skyworthlivetv.osd.common.data.SwitchData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.R;

/**
 * Created by xeasy on 2017/4/28.
 */

public class PopMenu  extends FrameLayout implements  View.OnFocusChangeListener{
    private MenuItemData menuItemData;
    private Context mContext;
    private View childChoseLayout;
    private TextView mVChoseTitle;
    private ImageView mVChoseImgLeft;
    private ImageView mVChoseImgRight;
    private TextView mVChoseValue;

    private View  childSeekLayout;
    private TextView mVSeekTx;
    private SeekBar mVSeekBar;
    private TextView mVSeekValue;

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    // 菜单项监听
    public interface PopMenuOnkeyListener
    {
        public boolean onKeyLeft(View v, MenuItemData currentData);

        public boolean onKeyRight(View v, MenuItemData currentData);

        public boolean onKeyBack(MenuItemData currentData);

        public boolean onKeyOther(View v, int keyCode);
    }
    private PopMenuOnkeyListener popMenuOnkeyListener;

    public PopMenu(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public void setData(MenuItemData menuItemData){
        this.menuItemData = menuItemData;
        TypedData typedData = menuItemData.getTypedData();
        if (typedData == null) return;
        TypedData.SkyDataType dataType = typedData.getType();
        switch(dataType)
        {
            case DATA_TYPE_ENUM:
                childChoseLayout.setVisibility(View.VISIBLE);
                childSeekLayout.setVisibility(View.GONE);
                mVChoseTitle.setText(menuItemData.getItemTitle());
                EnumData enumSetData = (EnumData)menuItemData.getTypedData();
                String enumTitle = enumSetData.getEnumTitleList().get(enumSetData.getCurrentIndex());
                Log.d(GlobalDefinitions.DEBUG_TAG," initData enumTitle:"+enumTitle);
                mVChoseValue.setText(enumTitle);
                break;
            case DATA_TYPE_SWITCH:
                childChoseLayout.setVisibility(View.VISIBLE);
                childSeekLayout.setVisibility(View.GONE);
                mVChoseTitle.setText(menuItemData.getItemTitle());
                SwitchData switchData = (SwitchData)menuItemData.getTypedData();
                mVChoseValue.setText(String.valueOf(switchData.isOn()));
                break;
            case DATA_TYPE_RANGE:
                childChoseLayout.setVisibility(View.GONE);
                childSeekLayout.setVisibility(View.VISIBLE);
                mVSeekTx.setText(menuItemData.getItemTitle());
                RangeData rangeData = (RangeData) typedData;
                mVSeekBar.setProgress(rangeData.getCurrent());
                mVSeekBar.setMax(rangeData.getMax());
                mVSeekValue.setText(String.valueOf(rangeData.getCurrent()));
                break;

        }
    };
    private void updateData(MenuItemData menuItemData){
        this.menuItemData = menuItemData;
        TypedData typedData = menuItemData.getTypedData();
        if (typedData == null) return;
        TypedData.SkyDataType dataType = typedData.getType();
        switch(dataType)
        {
            case DATA_TYPE_ENUM:
                mVChoseTitle.setText(menuItemData.getItemTitle());
                EnumData enumSetData = (EnumData)menuItemData.getTypedData();
                String enumTitle = enumSetData.getEnumTitleList().get(enumSetData.getCurrentIndex());
                Log.d(GlobalDefinitions.DEBUG_TAG," initData enumTitle:"+enumTitle);
                mVChoseValue.setText(enumTitle);
                break;
            case DATA_TYPE_SWITCH:
                mVChoseTitle.setText(menuItemData.getItemTitle());
                SwitchData switchData = (SwitchData)menuItemData.getTypedData();
                Log.d(GlobalDefinitions.DEBUG_TAG,"  switchData.isOn():"+switchData.isOn());
                mVChoseValue.setText(String.valueOf(switchData.isOn()));
                break;
            case DATA_TYPE_RANGE:
                mVSeekTx.setText(menuItemData.getItemTitle());
                RangeData rangeData = (RangeData) typedData;
                mVSeekBar.setProgress(rangeData.getCurrent());
                mVSeekBar.setMax(rangeData.getMax());
                mVSeekValue.setText(String.valueOf(rangeData.getCurrent()));
                break;

        }
    };
    private void initView()
    {
        int popMenuWidth = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_WIDTH);
        int popMenuHeight = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_HEIGHT);
        this.setLayoutParams(new LayoutParams(popMenuWidth, popMenuHeight));
        this.setFocusable(true);
        this.setBackgroundColor(Color.RED);
        this.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    Log.d(GlobalDefinitions.DEBUG_TAG,"  PopMenu onKeyDown:"+keyCode);
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if(DataUtil.changeMenuItemData(menuItemData.getTypedData(),true))
                            {
                                TypedData.SkyDataType  dataType = menuItemData.getTypedData().getType();
                                switch(dataType)
                                {
                                    case DATA_TYPE_SWITCH:
                                        SwitchData switchData = (SwitchData)menuItemData.getTypedData();
                                        Log.d(GlobalDefinitions.DEBUG_TAG,"  PopMenu KEYCODE_DPAD_LEFT switchData:"+switchData.isOn());
                                        break;
                                }
                                Log.d(GlobalDefinitions.DEBUG_TAG,"  PopMenu KEYCODE_DPAD_LEFT:"+popMenuOnkeyListener);
                                if (popMenuOnkeyListener != null)
                                {
                                    popMenuOnkeyListener.onKeyLeft(v,menuItemData);
                                    updateData(menuItemData);
                                }
                            }

                            return true;

                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if(DataUtil.changeMenuItemData(menuItemData.getTypedData(),false))
                            {
                                Log.d(GlobalDefinitions.DEBUG_TAG,"  PopMenu KEYCODE_DPAD_RIGHT:"+popMenuOnkeyListener);
                                if (popMenuOnkeyListener != null)
                                {
                                    popMenuOnkeyListener
                                            .onKeyRight(v,menuItemData);
                                    updateData(menuItemData);
                                }
                            }
                            return true;
                        case KeyEvent.KEYCODE_BACK:
                            if (popMenuOnkeyListener != null)
                            {
                                return popMenuOnkeyListener.onKeyBack(menuItemData);
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        LayoutInflater inflater = LayoutInflater.from(mContext);
        childChoseLayout = (RelativeLayout)inflater.inflate(R.layout.menu_item_multi_child_chose, null);
        childSeekLayout = (RelativeLayout)inflater.inflate(R.layout.menu_item_multi_child_seek, null);

        mVChoseTitle = (TextView) childChoseLayout.findViewById(R.id.menu_chose_title);
        mVChoseTitle.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_TEXT_SIZE));
        mVChoseTitle.setTextColor(MenuConstant.THIRD_POPMENU_LEFTTEXT_COLOR);
        mVChoseImgLeft = (ImageView) childChoseLayout.findViewById(R.id.left);
        mVChoseImgRight = (ImageView) childChoseLayout.findViewById(R.id.right);
        mVChoseValue = (TextView) childChoseLayout.findViewById(R.id.chose_value);
        mVChoseValue.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_TEXT_SIZE));
        mVChoseValue.setTextColor(MenuConstant.THIRD_POPMENU_RIGHTTEXT_COLOR);

        mVSeekTx = (TextView)  childSeekLayout.findViewById(R.id.menu_seek_title);
        mVSeekTx.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_TEXT_SIZE));
        mVSeekTx.setTextColor(MenuConstant.THIRD_POPMENU_LEFTTEXT_COLOR);
        mVSeekValue = (TextView)  childSeekLayout.findViewById(R.id.seek_value);
        mVSeekValue.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_TEXT_SIZE));
        mVSeekValue.setTextColor(MenuConstant.THIRD_POPMENU_LEFTTEXT_COLOR);
        mVSeekBar = (SeekBar)  childSeekLayout.findViewById(R.id.seek_bar);

        this.addView(childChoseLayout);
        this.addView(childSeekLayout);

   }
    public void setPopMenuOnkeyListener(PopMenuOnkeyListener popMenuOnkeyListener)
    {
        this.popMenuOnkeyListener = popMenuOnkeyListener;
    }
}