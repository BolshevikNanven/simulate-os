package scau.os.soos.common;

import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.common.model.Clock;

/**
 * 系统全局变量
 */
public class OS {
    public static volatile int PSW = 0b000;
    public static Clock clock = new Clock();

    // 设置PSW中的中断标志
    public static void setInterrupt(INTERRUPT interruptType) {
        PSW |= (1 << interruptType.ordinal());
    }

    // 清除PSW中的中断标志
    public static void clearInterrupt(INTERRUPT interruptType) {
        PSW &= ~(1 << interruptType.ordinal());
    }
}
