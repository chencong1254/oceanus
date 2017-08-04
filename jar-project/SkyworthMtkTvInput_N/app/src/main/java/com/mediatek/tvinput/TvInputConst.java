
package com.mediatek.tvinput;

import java.util.HashMap;
import java.util.Map;

import android.media.tv.TvInputHardwareInfo;

/**
 * Some static value here
 */
public class TvInputConst {
  private static final Map<Integer, String> inputDescription = new HashMap<Integer, String>();
  public static boolean DEBUG = true;

  public static int InputInvalid = -1;
  public static int InputMain = 0;
  public static int InputSub = 1;
  public static int InputMax = 2;

  /**
   * MTK defined input type, these type not defined by Google in class TVInputDeviceInfo
   */
  public static int TV_INPUT_TYPE_HDMI = TvInputHardwareInfo.TV_INPUT_TYPE_HDMI;
  public static int TV_INPUT_TYPE_BUILD_IN_TUNER = TvInputHardwareInfo.TV_INPUT_TYPE_TUNER;//
  public static int TV_INPUT_TYPE_S_VIDEO = TvInputHardwareInfo.TV_INPUT_TYPE_SVIDEO;
  public static int TV_INPUT_TYPE_SCART = TvInputHardwareInfo.TV_INPUT_TYPE_SCART;
  public static int TV_INPUT_TYPE_COMPOSITE = TvInputHardwareInfo.TV_INPUT_TYPE_COMPOSITE;
  public static int TV_INPUT_TYPE_COMPONENT = TvInputHardwareInfo.TV_INPUT_TYPE_COMPONENT;
  public static int TV_INPUT_TYPE_VGA = TvInputHardwareInfo.TV_INPUT_TYPE_VGA;
  public static int TV_INPUT_TYPE_DVI = TvInputHardwareInfo.TV_INPUT_TYPE_DVI;

  static {
    inputDescription.put(TV_INPUT_TYPE_HDMI, "HDMI");
    inputDescription.put(TV_INPUT_TYPE_BUILD_IN_TUNER, "TV");
    inputDescription.put(TV_INPUT_TYPE_S_VIDEO, "S_VIDEO");
    inputDescription.put(TV_INPUT_TYPE_SCART, "SCART");
    inputDescription.put(TV_INPUT_TYPE_COMPOSITE, "COMPOSITE ");
    inputDescription.put(TV_INPUT_TYPE_COMPONENT, "COMPONENT");
    inputDescription.put(TV_INPUT_TYPE_VGA, "VGA");
    inputDescription.put(TV_INPUT_TYPE_DVI, "DVI");
  }

  public static String getInputName(int type) {
    return "[" + inputDescription.get(type) + "]";
  }
}
