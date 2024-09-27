package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.BLOCK_CAUSE;
import scau.os.soos.common.enums.PROCESS_STATES;

public class PCB {
    // 进程标识符
    private int pid;
    // 进程状态
    private PROCESS_STATES status;
    // 程序计数器
    private int PC;
    // 程序状态字
    private int PSW;
    // 进程阻塞原因
    private BLOCK_CAUSE blockCause;

    private int AX;

    public PCB() {
        this.pid = -1;
        this.status = PROCESS_STATES.TERMINATED;
        this.PC = -1;
        this.PSW = -1;
        this.blockCause = BLOCK_CAUSE.CPU_BUSY;
    }

    protected void initPid(int pid){
        setPid(pid);
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

    public BLOCK_CAUSE getBlockCause() {
        return blockCause;
    }

    public void setBlockCause(BLOCK_CAUSE blockCause) {
        this.blockCause = blockCause;
    }


    public int getAX() {
        return AX;
    }

    public void setAX(int AX) {
        this.AX = AX;
    }
}
