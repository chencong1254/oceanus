package skyworth.skyworthlivetv.osd.common.data;

public class BooleanData extends TypedData
{
    boolean mSuccess = false;

    public static BooleanData FALSE = new BooleanData(false);
    public static BooleanData TRUE = new BooleanData(true);
    
    public BooleanData(){
        super(SkyDataType.DATA_TYPE_RET);
    }
    
    public BooleanData(boolean success){
        super(SkyDataType.DATA_TYPE_RET);
        this.mSuccess = success;
    }



    public boolean isSuccess()
    {
        return mSuccess;
    }


    public void setSuccess(boolean success)
    {
        this.mSuccess = success;
    }
}
