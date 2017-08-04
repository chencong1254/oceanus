package Oceanus.Tv.Service.TimeManager.TimeDefinitions;

/**
 * Created by sky057509 on 2017/6/12.
 */

public enum EN_TIME_ZONE {
    E_TIME_ZONE_GTM("GTM",0),
    E_TIME_ZONE_GTM_WEST_1("GTM-01:00",-1),
    E_TIME_ZONE_GTM_WEST_2("GTM-02:00",-2),
    E_TIME_ZONE_GTM_WEST_3("GTM-03:00",-3),
    E_TIME_ZONE_GTM_WEST_4("GTM-04:00",-4),
    E_TIME_ZONE_GTM_WEST_5("GTM-05:00",-5),
    E_TIME_ZONE_GTM_WEST_6("GTM-06:00",-6),
    E_TIME_ZONE_GTM_WEST_7("GTM-07:00",-7),
    E_TIME_ZONE_GTM_WEST_8("GTM-08:00",-8),
    E_TIME_ZONE_GTM_EAST_1("GTM+01:00",+1),
    E_TIME_ZONE_GTM_EAST_2("GTM+02:00",+2),
    E_TIME_ZONE_GTM_EAST_3("GTM+03:00",+3),
    E_TIME_ZONE_GTM_EAST_4("GTM+04:00",+4),
    E_TIME_ZONE_GTM_EAST_5("GTM+05:00",+5),
    E_TIME_ZONE_GTM_EAST_6("GTM+06:00",+6),
    E_TIME_ZONE_GTM_EAST_7("GTM+07:00",+7),
    E_TIME_ZONE_GTM_EAST_8("GTM+08:00",+8);
    private String TimeZoneName = "GTM";
    private int OffsetHour = 0;
    private EN_TIME_ZONE(String TimeZoneName,int OffsetHour)
    {
        this.TimeZoneName = TimeZoneName;
        this.OffsetHour = OffsetHour;
    }
    public String getTimeZoneName()
    {
        return this.TimeZoneName;
    }
    public int getOffsetHour()
    {
        return this.OffsetHour;
    }
}
