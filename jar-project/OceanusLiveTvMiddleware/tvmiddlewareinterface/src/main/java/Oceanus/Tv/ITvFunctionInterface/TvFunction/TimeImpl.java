package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;

import java.util.Date;

import Oceanus.Tv.ITvFunctionInterface.ITime;

/**
 * Created by sky057509 on 2017/6/12.
 */

public class TimeImpl implements ITime {
    private static TimeImpl mObj_This = null;
    public static TimeImpl getInstance()
    {
        if(mObj_This == null)
        {
            mObj_This = new TimeImpl();
        }
        return mObj_This;
    }
    @Override
    public Date getSystemTime() {
        return new Date(System.currentTimeMillis());//获取当前时间;
    }

    @Override
    public Date getTvTime() {
        MtkTvTimeFormatBase time = MtkTvTime.getInstance().getBroadcastTime();
        return new Date(time.toMillis());
    }
}
