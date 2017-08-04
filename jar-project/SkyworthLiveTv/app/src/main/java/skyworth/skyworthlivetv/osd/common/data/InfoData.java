package skyworth.skyworthlivetv.osd.common.data;

public class InfoData extends TypedData
{

    public InfoData()
    {
        super(TypedData.SkyDataType.DATA_TYPE_INFO);
    }
    
    private String current = null;

    public String getCurrent()
    {
        return current;
    }

    public void setCurrent(String current)
    {
        this.current = current;
    }
}
