package Oceanus.Tv.Service.SourceManager;

import java.util.HashMap;

import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

/**
 * Created by heji@skyworth.com on 2016/8/22.
 */
public class Source {
    private EN_INPUT_SOURCE_TYPE type = EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_NUM;
    private String name = null;
    private String old_name = null;
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
    public Source()
    {

    }
    public Source(EN_INPUT_SOURCE_TYPE type, String name)
    {
        this.type = type;
        this.name = name;
        otherInfo = null;
    }
    public EN_INPUT_SOURCE_TYPE getType()
    {
        return this.type;
    }
    public void setType(EN_INPUT_SOURCE_TYPE type)
    {
        this.type = type;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getOldName()
    {
        return this.old_name;
    }
    public void putOtherInfo(String key,Object value)
    {
        if(otherInfo == null)
        {
            this.otherInfo = new OtherInfo();
        }
        this.otherInfo.putValue(key,value);
    }
    public Object getOtherInfo(String key)
    {
        if(otherInfo == null)
        {
            return null;
        }
        return this.otherInfo.getValue(key);
    }

}
