package scau.os.soos.module.process;

import scau.os.soos.common.enums.CPU_STATES;
import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.common.enums.PROCESS_STATES;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.MyFile;
import scau.os.soos.module.memory.MemoryController;
import scau.os.soos.module.memory.model.Memory;
import scau.os.soos.module.process.model.BlockingQueue;
import scau.os.soos.module.process.model.EmptyPCBQueue;
import scau.os.soos.module.process.model.PCB;
import scau.os.soos.module.process.model.ReadyQueue;
import scau.os.soos.module.process.model.Process;

public class ProcessService {
    // 进程最大数量
    private static final int MAX_PROCESS_COUNT = 10;
    // 进程实例数量跟踪，用于生成pid
    private int processCount = 1;
    // 空白PCB队列，用于生成进程
    private final EmptyPCBQueue emptyPCBQueue;
    // 就绪队列
    private final ReadyQueue readyQueue;
    // 阻塞队列
    private final BlockingQueue blockingQueue;

    public ProcessService() {
        this.emptyPCBQueue = new EmptyPCBQueue(MAX_PROCESS_COUNT);
        this.readyQueue = new ReadyQueue();
        this.blockingQueue = new BlockingQueue();
    }

    // 进程创建
    public Process create(MyFile file) {
        // 1.申请空白进程控制块
        PCB newPCB = emptyPCBQueue.applyEmptyPCB();
        if (newPCB == null) {
            System.out.println("-------Process-------进程控制块申请失败");
            return null;
        }

        // 2.查询文件大小
        int fileSize = FileController.getInstance().getFileSize(file);

        // 3.申请内存空间
        if (MemoryController.getInstance().allocate(fileSize)) {
            System.out.println("-------Process-------进程内存申请失败");
            return null;
        }

        // 4.初始化进程控制块
        Process process = new Process(newPCB, processCount++);
        // 设置进程为新建态
        process.getPCB().setStatus(PROCESS_STATES.NEW);

        // 5.将进程装入内存系统区
        MemoryController.getInstance().write(process.getPCB());

        // 6.将文件装入内存用户区
        // 获取文件数据
        Object content = FileController.getInstance().readFile(file);
        // 写入内存用户区
        MemoryController.getInstance().write(, file);

        // 7.将进程放入就绪队列
        // 设置进程为就绪态度
        process.getPCB().setStatus(PROCESS_STATES.READY);
        readyQueue.offerPCB(process);

        System.out.println("-------Process-------进程创建成功");
        return process;
    }

    public void clockSchedule() {
//        // 判断就绪队列是否为空
//        if (readyQueue.isEmpty()) {
//            System.out.println("-------Process-------就绪队列为空，无法调度");
//            return;
//        }
//
//        // 判断cpu空闲
//        if (CpuController.getInstance().getCpuState() == CPU_STATES.BUSY) {
//            System.out.println("-------Process-------CPU繁忙，无法调度");
//            return;
//        }
//
//        // 调度进程上处理机
//        Process process = readyQueue.pollPCB().getProcess();
//        if (process != null) {
//            processSchedule(process);
//        }
    }

    public void processSchedule() {
        System.out.println("调度新进程");

        // 1.从就绪队列中选择一个进程
        Process process = readyQueue.pollPCB().getProcess();
        if(process == null) {
            System.out.println("-------Process-------进程为空");
            return;
        }

        // 2.恢复现场
        CpuController.getInstance().setAX(process.getPCB().getAX());
        CpuController.getInstance().setPC(process.getPCB().getPC());
        process.getPCB().setStatus(PROCESS_STATES.RUNNING);

        // 3.更新当前进程
        CpuController.getInstance().setCurrentProcess(process);

        System.out.println("-------Process-------进程调度成功");
    }

    // 进程撤销
    public void destroy(Process process) {
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return;
        }

        // 1.回收进程内存
        MemoryController.getInstance().free(process.getPCB());
        // 2.回收进程控制块
        // 设置进程为终止态度
        process.getPCB().setStatus(PROCESS_STATES.TERMINATED);
        emptyPCBQueue.recycleEmptyPCB();
        System.out.println("-------Process-------进程销毁成功");
    }

    // 进程唤醒
    public void wake(DEVICE_TYPE deviceType) {

    }

    // 进程唤醒
    public void wake(Process process) {
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return;
        }

        if (blockingQueue.isEmpty()) {
            System.out.println("-------Process-------阻塞队列为空");
            return;
        }

        // 1.进程唤醒的主要工作是将进程由阻塞队列中摘下
        if (!blockingQueue.removePCB(process.getPCB())) {
            System.out.println("-------Process-------阻塞队列中不存在该进程");
            return;
        }

        // 2.修改进程状态为就绪
        process.getPCB().setStatus(PROCESS_STATES.READY);

        // 3.链入就绪队列
        readyQueue.offerPCB(process);
        System.out.println("-------Process-------进程唤醒成功");
    }

    // 进程阻塞
    public void block(Process process) {
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return;
        }

        // 1.保存运行进程的CPU现场
        process.getPCB().setAX(CpuController.getInstance().getAX());
        process.getPCB().setPC(CpuController.getInstance().getPC());

        // 2.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.BLOCKED);

        // 3.将进程链入对应的阻塞队列
        blockingQueue.offerPCB(process);
        System.out.println("-------Process-------进程阻塞成功");
    }

    // 进程切换
    public void handoff(Process process) {
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return;
        }

        // 1.保存运行进程的CPU现场
        process.getPCB().setAX(CpuController.getInstance().getAX());
        process.getPCB().setPC(CpuController.getInstance().getPC());

        // 2.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.READY);

        // 3.将进程链入对应的就绪队列
        readyQueue.offerPCB(process);
        System.out.println("-------Process-------进程切换成功");
    }
}
