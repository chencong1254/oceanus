package skyworth.skyworthlivetv.osd.ui.info;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.product.adpater.infobar.InfoBarViewAdapter;

import skyworth.skyworthlivetv.R;

import static java.lang.Thread.sleep;
import static skyworth.skyworthlivetv.global.GlobalDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/23.
 */

public class DetailedInfo implements Animation.AnimationListener{
    private static final int MAX_TEXT_NUMBER_IN_PAGE = 378;
    private boolean b_StartAnimation = true;
    private int currentPage = 1;
    private int currentPageNumber = 1;
    private InfoBarView m_pView = null;
    private InfoBarViewAdapter m_pAdpater = null;
    private TextView audioType = null;
    private TextView videoType = null;
    private TextView ratingInfo = null;
    private TextView currentProgTime = null;
    private TextView currentProgName = null;
    private TextView nextProgTime = null;
    private TextView nextProgName = null;
    private ImageView icon_1,icon_2,icon_3;
    private TextView detail_text_1 = null;
    private TextView detail_text_2 = null;
    private ProgressBar signalStrength = null;
    private ProgressBar signalQuality = null;
    private RelativeLayout ly_complex_info,ly_cirle_page;
    private LinearLayout tv_program_detals_fst,tv_program_detals_second;
    private TranslateAnimation translateAnimation_start = null;
    private TranslateAnimation translateAnimation_exit = null;
    private TextView signalQuality_Text = null;
    private TextView signalStrength_Text = null;

