package Oceanus.Tv.Service.SatelliteManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.SatelliteManager.SatelliteManagerDefinitions.EN_POLARIZATION_TYPE;

/**
 * Created by sky057509 on 2017/5/4.
 */

public class Transponder {
    private int SatelliteId = 0;
    private int Id = 0;
    private int Symble = 0;
    private int Freq = 0;
    private EN_POLARIZATION_TYPE PolarizationType = EN_POLARIZATION_TYPE.E_POLARIZATION_VERTICAL;
    public Transponder(int satelliteId,int symble,int freq,EN_POLARIZATION_TYPE polarization_type)
    {
        this.Id = -1;
        this.Freq = freq;
        this.PolarizationType = polarization_type;
        this.Symble = symble;
        this.SatelliteId = satelliteId;
    }
    public Transponder(JSONObject jsonObject)
    {
        try {
            this.Freq = jsonObject.getInt("Freq");
            this.SatelliteId = jsonObject.getInt("SatelliteId");
            this.Id = jsonObject.getInt("Id");
            this.PolarizationType = EN_POLARIZATION_TYPE.values()[jsonObject.getInt("PolarizationType")];
            this.Symble = jsonObject.getInt("symble");
            } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String toJsonString()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id",this.Id);
            jsonObject.put("Symble",this.Symble);
            jsonObject.put("SatelliteId",this.SatelliteId);
            jsonObject.put("Freq",this.Freq);
            jsonObject.put("PolarizationType",this.PolarizationType.ordinal());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject.toString();
    }
    public int getSatelliteId()
    {
        return this.SatelliteId;
    }
    public int getId()
    {
        return this.Id;
    }
    public int getSymble()
    {
        return this.Symble;
    }
    public int getFreq()
    {
        return this.Freq;
    }
}
