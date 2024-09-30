package scau.os.soos.module.process.model;

public class Process {
    // 进程控制块
    private PCB pcb;

    public Process(PCB pcb, int pid) {
        this.pcb = pcb;
        this.pcb.initPCB(pid,this);
    }

    public PCB getPCB() {
        return pcb;
    }

}