    protected DetailedInfo(InfoBarView view, InfoBarViewAdapter adpater)
    {
        m_pView = view;
        m_pAdpater = adpater;
        ly_complex_info = (RelativeLayout) m_pView.findViewById(R.id.complex_info);
        ly_cirle_page = (RelativeLayout) m_pView.findViewById(R.id.cirle_page);
        tv_program_detals_fst = (LinearLayout) m_pView.findViewById(R.id.details_tv_programe_fst);
        tv_program_detals_second = (LinearLayout) m_pView.findViewById(R.id.details_tv_programe_second);
        audioType = (TextView)m_pView.findViewById(R.id.info_channel_audiotype);
        videoType = (TextView) m_pView.findViewById(R.id.info_channel_videoinfo);
        ratingInfo = (TextView) m_pView.findViewById(R.id.info_channel_rating);
        currentProgTime = (TextView) m_pView.findViewById(R.id.info_current_prog_time);
        currentProgName = (TextView) m_pView.findViewById(R.id.info_current_prog_name);
        nextProgName = (TextView) m_pView.findViewById(R.id.in_channel_next_prog_name);
        nextProgTime = (TextView) m_pView.findViewById(R.id.in_channel_next_prog_time);
        icon_1 = (ImageView) m_pView.findViewById(R.id.icon_1);
        icon_2 = (ImageView) m_pView.findViewById(R.id.icon_2);
        icon_3 = (ImageView) m_pView.findViewById(R.id.icon_3);
        signalQuality_Text = (TextView) m_pView.findViewById(R.id.info_channel_signal_quality);
        signalStrength_Text = (TextView) m_pView.findViewById(R.id.info_channel_signal_strength);
        signalQuality = (ProgressBar) m_pView.findViewById(R.id.info_channel_signal_quality_bar);
        signalStrength = (ProgressBar) m_pView.findViewById(R.id.info_channel_signal_strength_bar);
        detail_text_1 = (TextView) m_pView.findViewById(R.id.detail_info_1);
        detail_text_2 = (TextView) m_pView.findViewById(R.id.detail_info_2);
        translateAnimation_start = new TranslateAnimation
                (
                        Animation.RELATIVE_TO_SELF, -2f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f
                );
        translateAnimation_exit = new TranslateAnimation
                (
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, -2f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f
                );
        translateAnimation_start.setDuration(900);
        translateAnimation_exit.setDuration(600);
        translateAnimation_start.setAnimationListener(this);
        translateAnimation_exit.setAnimationListener(this);

    }
    private void RefreshPage(int pageNumber,int page)
    {
        if(pageNumber>3)
        {
            pageNumber = 3;
        }
        if(page>3)
        {
            page = 1;
        }
        if(page<1)
        {
            page = 3;
        }
        this.currentPage = page;
        if(pageNumber <=1)
        {
            ly_complex_info.setVisibility(View.VISIBLE);
            tv_program_detals_fst.setVisibility(View.INVISIBLE);
            tv_program_detals_second.setVisibility(View.INVISIBLE);
            icon_1.setVisibility(View.GONE);
            icon_2.setVisibility(View.GONE);
            icon_3.setVisibility(View.GONE);
        }
        else if(pageNumber == 2)
        {
            icon_1.setVisibility(View.VISIBLE);
            icon_2.setVisibility(View.VISIBLE);
            icon_3.setVisibility(View.GONE);
            if(page ==1)
            {
                ly_complex_info.setVisibility(View.VISIBLE);
                tv_program_detals_fst.setVisibility(View.INVISIBLE);
                tv_program_detals_second.setVisibility(View.INVISIBLE);
                icon_1.setBackgroundResource(R.drawable.icon_change_current);
                icon_2.setBackgroundResource(R.drawable.icon_change);
            }
            else
            {
                ly_complex_info.setVisibility(View.INVISIBLE);
                tv_program_detals_fst.setVisibility(View.VISIBLE);
                tv_program_detals_second.setVisibility(View.INVISIBLE);
                icon_1.setBackgroundResource(R.drawable.icon_change);
                icon_2.setBackgroundResource(R.drawable.icon_change_current);
            }
        }
        else
        {
            icon_1.setVisibility(View.VISIBLE);
            icon_2.setVisibility(View.VISIBLE);
            icon_3.setVisibility(View.VISIBLE);
            if(page ==1)
            {
                ly_complex_info.setVisibility(View.VISIBLE);
                tv_program_detals_fst.setVisibility(View.INVISIBLE);
                tv_program_detals_second.setVisibility(View.INVISIBLE);
                icon_1.setBackgroundResource(R.drawable.icon_change_current);
                icon_2.setBackgroundResource(R.drawable.icon_change);
                icon_3.setBackgroundResource(R.drawable.icon_change);
            }
            else if(page == 2)
            {
                ly_complex_info.setVisibility(View.INVISIBLE);
                tv_program_detals_fst.setVisibility(View.VISIBLE);
                tv_program_detals_second.setVisibility(View.INVISIBLE);
                icon_1.setBackgroundResource(R.drawable.icon_change);
                icon_2.setBackgroundResource(R.drawable.icon_change_current);
                icon_3.setBackgroundResource(R.drawable.icon_change);
            }
            else if (page == 3)
            {
                ly_complex_info.setVisibility(View.INVISIBLE);
                tv_program_detals_fst.setVisibility(View.INVISIBLE);
                tv_program_detals_second.setVisibility(View.VISIBLE);
                icon_1.setBackgroundResource(R.drawable.icon_change);
                icon_2.setBackgroundResource(R.drawable.icon_change);
                icon_3.setBackgroundResource(R.drawable.icon_change_current);
            }
        }
    }
    void Refresh()
    {
        RefreshPage(0,1);
        currentPageNumber = 1;
        final String[] ChannelResolution = new String[1];
        final String[] ChannelAudioInfo = new String[1];
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void run() {
                int tryCount = 0;
                ChannelResolution[0] = m_pAdpater.GetCurrentResolution();
                ChannelAudioInfo[0] = m_pAdpater.GetAudioType();
                while (ChannelResolution[0] == null || ChannelAudioInfo[0] == null)
                {
                    tryCount++;
                    try {
                        sleep(500);
                        ChannelResolution[0] = m_pAdpater.GetCurrentResolution();
                        ChannelAudioInfo[0] = m_pAdpater.GetAudioType();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(tryCount<=3)
                    {
                        continue;
                    }
                    if(ChannelResolution[0]==null)
                    {
                        ChannelResolution[0] = m_pView.getContext().getString(R.string.No_video_info);
                    }
                    if(ChannelAudioInfo[0]==null)
                    {
                        ChannelAudioInfo[0] = m_pView.getContext().getString(R.string.No_Audio_info);
                    }
                }
                m_pView.post(new Runnable() {
                    @Override
                    public void run() {
                        videoType.setText(ChannelResolution[0]);
                        audioType.setText(ChannelAudioInfo[0]);
                        currentProgTime.setText(m_pAdpater.GetCurrentProgramTimeInfo()!=null?m_pAdpater.GetCurrentProgramTimeInfo():"");
                        currentProgName.setText(m_pAdpater.GetCurrentProgramTitle());
                        nextProgName.setText(m_pAdpater.GetNextProgramTitle()!=null?m_pAdpater.GetNextProgramTitle():"");
                        nextProgTime.setText(m_pAdpater.GetNextProgramTimeInfo()!=null?m_pAdpater.GetNextProgramTimeInfo():"");
                        ratingInfo.setText(m_pAdpater.GetCurrentChannelRating()!=null?m_pAdpater.GetCurrentChannelRating():m_pView.getContext().getString(R.string.No_Rating));
                        String detail = m_pAdpater.GetCurrentProgramDetial();
                        if(detail!=null)
                        {
                            Log.d(DEBUG_TAG,"detail: " + detail);
                            if(detail.length()>0)
                            {
                                if(detail.length()<MAX_TEXT_NUMBER_IN_PAGE)
                                {
                                    currentPageNumber = 2;
                                    detail_text_1.setText(detail);
                                }
                                else
                                {
                                    currentPageNumber = 3;
                                    detail_text_1.setText(detail.subSequence(0,MAX_TEXT_NUMBER_IN_PAGE-1));
                                    detail_text_2.setText(detail.substring(MAX_TEXT_NUMBER_IN_PAGE));
                                }
                            }
                            else
                            {
                                currentPageNumber = 1;
                            }
                            RefreshPage(currentPageNumber,1);
                        }
                        RefreshSingleInfo();
                    }
                });
            }
        }).start();
        if(b_StartAnimation)
        {
            ly_complex_info.startAnimation(translateAnimation_start);
            ly_cirle_page.startAnimation(translateAnimation_start);
            b_StartAnimation = false;
        }
    }
    void RefreshSingleInfo()
    {
        if(m_pAdpater.GetSignalStrength() == -1 && m_pAdpater.GetSignalQuality() == -1)
        {
            signalQuality.setVisibility(View.INVISIBLE);
            signalStrength.setVisibility(View.INVISIBLE);
            signalQuality_Text.setVisibility(View.INVISIBLE);
            signalStrength_Text.setVisibility(View.INVISIBLE);
            return;
        }
        signalStrength.setProgress(m_pAdpater.GetSignalStrength());
        signalQuality.setProgress(m_pAdpater.GetSignalQuality());
    }
    boolean onKeyDown(int keyCode) {

        switch (keyCode)
        {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            {
                RefreshPage(currentPageNumber,currentPage-1);
            }
            break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            {
                RefreshPage(currentPageNumber,currentPage+1);
            }
            break;
            default:
                break;
        }
        return true;
    }
    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation == translateAnimation_exit)
        {
            Log.d(DEBUG_TAG,"Hide translateAnimation_exit end!");
            RefreshPage(0,1);
            m_pView.setVisibility(View.INVISIBLE);
            b_StartAnimation = true;
            translateAnimation_start.reset();
            translateAnimation_exit.reset();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    void Hide()
    {
        RefreshPage(currentPageNumber,1);
        currentPageNumber = 1;
        ly_complex_info.startAnimation(translateAnimation_exit);
        ly_cirle_page.startAnimation(translateAnimation_exit);
    }
}
