package Oceanus.Tv.Service.ChannelManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_ANTENNA_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_AUDIO_FORMAT;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_VIDEO_FORMAT;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;

/**
 * Created by heji@skyworth on 2016/8/22.
 */
public class Channel {
    public static final String AtvName = "CH ";
    private class Attr
    {
        private OtherInfo otherInfo = null;
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
    }
    public class AtvAttr extends Attr
    {
        private ATV.EN_SOUND_SYSTEM soundSystem = ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_UNKNOW;
        private ATV.EN_COLOR_SYSTEM colorSystem = ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_UNKNOW;
        private int soundCarrierFreq = -1;
        private ATV.EN_ATV_MTS_MODE mtsMode = ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_AUTO;
        private AtvAttr(){};
        public ATV.EN_COLOR_SYSTEM getColorSystem()
        {
            return this.colorSystem;
        }
        public void setSoundSystem(ATV.EN_SOUND_SYSTEM system)
        {
            this.soundSystem = system;
        }

        public void setColorSystem(ATV.EN_COLOR_SYSTEM colorSystem) {
            this.colorSystem = colorSystem;
        }

        public ATV.EN_ATV_MTS_MODE getMtsMode() {
            return mtsMode;
        }

        public void setMtsMode(ATV.EN_ATV_MTS_MODE mtsMode) {
            this.mtsMode = mtsMode;
        }

        public ATV.EN_SOUND_SYSTEM getSoundSystem()
        {
            return this.soundSystem;
        }
    }
    public class DtvAttr extends Attr
    {
        private boolean bIsScramble = false;
        private EN_VIDEO_FORMAT videoformat = EN_VIDEO_FORMAT.E_VIDEO_FORMAT_UNKNOW;
        private EN_AUDIO_FORMAT audioformat = EN_AUDIO_FORMAT.EN_AUDIO_FORMAT_UNKNOW;
        private boolean rev_3 = false;
        private DtvAttr(){};
        public void setbIsScramble(boolean bIsScramble)
        {
            this.bIsScramble = bIsScramble;
        }
        public boolean getbIsScramble()
        {
            return this.bIsScramble;
        }

        public EN_AUDIO_FORMAT getAudioformat() {
            return audioformat;
        }

        public EN_VIDEO_FORMAT getVideoformat() {
            return videoformat;
        }

        public void setAudioformat(EN_AUDIO_FORMAT audioformat) {
            this.audioformat = audioformat;
        }

