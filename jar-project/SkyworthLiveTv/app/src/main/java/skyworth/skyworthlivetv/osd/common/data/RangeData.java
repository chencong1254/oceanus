package skyworth.skyworthlivetv.osd.common.data;

public class RangeData extends TypedData
{
    private int max = 0;
    private int min = 0;
    private int current = 0;

    public RangeData()
    {
        super(SkyDataType.DATA_TYPE_RANGE);
    }

    public int getMax()
    {
        return max;
    }

    public void setMax(int max)
    {
        this.max = max;
    }

    public int getMin()
    {
        return min;
    }

    public void setMin(int min)
    {
        this.min = min;
    }

    public int getCurrent()
    {
        return current;
    }

    public void setCurrent(int current)
    {
        this.current = current;
    }
}
