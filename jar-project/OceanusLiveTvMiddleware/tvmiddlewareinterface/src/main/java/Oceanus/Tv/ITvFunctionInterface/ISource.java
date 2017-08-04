package Oceanus.Tv.ITvFunctionInterface;

import java.util.List;

import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

/**
 * Created by sky057509 on 2016/12/9.
 */
public interface ISource {
    public abstract  List<Source> getSourceList();
    public abstract boolean setSource(Source source);
    public abstract Source getCurrentSource();
    public abstract boolean blockSource(EN_INPUT_SOURCE_TYPE source);
    public abstract int getSourceNumber();
}
