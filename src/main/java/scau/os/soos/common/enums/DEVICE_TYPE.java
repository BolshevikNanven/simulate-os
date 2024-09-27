package scau.os.soos.common.enums;

public enum DEVICE_TYPE {
    A,
    B,
    C;

    public static DEVICE_TYPE ordinalToDeviceType(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal");
        }
        return values()[ordinal];
    }

}
