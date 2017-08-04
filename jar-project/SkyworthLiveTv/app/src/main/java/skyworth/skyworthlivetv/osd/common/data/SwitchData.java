package skyworth.skyworthlivetv.osd.common.data;

public class SwitchData extends TypedData
{
    private boolean isOn = false;
    private String  onStr ="On";
    private String  offStr ="Off";

    public SwitchData()
    {
        super(TypedData.SkyDataType.DATA_TYPE_SWITCH);
    }

    public boolean isOn()
    {
        return isOn;
    }

    public void setOn(boolean isOn)
    {
        this.isOn = isOn;
    }
    public void setOnStr(String onStr)
    {
        this.onStr = onStr;
    }
    public void setOffStr(String offStr)
    {
        this.offStr = offStr;
    }
    public String getCurrentStr()
    {
        if(isOn)
            return onStr;
        else
            return offStr;
    }
}