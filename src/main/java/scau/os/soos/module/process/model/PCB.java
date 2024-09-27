package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.PROCESS_STATES;

public class PCB {
    // 进程标识符
    private int pid;
    // 进程状态
    private PROCESS_STATES status;
    // 程序计数器
    private int PC;
    // 进程寄存器AX
    private int AX;
    // 进程实体
    private Process process;

    public PCB() {
        this.pid = -1;
        this.status = PROCESS_STATES.TERMINATED;
        this.PC = -1;
        this.AX = -1;
        this.process = null;
    }

    protected void initPCB(int pid, Process process) {
        setPid(pid);
        setProcess(process);
    }

    private void setPid(int pid) {
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    public PROCESS_STATES getStatus() {
        return status;
    }

    public void setStatus(PROCESS_STATES status) {
        this.status = status;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getAX() {
        return AX;
    }

    public void setAX(int AX) {
        this.AX = AX;
    }

    private void setProcess(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }
    
}
