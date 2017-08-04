package Oceanus.Tv.Service.SatelliteManager.SatelliteManagerDefinitions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sky057509 on 2017/5/4.
 */

public enum EN_LNB_TYPE {
    E_LNB_TYPE_UNIVERSAL(0,0),
    EN_LNB_TYPE_5150_MHZ(5150,5150),
    EN_LNB_TYPE_5750_MHZ(5750,5750),
    EN_LNB_TYPE_9750_MHZ(9750,9750),
    EN_LNB_TYPE_10600_MHZ(10600,10600),
    EN_LNB_TYPE_10750_MHZ(10750,10750),
    EN_LNB_TYPE_11250_MHZ(11250,11250),
    EN_LNB_TYPE_11300_MHZ(11300,11300),
    EN_LNB_TYPE_11475_MHZ(11475,11475),
    EN_LNB_TYPE_5150_5750_MHZ(5150,5750),
    EN_LNB_TYPE_9750_10750_MHZ(9750,10750),
    EN_LNB_TYPE_10000_10450_MHZ(10000,10450),
    E_LNB_TYPE_USER(9750,10600);
    private int LnbLowFreq;
    private int LnbStrongFrq;
    EN_LNB_TYPE(int lnbLowFreq,int lnbStrongFrq)
    {
        this.LnbLowFreq = lnbLowFreq;
        this.LnbStrongFrq = lnbStrongFrq;
    }
    public int getLnbLowFreq()
    {
        return this.LnbLowFreq;
    }
    public int getLnbStrongFrq()
    {
        return this.LnbStrongFrq;
    }
    public void seteLnbType(int lnbLowFreq,int lnbStrongFrq)
    {
        if(this == E_LNB_TYPE_USER)
        {
            this.LnbLowFreq = lnbLowFreq;
            this.LnbStrongFrq = lnbStrongFrq;
        }
    }
    public String toJsonString()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ordinal",ordinal());
            jsonObject.put("LnbLowFreq",this.LnbLowFreq);
            jsonObject.put("LnbStrongFrq",this.LnbStrongFrq);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject.toString();
    }
}
