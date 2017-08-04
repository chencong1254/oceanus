package Oceanus.Tv.Service.EpgManager;

import java.util.Date;
import java.util.HashMap;

import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EPG_EVENT_TYPE;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EVENT_CONTENT_TYPE;

/**
 * Created by heji@skyworth.com on 2016/8/22.
 */
public class EpgEvent {
    private EpgManager mEpgManager = null;
    /**
     * channel ID for program event,and -1 as default.<br>
     * CN:节目事件频道的ID，默认为-1。<br>
     */
    private long mChannelId = -1;

    /**
     * EventID for program event,and -1 as default.<br>
     * CN:节目事件的EventID，默认为-1。<br>
     */
    private long mEventId = -1;

    /**
     * FreeCA tag for program event,and FALSE as default.<br>
     * CN:节目事件的加密标记，默认为FALSE。<br>
     */
    private boolean mFreeCA = false;

    /**
     * Parent Lock level for program event,and -1 as default.<br>
     * CN:节目事件的父母锁级别，默认为-1。<br>
     */
    private int mParentLockLevel = -1;

    /**
     * Country code of Parental Rating ,and empty as default.<br>
     * CN:节目事件父母锁级别的国家码，默认为空字符串。<br>
     */
    private String mCountryCode = "";

    /**
     * start time for program event,and current time as default.<br>
     * CN:节目事件开始日期，默认为当前日期。<br>
     */
    private Date mStartTime = new Date();

    /**
     * end time for program event,and current time as default.<br>
     * CN:节目事件结束日期，默认为当前日期。<br>
     */
    private Date mEndTime = new Date();

    /**
     * second value for program event duration,and -1 as default.<br>
     * CN:节目事件时长秒值，默认为-1。<br>
     */
    private long mDuration = -1;

    /**
     * program event name,and empty as default.<br>
     * CN:节目事件名称，默认为空字符串。<br>
     */
    private String mEventName = "";

    /**
     * Program event content level type one.<br>
     * CN:节目分类类型。<br>
     */
    private EN_EVENT_CONTENT_TYPE[] mContentType;

    /**
     * Booked tag for program event.<br>
     * CN:节目事件预订与否标志。<br>
     */
    private boolean isBook = false;
    private String mProgramName = "";
    private String mLongDescription = "";
    private String mShortDescription = "";
    private EN_EPG_EVENT_TYPE mType;
    /**
     * get getShortDescription
     * @return String ShortDescription
     */
    private OtherInfo otherInfo = null;

    public EpgEvent(EN_EPG_EVENT_TYPE type) {
        mType =type;
    }

    public EN_EPG_EVENT_TYPE getType() {
        return mType;
    }

    private class OtherInfo
    {
        private HashMap<String,Object> info;
        protected OtherInfo()
        {
            info = new HashMap<String, Object>();
        }
        public void putValue(String key,Object value)
        {
            info.put(key,value);
        }
        public Object getValue(String key)
        {
            return info.get(key);
        }
    };

    public String getmProgramName() {
        return mProgramName;
    }

    public void setmProgramName(String mProgramName) {
        this.mProgramName = mProgramName;
    }

    public void putOtherInfo(String key, Object value)
    {
        if(this.otherInfo == null)
        {
            this.otherInfo = new OtherInfo();
        }
        this.otherInfo.putValue(key,value);
    }
    public Object getOtherInfo(String key)
    {
        if(this.otherInfo == null)
        {
            return null;
        }
        return this.otherInfo.getValue(key);
    }
    public String getLongDescription()
    {
        return mLongDescription;
    }

    public void setLongDescription(String description)
    {
        this.mLongDescription = description;
    }

    public String getShortDescription()
    {
        return mShortDescription;
    }

    public void setShortDescription(String description)
    {
        this.mShortDescription = description;
    }

    public long getChannelIndex()
    {
        return this.mChannelId;
    }

    public void setChannelIndex(long index)
    {
        this.mChannelId = index;
    }

    public long getEventId()
    {
        return this.mEventId;
    }

    public void setEventId(long eventId)
    {
        this.mEventId = eventId;
    }

    public boolean isFreeCA()
    {
        return this.mFreeCA;
    }

    public void setFreeCA(boolean bIsFree)
    {
        this.mFreeCA = bIsFree;
    }

    public int getParentLockLevel()
    {
        return this.mParentLockLevel;
    }

    public void setParentLockLevel(int level)
    {
        this.mParentLockLevel = level;
    }

    public String getCountryCode()
    {
        return  this.mCountryCode;
    }

    public void setCountryCode(String mCountryCode)
    {
        this.mCountryCode = mCountryCode;
    }

    public Date getEndTime()
    {
        return this.mEndTime;
    }

    public void setEndTime(Date mEndTime)
    {
        this.mEndTime = mEndTime;
    }
    public Date getStartTime()
    {
        return this.mStartTime;
    }

    public void setStartTime(Date mStartTime)
    {
        this.mStartTime = mStartTime;
    }

    public long getDuration()
    {
        return this.mDuration;
    }
    public void setDuration(long duration)
    {
        this.mDuration = duration;
    }

    public String getEventName()
    {
        return this.mEventName;
    }

    public void setEventName(String name)
    {
        this.mEventName = name;
    }

    public EN_EVENT_CONTENT_TYPE[] getContentType()
    {
        return mContentType;
    }

    public void setContentType(EN_EVENT_CONTENT_TYPE[] mContentLevel)
    {
        this.mContentType = mContentLevel;
    }

    public boolean isBooked()
    {
        return this.isBook;
    }
    public void setBooked(Boolean isBook)
    {
        this.isBook = isBook;
    }
}
