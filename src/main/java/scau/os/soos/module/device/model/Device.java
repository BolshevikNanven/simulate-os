package scau.os.soos.module.device.model;

import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.module.process.model.Process;

public class Device {
    private DEVICE_TYPE type;
    private boolean isBusy;
    private Process process;
    private int time;

    private Device() {

    }

    public Device(DEVICE_TYPE deviceType) {
        type = deviceType;
        isBusy = false;
    }

    public DEVICE_TYPE getType() {
        return type;
    }

    public void setType(DEVICE_TYPE type) {
        this.type = type;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
