package Oceanus.Tv.Service.EpgManager;

/**
 * Created by heji@skyworth.com on 2016/8/23.
 */
import java.util.Date;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EPG_FILTER_TYPE;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EVENT_CONTENT_TYPE;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EnWeekDay;

/**
 * Filter condition class of program event,basic parameters: program event content level, weekday,
 * start time, end time and channel.<br>
 * CN:节目事件过滤条件类,主要参数为:节目事件分类级别、星期、开始时间、结束时间和频道。<br>
 *
 * @see Channel
 */

public class EPGEventFilter
{
    private EN_EPG_FILTER_TYPE mFilterType;
    /**
     * Program event content level,.<br>
     * CN:节目分类级别一。<br>
     */
    private EN_EVENT_CONTENT_TYPE mContentType = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_OTHER;

    /**
     * Weekday,please see EnWeekDay enumeration.<br>
     * CN:星期，请参考EnWeekDay枚举类。<br>
     */
    private EnWeekDay mWeekday = null;

    /**
     * Start time.<br>
     * CN:开始时间。<br>
     */
    private Date mStartTime = null;

    /**
     * End time.<br>
     * CN:结束时间。<br>
     */
    private Date mEndTime = null;

    /**
     * Channel.<br>
     * CN:频道。<br>
     */
    private Channel mChannel = null;
    public EPGEventFilter(EN_EPG_FILTER_TYPE type)
    {
        mFilterType = type;
    }
    /**
     * Obtain program event level.<br>
     * CN:获取节目事件分类级别。<br>
     *
     * @return Program Event Level.<br>
     *         CN:节目事件分类级别。<br>
     */
    public EN_EPG_FILTER_TYPE getmFilterType() {
        return mFilterType;
    }

    /**
     * Obtain weekday in one week.<br>
     * CN:获取一星期中的星期数。<br>
     *
     * @return Weekday,please see EnWeekDay enumeration.<br>
     *         CN:星期，请参考EnWeekDay枚举类。<br>
     * @see EnWeekDay
     */
    public EnWeekDay getWeekday()
    {
        return mWeekday;
    }

    /**
     * Set weekday.<br>
     * CN:设置星期。<br>
     *
     * @param weekday please see EnWeekDay enumeration.<br>
     *        CN:星期，请参考EnWeekDay枚举类。<br>
     * @see EnWeekDay
     */
    public void setWeekday(EnWeekDay weekday)
    {
        this.mWeekday = weekday;
    }

    /**
     * Obtain start time.<br>
     * CN:获取开始时间。<br>
     *
     * @return Start Time.<br>
     *         CN:开始时间。<br>
     */
    public Date getStartTime()
    {
        return mStartTime;
    }

    /**
     * Set start time.<br>
     * CN:获取开始时间。<br>
     *
     * @param startTime Start Time<br>
     *        CN:开始时间。<br>
     */
    public void setStartTime(Date startTime)
    {
        this.mStartTime = startTime;
    }

    /**
     * Obtain end time.<br>
     * CN:获取结束时间。<br>
     *
     * @return End Time.<br>
     *         CN:结束时间。<br>
     */
    public Date getEndTime()
    {
        return mEndTime;
    }

    /**
     * Set end time.<br>
     * CN:获取结束时间。<br>
     *
     * @param endTime End Time.<br>
     *        CN:结束时间。<br>
     */
    public void setEndTime(Date endTime)
    {
        this.mEndTime = endTime;
    }

    /**
     * Obtain channel.<br>
     * CN:获取频道。<br>
     *
     * @return Channel,please see Channel class.<br>
     *         CN:频道，请参考Channel类。<br>
     * @see Channel
     */
    public Channel getChannel()
    {
        return mChannel;
    }

    /**
     * Set channel.<br>
     * CN:设置频道。<br>
     *
     * @param channel Channel,please see Channel class.<br>
     *        CN:频道，请参考Channel类。<br>
     * @see Channel
     */
    public void setChannel(Channel channel)
    {
        this.mChannel = channel;
    }

    public EN_EVENT_CONTENT_TYPE getContentType() {
        return mContentType;
    }

    public void setContentType(EN_EVENT_CONTENT_TYPE mContentType) {
        this.mContentType = mContentType;
    }
}

