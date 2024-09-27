package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.BLOCK_CAUSE;
import scau.os.soos.common.enums.PROCESS_STATES;

public class PCB {
    // PCB实例数量跟踪，用于生成pid
    private static int pidCount = 0;
    // 进程标识符
    private final int pid;
    // 进程状态
    private PROCESS_STATES status;
    // 程序计数器
    private int PC;
    // 程序状态字
    private int PSW;
    // 进程阻塞原因
    private BLOCK_CAUSE blockCause;
    // 进程当前内存地址
    private int memoryAddress;

    public PCB() {
        this.pid = pidCount++;
        this.status = PROCESS_STATES.NEW;
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

    public int getPSW() {
        return PSW;
    }

    public void setPSW(int PSW) {
        this.PSW = PSW;
    }

    public BLOCK_CAUSE getBlockCause() {
        return blockCause;
    }

    public void setBlockCause(BLOCK_CAUSE blockCause) {
        this.blockCause = blockCause;
    }

    public int getMemoryAddress() {
        return memoryAddress;
    }

    public void setMemoryAddress(int memoryAddress) {
        this.memoryAddress = memoryAddress;
    }
}
