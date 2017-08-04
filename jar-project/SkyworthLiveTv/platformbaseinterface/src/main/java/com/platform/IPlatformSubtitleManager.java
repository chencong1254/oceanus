package com.platform;

import java.util.List;

/**
 * Created by sky057509 on 2017/5/26.
 */

public interface IPlatformSubtitleManager {
    public boolean EnableSubtitle(boolean bEnable);

    public boolean SelectSubtitle(int index);

    public List<String> GetSubtitleList();

    public String GetSubtitleInfoById(int id);

    public boolean IsSubtitleExist();

    public boolean IsSubtitleEnable();

    public int GetCurrentSubtitleIndex();
}
