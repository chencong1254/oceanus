package skyworth.skyworthlivetv.osd.common.data;

import java.io.Serializable;

public abstract class TypedData implements Serializable
{
    public enum SkyDataType {
        DATA_TYPE_NONE,
        DATA_TYPE_SINGLE,
        DATA_TYPE_RANGE,
        DATA_TYPE_ENUM,
        DATA_TYPE_SINGLE_SELECT_LIST,
        DATA_TYPE_MULTI_SELECT_LIST,
        DATA_TYPE_INFO,
        DATA_TYPE_SWITCH,
        DATA_TYPE_RET,
    }

    protected String name = null;
    protected SkyDataType type = SkyDataType.DATA_TYPE_RANGE;
    protected String value = null;
    protected boolean enable = true;

    public TypedData(SkyDataType type)
    {
        this.type = type;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public SkyDataType getType()
    {
        return type;
    }
    public void setType(SkyDataType type)
    {
        this.type = type;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public boolean isEnable()
    {
        return enable;
    }
    public void setEnable(boolean enable)
    {
        this.enable = enable;
    }

}
