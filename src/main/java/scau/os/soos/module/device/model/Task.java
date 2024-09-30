package scau.os.soos.module.device.model;

import scau.os.soos.module.process.model.Process;

public class Task {
    private Process process;
    private int time;

    private Task() {

    }

    public Task(Process process, int time) {
        this.process = process;
        this.time = time;
    }

    public Process getProcess() {
        return process;
    }

    public int getTime() {
        return time;
    }
}
