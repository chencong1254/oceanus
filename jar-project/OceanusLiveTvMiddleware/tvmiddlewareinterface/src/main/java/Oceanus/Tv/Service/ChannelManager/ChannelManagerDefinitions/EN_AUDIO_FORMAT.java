package Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions;

/**
 * Created by sky057509 on 2017/5/9.
 */

public enum EN_AUDIO_FORMAT {
    EN_AUDIO_FORMAT_AC3("AC3"),
    EN_AUDIO_FORMAT_HEAAC("HEAAC"),
    EN_AUDIO_FORMAT_UNKNOW("");
    private String name = "";
    private EN_AUDIO_FORMAT(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
}
