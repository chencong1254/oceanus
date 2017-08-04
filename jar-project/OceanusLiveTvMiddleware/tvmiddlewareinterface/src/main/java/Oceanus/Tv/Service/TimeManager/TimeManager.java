package Oceanus.Tv.Service.TimeManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.TimeImpl;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EnWeekDay;
import Oceanus.Tv.Service.TimeManager.TimeDefinitions.EN_TIME_ZONE;

/**
 * Created by sky057509 on 2017/6/12.
 */

public class TimeManager  {
    private TimeImpl m_ObjTimeImpl = null;
    private boolean b_isDst = false;
    private static TimeManager m_pThis = null;
    public TimeManager()
    {
        m_ObjTimeImpl = TimeImpl.getInstance();
        m_pThis = this;
    }
    static public TimeManager getInstance()
    {
        if(m_pThis==null)
        {
            new TimeManager();
        }
        return m_pThis;
    }
    public Date getTvTime()
    {
        return m_ObjTimeImpl.getTvTime();
    }
    public Date getSystemTime()
    {
        return m_ObjTimeImpl.getSystemTime();
    }
    public void setDayLightSavingTime(boolean on)
    {
        b_isDst = on;
    }
    public EN_TIME_ZONE getCurrentTimeZone()
    {
        TimeZone timeZone = TimeZone.getDefault();
        int offsetHour = timeZone.getRawOffset()/3600000;
        for(EN_TIME_ZONE timezone:EN_TIME_ZONE.values())
        {
            if(timezone.getOffsetHour() == offsetHour)
            {
                return timezone;
            }
        }
        return EN_TIME_ZONE.E_TIME_ZONE_GTM;
    }
    static public String getFormatterTimeStringByHHmmMMDD(Date date)
    {
        if(date!=null)
        {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm MM/dd");
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(date);
        }
        return "";
    }
    static public String getFormatterTimeStringByHHmmMMDDYY(Date date)
    {
        if(date!=null)
        {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm MM/dd/yyyy");
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(date);
        }
        return "";
    }
    static public String getFormatterTimeStringByHHmm(Date date)
    {
        if(date!=null)
        {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            return formatter.format(date);
        }
        return "";
    }
    static public EnWeekDay getWeek(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getDefault());
        return EnWeekDay.values()[calendar.get(Calendar.WEEK_OF_MONTH)];
        /*
        switch (EnWeekDay.values()[calendar.get(Calendar.WEEK_OF_MONTH)])
        {
            case SUNDAY:return GlobalApplication.getAppInstance().getString(R.string.Sunday);
            case MONDAY:return GlobalApplication.getAppInstance().getString(R.string.Monday);
            case TUESDAY:return GlobalApplication.getAppInstance().getString(R.string.Tuesday);
            case WEDNESDAY:return GlobalApplication.getAppInstance().getString(R.string.Wednesday);
            case THURSDAY:return GlobalApplication.getAppInstance().getString(R.string.Tuesday);
            case FRIDAY:return GlobalApplication.getAppInstance().getString(R.string.Friday);
            case SATURDAY:return GlobalApplication.getAppInstance().getString(R.string.Saturday);
            default:break;
        }
        return "";
        */
    }
}
