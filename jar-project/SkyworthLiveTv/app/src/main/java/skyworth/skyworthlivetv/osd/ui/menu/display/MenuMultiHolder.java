package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.SkyScreenParams;
import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.RangeData;
import skyworth.skyworthlivetv.osd.common.data.SingleSelectListData;
import skyworth.skyworthlivetv.osd.common.data.SwitchData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.R;

/**
 * Created by xeasy on 2017/5/2.
 */

public class MenuMultiHolder extends MenuBaseViewHolder {
    public View itemView;
    private Context mContext;

    private View mVChoise;
    private TextView mVChoseTitle;
    private ImageView mVChoseImgLeft;
    private ImageView mVChoseImgRight;
    private TextView mVChoseValue;

    private View mVSeek;
    private TextView mVSeekTx;
    private SeekBar mVSeekBar;
    private TextView mVSeekValue;

    private TypedData.SkyDataType currentDataType = TypedData.SkyDataType.DATA_TYPE_NONE;

    public MenuMultiHolder(View itemView,Context mContext) {
        super(itemView);
        this.mContext = mContext;
        this.itemView = itemView;
        mVChoise = itemView.findViewById(R.id.menu_chose);
        mVChoseTitle = (TextView) mVChoise.findViewById(R.id.menu_chose_title);
        mVChoseTitle.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_TEXT_SIZE));
        mVChoseImgLeft = (ImageView) mVChoise.findViewById(R.id.left);
        mVChoseImgLeft.setImageResource(R.drawable.menu_enum_left_arrow);
        mVChoseImgRight = (ImageView) mVChoise.findViewById(R.id.right);
        mVChoseImgRight.setImageResource(R.drawable.menu_enum_right_arrow);
        mVChoseValue = (TextView) mVChoise.findViewById(R.id.chose_value);
        mVChoseValue.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_TEXT_SIZE));

        mVSeek = itemView.findViewById(R.id.menu_seek);
        mVSeekTx = (TextView) mVSeek.findViewById(R.id.menu_seek_title);
        mVSeekTx.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_TEXT_SIZE));
        mVSeekValue = (TextView) mVSeek.findViewById(R.id.seek_value);
        mVSeekValue.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_TEXT_SIZE));
        mVSeekBar = (SeekBar) mVSeek.findViewById(R.id.seek_bar);

        itemView.setBackgroundResource(0);
        itemView.setVisibility(View.VISIBLE);

    }

    @Override
    protected String viewHolderName() {
        return "MenuMultiHolder";
    }

    @Override
    protected void initData(MenuItemData menuItemData){
        super.initData(menuItemData);

        TypedData typedData = menuItemData.getTypedData();
        if (typedData == null){
            mVChoseTitle.setText(menuItemData.getItemTitle());
            return;
        }
        currentDataType =typedData.getType();
        Log.d(GlobalDefinitions.DEBUG_TAG,"MenuMultiHolder initData dataType:"+currentDataType.toString());
        switch(currentDataType)
        {
            case DATA_TYPE_ENUM:
            {
                EnumData enumSetData = (EnumData)menuItemData.getTypedData();
                mVChoise.setVisibility(View.VISIBLE);
                mVSeek.setVisibility(View.GONE);
                mVChoseTitle.setText(menuItemData.getItemTitle());

                if(enumSetData!=null  && enumSetData.getEnumTitleList()!=null  && enumSetData.getCurrentIndex()>=0 && enumSetData.getCurrentIndex() <enumSetData.getEnumTitleList().size()) {
                    String enumTitle = enumSetData.getEnumTitleList().get(enumSetData.getCurrentIndex());
                    Log.d(GlobalDefinitions.DEBUG_TAG, " initData enumTitle:" + enumTitle);
                    mVChoseValue.setText(enumTitle);
                }
            }
               break;
            case DATA_TYPE_SINGLE_SELECT_LIST:
            {
                SingleSelectListData enumSetData = (SingleSelectListData)menuItemData.getTypedData();
                mVChoise.setVisibility(View.VISIBLE);
                mVSeek.setVisibility(View.GONE);
                mVChoseTitle.setText(menuItemData.getItemTitle());
                mVChoseImgLeft.setVisibility(View.GONE);
                mVChoseImgRight.setVisibility(View.GONE);
                if(enumSetData!=null  && enumSetData.getEnumTitleList()!=null  && enumSetData.getCurrentIndex()>=0 && enumSetData.getCurrentIndex() <enumSetData.getEnumTitleList().size()) {
                    String enumTitle = enumSetData.getEnumTitleList().get(enumSetData.getCurrentIndex());
                    Log.d(GlobalDefinitions.DEBUG_TAG, " initData enumTitle:" + enumTitle);
                    mVChoseValue.setText(enumTitle);
                }
            }
                break;
            case DATA_TYPE_SWITCH:
            {
                mVChoise.setVisibility(View.VISIBLE);
                mVSeek.setVisibility(View.GONE);
                mVChoseTitle.setText(menuItemData.getItemTitle());
                SwitchData switchData = (SwitchData)menuItemData.getTypedData();
                mVChoseValue.setText(switchData.getCurrentStr());
            }
                break;
            case DATA_TYPE_RANGE:
            {
                mVChoise.setVisibility(View.GONE);
                mVSeek.setVisibility(View.VISIBLE);
                mVSeekTx.setText(menuItemData.getItemTitle());
                RangeData rangeData = (RangeData) typedData;

                mVSeekBar.setProgress(rangeData.getCurrent());
                mVSeekBar.setMax(rangeData.getMax());
                mVSeekValue.setText(String.valueOf(rangeData.getCurrent()));
            }
                break;
            default:
                break;
        }
        if (menuItemData.isEnabled()) {
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setEnabled(true);
        } else {
            itemView.setFocusable(false);
            itemView.setFocusableInTouchMode(false);
            itemView.setEnabled(false);
        }
        if (menuItemData.isShow()) {
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
        }
    };
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"viewHolderName:"+viewHolderName()+"  hasFocus:"+hasFocus);
        super.onFocusChange(v, hasFocus);


        if(hasFocus){
            itemView.setBackgroundResource(R.drawable.menu_focus);
            mVChoseTitle.setSelected(true);
            mVChoseValue.setSelected(true);
            mVChoseImgLeft.setSelected(true);
            mVChoseImgRight.setSelected(true);
            mVSeekTx.setSelected(true);
            mVSeekValue.setSelected(true);

        }else{
            itemView.setBackgroundResource(0);
            mVChoseTitle.setSelected(false);
            mVChoseValue.setSelected(false);
            mVChoseImgLeft.setSelected(false);
            mVChoseImgRight.setSelected(false);
            mVSeekTx.setSelected(false);
            mVSeekValue.setSelected(false);
            }
        }

}
