package Oceanus.Tv.Service.SatelliteManager;

import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.Service.SatelliteManager.SatelliteManagerDefinitions.EN_22_KHZ_TYPE;
import Oceanus.Tv.Service.SatelliteManager.SatelliteManagerDefinitions.EN_DISEQ_1_0_TYPE;
import Oceanus.Tv.Service.SatelliteManager.SatelliteManagerDefinitions.EN_DISEQ_1_1_TYPE;
import Oceanus.Tv.Service.SatelliteManager.SatelliteManagerDefinitions.EN_LNB_POWER_TYPE;
import Oceanus.Tv.Service.SatelliteManager.SatelliteManagerDefinitions.EN_LNB_TYPE;

/**
 * Created by sky057509 on 2017/5/4.
 */

public class Satellite {
    MtkTvDvbsConfigInfoBase mtkTvDvbsConfigInfoBase = null;
    private String Name;
    private int SatelliteId;
    private EN_LNB_POWER_TYPE LnbPowerType = EN_LNB_POWER_TYPE.E_LNB_POWER_OFF;
    private EN_LNB_TYPE LnbType = EN_LNB_TYPE.E_LNB_TYPE_UNIVERSAL;
    private List<Transponder> TransponderList = null;
    private Transponder DefaultTransponder = null;
    private EN_22_KHZ_TYPE En22KhzType = EN_22_KHZ_TYPE.E_22_KHZ_AUTO;
    private EN_DISEQ_1_0_TYPE EnDiseq10Type = EN_DISEQ_1_0_TYPE.E_DISEQ_1_0_TYPE_NONE;
    private EN_DISEQ_1_1_TYPE EnDiseq11Type = EN_DISEQ_1_1_TYPE.E_DISEQ_1_1_TYPE_NONE;
    public Satellite(String name,int satelliteId)
    {
        this.Name = name;
        this.SatelliteId = satelliteId;
    }
    public Satellite(String jsonString) throws JSONException {
        if(TransponderList == null)
        {
            TransponderList = new ArrayList<>();
            TransponderList.clear();
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject jsonObjectLnbType = jsonObject.getJSONObject("LnbType");
        LnbType = EN_LNB_TYPE.values()[jsonObjectLnbType.getInt("ordinal")];
        if(LnbType == EN_LNB_TYPE.E_LNB_TYPE_USER)
        {
            LnbType.seteLnbType(jsonObjectLnbType.getInt("LnbLowFreq"),jsonObjectLnbType.getInt("LnbStrongFrq"));
        }
        this.Name = jsonObject.getString("Name");
        this.SatelliteId = jsonObject.getInt("SatelliteId");
        this.LnbPowerType = EN_LNB_POWER_TYPE.values()[jsonObject.getInt("LnbPowerType")];
        this.En22KhzType = EN_22_KHZ_TYPE.values()[jsonObject.getInt("En22KhzType")];
        this.EnDiseq10Type = EN_DISEQ_1_0_TYPE.values()[jsonObject.getInt("EnDiseq10Type")];
        this.EnDiseq11Type = EN_DISEQ_1_1_TYPE.values()[jsonObject.getInt("EnDiseq11Type")];
        JSONArray jsonObjectTransponderList = jsonObject.getJSONArray("TransponderList");
        for(int i=0;i<jsonObjectTransponderList.length();i++)
        {
            JSONObject jsonObjectTransponder = jsonObjectTransponderList.getJSONObject(i);
            TransponderList.add(new Transponder(jsonObjectTransponder));
        }

    }
    public void setName(String name)
    {
        this.Name = name;
    }
    public String getName()
    {
        return this.Name;
    }
    public void setSatelliteId(int id)
    {
        this.SatelliteId = id;
    }
    public int getSatellitedId()
    {
        return this.SatelliteId;
    }
    public void setLnbPowerType(EN_LNB_POWER_TYPE type)
    {
        this.LnbPowerType = type;
    }
    public EN_LNB_POWER_TYPE getLnbPowerType()
    {
        return this.LnbPowerType;
    }
    public void addTransponder(Transponder transponder)
    {
        if(this.TransponderList == null)
        {
            this.TransponderList = new ArrayList<>();
        }
        this.TransponderList.add(transponder);
    }
    public void setDefaultTransponder(Transponder transponder)
    {
        this.DefaultTransponder = transponder;
    }
    public List<Transponder> getTransponderList()
    {
        return this.TransponderList;
    }
    public Transponder getDefaultTransponder()
    {
        return this.DefaultTransponder;
    }
    public void setEn22KhzType(EN_22_KHZ_TYPE type)
    {
        this.En22KhzType = type;
    }
    public EN_22_KHZ_TYPE getEn22KhzType()
    {
        return this.En22KhzType;
    }
    public void setEnDiseq10Type(EN_DISEQ_1_0_TYPE type)
    {
        this.EnDiseq10Type = type;
    }
    public EN_DISEQ_1_0_TYPE getEnDiseq10Type()
    {
        return this.EnDiseq10Type;
    }
    public void setEnDiseq11Type(EN_DISEQ_1_1_TYPE type)
    {
        this.EnDiseq11Type = type;
    }
    public EN_DISEQ_1_1_TYPE getEnDiseq11Type()
    {
        return this.EnDiseq11Type;
    }
    public void setLnbType(EN_LNB_TYPE type)
    {
        this.LnbType = type;
    }
    public EN_LNB_TYPE getLnbType()
    {
        return this.LnbType;
    }

}
