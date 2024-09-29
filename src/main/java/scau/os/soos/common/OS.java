package scau.os.soos.common;

import scau.os.soos.common.enums.OS_STATES;
import scau.os.soos.common.model.Clock;

/**
 * 模拟硬件全局变量
 */
public class OS {
    public static Clock clock = new Clock();
    public static OS_STATES state = OS_STATES.STOPPED;
}
