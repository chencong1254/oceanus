package Oceanus.Tv.ITvFunctionInterface;

import java.util.List;

/**
 * Created by sky057509 on 2017/3/10.
 */
public interface ISubtitle {
    public abstract boolean EnableSubtitle(boolean bEnable);
    public abstract boolean SelectSubtitle(int index);
    public abstract List<String> GetSubtitleList();
    public abstract boolean IsSubtitleExist();
}