        public void setVideoformat(EN_VIDEO_FORMAT videoformat) {
            this.videoformat = videoformat;
        }
    }
    private String name = null;
    private int channelNumber = 0;
    private int logicNumber = 0;
    private int dbIndex = 0;
    private int freq = 0;
    private boolean bIsLock = false;
    private boolean bIsSkip = false;
    private boolean bIsDelete = false;
    private boolean bIsFav = false;
    private EN_CHANNEL_SERVICE_TYPE type = EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP;
    private EN_ANTENNA_TYPE antennaType = EN_ANTENNA_TYPE.E_ANTENNA_TYPE_NONE;
    private Attr attr = null;
    public Channel(EN_CHANNEL_SERVICE_TYPE type)
    {
        if(type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
        {
            attr = new AtvAttr();
        }
        else
        {
            attr = new DtvAttr();
        }
        this.type = type;
    }
    public Channel(JSONObject jsonChannelObj)
    {
        if(jsonChannelObj != null)
        {
            try {
                this.name = jsonChannelObj.getString("name");
                this.dbIndex = jsonChannelObj.getInt("dbIndex");
                this.channelNumber = jsonChannelObj.getInt("channelNumber");
                this.freq = jsonChannelObj.getInt("freq");
                this.type = EN_CHANNEL_SERVICE_TYPE.values()[jsonChannelObj.getInt("type")];
                this.bIsDelete = jsonChannelObj.getBoolean("isDelete");
                this.bIsFav = jsonChannelObj.getBoolean("isFav");
                this.bIsLock = jsonChannelObj.getBoolean("isLock");
                this.bIsSkip = jsonChannelObj.getBoolean("isSkip");
                this.logicNumber = jsonChannelObj.getInt("logicNumber");
                if(this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
                {
                    attr = new AtvAttr();
                    ((AtvAttr)attr).colorSystem = ATV.EN_COLOR_SYSTEM.values()[jsonChannelObj.getInt("coloreSystem")];
                    ((AtvAttr)attr).soundSystem = ATV.EN_SOUND_SYSTEM.values()[jsonChannelObj.getInt("soundSystem")];
                    ((AtvAttr)attr).soundCarrierFreq = jsonChannelObj.getInt("soundCarrierFreq");
                    ((AtvAttr)attr).mtsMode = ATV.EN_ATV_MTS_MODE.values()[jsonChannelObj.getInt("mtsMode")];
                }
                else if(this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_ATSC||
                        this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DTMB||
                        this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBC||
                        this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBS||
                        this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBT||
                        this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_ISDB||
                        this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
                {
                    attr = new DtvAttr();
                    ((DtvAttr)attr).bIsScramble = jsonChannelObj.getBoolean("bIsScramble");
                    ((DtvAttr)attr).videoformat = EN_VIDEO_FORMAT.values()[jsonChannelObj.getInt("videoformat")];
                    ((DtvAttr)attr).audioformat = EN_AUDIO_FORMAT.values()[jsonChannelObj.getInt("audioformat")];
                    ((DtvAttr)attr).rev_3 = jsonChannelObj.getBoolean("rev_3");
                }
                this.antennaType = EN_ANTENNA_TYPE.values()[jsonChannelObj.getInt("antennaType")];
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    protected String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name",this.name);
            jsonObject.put("dbIndex",this.dbIndex);
            jsonObject.put("channelNumber",this.channelNumber);
            jsonObject.put("freq",this.freq);
            jsonObject.put("type",this.type.ordinal());
            jsonObject.put("isDelete",this.bIsDelete);
            jsonObject.put("isFav",this.bIsFav);
            jsonObject.put("isLock",this.bIsLock);
            jsonObject.put("isSkip",this.bIsSkip);
            jsonObject.put("logicNumber",this.logicNumber);
            if(this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
            {
                AtvAttr atv_attr = (AtvAttr) this.attr;
                jsonObject.put("coloreSystem",atv_attr.colorSystem.ordinal());
                jsonObject.put("soundSystem",atv_attr.soundSystem.ordinal());
                jsonObject.put("soundCarrierFreq",atv_attr.soundCarrierFreq);
                jsonObject.put("mtsMode",atv_attr.mtsMode.ordinal());
            }
            else if(this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_ATSC||
                    this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DTMB||
                    this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBC||
                    this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBS||
                    this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBT||
                    this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_ISDB||
                    this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
            {
                DtvAttr dtv_attr = (DtvAttr)this.attr;
                jsonObject.put("isScramble",dtv_attr.bIsScramble);
                jsonObject.put("videoformat",dtv_attr.videoformat.ordinal());
                jsonObject.put("audioformat",dtv_attr.audioformat.ordinal());
                jsonObject.put("rev_3",dtv_attr.rev_3);
            }
            jsonObject.put("antennaType",this.antennaType.ordinal());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
    public void setLogicNumber(int logicNumber)
    {
        this.logicNumber = logicNumber;
    }
    public int getLogicNumber()
    {
        return this.logicNumber;
    }
    public void setChannelNumber(int channelNumber)
    {
        this.channelNumber = channelNumber;
    }
    public int getChannelNumber()
    {
        return this.channelNumber;
    }
    public void setType(EN_CHANNEL_SERVICE_TYPE type)
    {
        this.type = type;
    }
    public EN_CHANNEL_SERVICE_TYPE getType()
    {
        return this.type;
    }
    public void setDbIndex(int index)
    {
        this.dbIndex = index;
    }
    public int getDbIndex()
    {
        return this.dbIndex;
    }
    public void setFreq(int freq)
    {
        this.freq = freq;
    }
    public int getFreq()
    {
        return this.freq;
    }
    public void setIsLock(boolean isLock)
    {
        this.bIsLock = isLock;
    }
    public boolean getIsLock()
    {
        return this.bIsLock;
    }
    public void setIsSkip(boolean isSkip)
    {
        this.bIsSkip = isSkip;
    }
    public boolean getIsSkip()
    {
        return this.bIsSkip;
    }
    public void setIsDelete(boolean bIsDelete)
    {
        this.bIsDelete = bIsDelete;
    }
    public boolean getIsDelete()
    {
        return this.bIsDelete;
    }
    public void setIsFav(boolean bIsFav)
    {
        this.bIsFav = bIsFav;
    }
    public boolean getIsFav()
    {
        return this.bIsFav;
    }
    public void setAntennaType(EN_ANTENNA_TYPE type)
    {
        this.antennaType = type;
    }
    public EN_ANTENNA_TYPE getAntennaType()
    {
        return this.antennaType;
    }
    public AtvAttr getAtvAttr()
    {
        if(this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
        {
            return (AtvAttr) attr;
        }
        return null;
    }
    public DtvAttr getDtvAttr()
    {
        if(this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_ATSC||
                this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DTMB||
                this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBC||
                this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBS||
                this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBT||
                this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_ISDB||
                this.type == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP) {
            return (DtvAttr) attr;
        }
            return null;
    }
}
