package com.platform;

import java.util.List;

import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

public interface IPlatformSourceManager {
    List<Source> GetSourceList();
    boolean SetSource(Source source);
    boolean SetSource(EN_INPUT_SOURCE_TYPE type);
    int GetSourceNumber();
    void RefreshSource();
    Source GetCurrentSource();
}
