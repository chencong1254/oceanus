package Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions;

/**
 * Created by sky057509 on 2017/5/9.
 */

public enum  EN_VIDEO_FORMAT {
    E_VIDEO_FORMAT_4k("2160P"),
    E_VIDEO_FORMAT_1080P("1080P"),
    E_VIDEO_FORMAT_1080I("1080I"),
    E_VIDEO_FORMAT_720P("720P"),
    E_VIDEO_FORMAT_480P("480P"),
    E_VIDEO_FORMAT_480I("480I"),
    E_VIDEO_FORMAT_576I("576I"),
    E_VIDEO_FORMAT_576P("576P"),
    E_VIDEO_FORMAT_360P("360P"),
    E_VIDEO_FORMAT_240P("240P"),
    E_VIDEO_FORMAT_UNKNOW("");
    private String name = "";
    private EN_VIDEO_FORMAT(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
}
