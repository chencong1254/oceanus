package Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions;

/**
 * Created by sky057509 on 2016/8/19.
 */

public enum EN_CHANNEL_SERVICE_TYPE
{
    E_SERVICE_ATV("ATV"),
    E_SERVICE_DTV_ATSC("ATSC"),
    E_SERVICE_DTV_DTMB("DTMB"),
    E_SERVICE_DTV_DVBC("DVB-C"),
    E_SERVICE_DTV_DVBT("DVB-T"),
    E_SERVICE_DTV_DVBS("DVB-S"),
    E_SERVICE_DTV_ISDB("ISDB"),
    E_SERVICE_OTHER_APP("APP");
    private String name = "";
    private EN_CHANNEL_SERVICE_TYPE(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }
};
