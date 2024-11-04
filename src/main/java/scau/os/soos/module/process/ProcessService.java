package scau.os.soos.module.process;

import scau.os.soos.common.enums.CPU_STATES;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.common.enums.PROCESS_STATES;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.memory.MemoryController;
import scau.os.soos.module.process.model.BlockingQueue;
import scau.os.soos.module.process.model.EmptyPCBQueue;
import scau.os.soos.module.process.model.PCB;
import scau.os.soos.module.process.model.ReadyQueue;
import scau.os.soos.module.process.model.Process;

public class ProcessService {
    // 进程最大数量
    private static final int MAX_PROCESS_COUNT = 10;
    // 进程最大时间片
    private final int MAX_CLOCK = 6;
    // 进程实例数量跟踪，用于生成pid
    private int processCount = 1;
    // 空白PCB队列，用于生成进程
    private final EmptyPCBQueue emptyPCBQueue;
    // 就绪队列
    private final ReadyQueue readyQueue;
    // 阻塞队列
    private final BlockingQueue blockingQueue;
    // 时间片
    private int currentProcessTimeSlice = 0;

    public ProcessService() {
        this.emptyPCBQueue = new EmptyPCBQueue(MAX_PROCESS_COUNT);
        this.readyQueue = new ReadyQueue();
        this.blockingQueue = new BlockingQueue();
    }

    // 新建 -> 就绪
    public synchronized Process create(Item file) {
        if (!(file instanceof Exe)) {
            System.out.println("-------Process-------文件为空 或 非可执行程序");
            return null;
        }

        // 1.申请空白进程控制块
        PCB newPCB = emptyPCBQueue.applyEmptyPCB();
        if (newPCB == null) {
            System.out.println("-------Process-------进程控制块申请失败");
            return null;
        }

        // 2.查询文件大小
        int fileSize = FileController.getInstance().getFileSize(file);

        // 3.申请内存空间,装入内存系统区
        if (MemoryController.getInstance().allocate(newPCB, fileSize)) {
            System.out.println("-------Process-------进程内存申请失败");
            return null;
        }

        // 4.初始化进程控制块
        Process process = new Process(newPCB, processCount++);
        // 设置进程为新建态
        process.getPCB().setStatus(PROCESS_STATES.NEW);

        // 5.将文件装入内存用户区
        // 获取文件数据
        byte[] instructions = FileController.getInstance().readFile(file);
        int address = newPCB.getPC();
        // 写入内存用户区
        for (byte instruction :instructions){
            MemoryController.getInstance().write(address, instruction);
            address++;
        }


        // 6.将进程放入就绪队列
        // 设置进程为就绪态度
        process.getPCB().setStatus(PROCESS_STATES.READY);
        readyQueue.offerPCB(process);

        System.out.println("-------Process-------进程创建成功");
        return process;
    }

    // 就绪 -> 运行
    public synchronized boolean schedule() {
        System.out.println("-------Process-------调度新进程");

        // 1.判断CPU是否空闲
        if (CpuController.getInstance().getCpuState() == CPU_STATES.BUSY) {
            System.out.println("-------Process-------CPU繁忙");
            return false;
        }

        // 2.从就绪队列中选择一个新进程
        PCB pcb = readyQueue.pollPCB();
        if(pcb==null){
            System.out.println("-------Process-------就绪队列为空");
            return false;
        }
        Process process = readyQueue.pollPCB().getProcess();

        // 3.调度新进程上处理机
        if (!CpuController.getInstance().handleProcess(process)) {
            System.out.println("-------Process-------恢复现场失败");
            // 将进程放入就绪队列
            readyQueue.offerPCB(process);
            return false;
        }

        // 4.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.RUNNING);
        // 为进程分配时间片
        resetTimeSlice();
        System.out.println("-------Process-------进程调度成功");
        return true;
    }

    // 运行 -> 退出
    public synchronized boolean destroy(Process process) {
        // 1.判断当前进程是否为空
        if (process == null) {
            System.out.println("-------Process-------当前进程为空");
            return false;
        }

        // 2.回收进程内存
        if (!MemoryController.getInstance().free(process.getPCB())) {
            System.out.println("-------Process-------回收进程内存失败");
            return false;
        }

        // 3.回收进程控制块
        // 设置进程为终止态度
        process.getPCB().setStatus(PROCESS_STATES.TERMINATED);
        if (!emptyPCBQueue.recycleEmptyPCB()) {
            System.out.println("-------Process-------回收进程控制块失败");
            return false;
        }
        System.out.println("-------Process-------进程销毁成功");
        return true;
    }

    // 阻塞 -> 就绪
    public synchronized boolean wake(Process process) {
        // 1.判断当前进程是否为空
        if (process == null) {
            System.out.println("-------Process-------当前进程为空");
            return false;
        }

        // 2.判断阻塞队列是否为空
        if (blockingQueue.isEmpty()) {
            System.out.println("-------Process-------阻塞队列为空");
            return false;
        }

        // 3.将进程由阻塞队列中摘下
        if (!blockingQueue.removePCB(process.getPCB())) {
            System.out.println("-------Process-------阻塞队列中不存在该进程");
            return false;
        }

        // 4.修改进程状态为就绪
        process.getPCB().setStatus(PROCESS_STATES.READY);

        // 5.链入就绪队列
        readyQueue.offerPCB(process);
        System.out.println("-------Process-------进程唤醒成功");
        return true;
    }

    // 运行 -> 阻塞
    public synchronized boolean block(Process process) {
        // 1.判断当前进程是否为空
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return false;
        }

        // 2.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.BLOCKED);

        // 3.将进程链入对应的阻塞队列
        blockingQueue.offerPCB(process);
        System.out.println("-------Process-------进程阻塞成功");
        return true;
    }

    // 运行 -> 就绪
    public synchronized boolean handoff(Process process) {
        // 1.判断当前进程是否为空
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return false;
        }

        // 2.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.READY);

        // 3.将进程链入对应的就绪队列
        readyQueue.offerPCB(process);
        System.out.println("-------Process-------进程切换成功");
        return true;
    }

    public void clockSchedule() {
        Process runningProcess = CpuController.getInstance().getCurrentProcess();

        if (runningProcess == null) {
            System.out.println("-------Process-------没有正在运行的进程");
            return;
        }

        if (runningProcess.getPCB().getStatus() == PROCESS_STATES.RUNNING) {
            currentProcessTimeSlice--;
            if (currentProcessTimeSlice == 0) {
                // 时间片用完，请求中断处理
                CpuController.getInstance().requestInterrupt(INTERRUPT.TimeSliceEnd, runningProcess);
                resetTimeSlice();
            }
        }
    }

    private void resetTimeSlice() {
        this.currentProcessTimeSlice = MAX_CLOCK;
    }
}


