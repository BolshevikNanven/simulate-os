package scau.os.soos.module.process;

import scau.os.soos.common.enums.CPU_STATES;
import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.common.enums.PROCESS_STATES;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.MyFile;
import scau.os.soos.module.memory.MemoryController;
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

    // 新建 -> 就绪
    public Process create(MyFile file) {
        if (file == null) {
            System.out.println("-------Process-------文件为空");
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
        Object content = FileController.getInstance().readFile(file);
        // 写入内存用户区
        MemoryController.getInstance().write(newPCB.getPC(), content);

        // 6.将进程放入就绪队列
        // 设置进程为就绪态度
        process.getPCB().setStatus(PROCESS_STATES.READY);
        readyQueue.offerPCB(process);

        System.out.println("-------Process-------进程创建成功");
        return process;
    }

    public void clockSchedule() {
        // 判断就绪队列是否为空

        // 判断cpu空闲

        // 调度进程上处理机
    }

    // 就绪 -> 运行
    public boolean processSchedule() {
        System.out.println("-------Process-------调度新进程");

        // 1.从就绪队列中选择一个进程
        Process process = readyQueue.pollPCB().getProcess();
        if (process == null) {
            System.out.println("-------Process-------就绪队列为空");
            return false;
        }

        // 2.判断CPU是否空闲
        if (CpuController.getInstance().getCpuState() == CPU_STATES.BUSY) {
            System.out.println("-------Process-------CPU繁忙");
            return false;
        }

        // 3.恢复现场
        if (!CpuController.getInstance().handleProcess(process)) {
            System.out.println("-------Process-------恢复现场失败");
            // 将进程放入就绪队列
            readyQueue.offerPCB(process);
            return false;
        }

        // 4.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.RUNNING);
        System.out.println("-------Process-------进程调度成功");
        return true;
    }

    // 运行 -> 退出
    public boolean destroy(Process process) {
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
    public boolean wake(DEVICE_TYPE deviceType) {
        // ??? 要判断下设备数量 防止多个模块重复唤醒 ???

        // 1.判断阻塞队列是否为空
        if (blockingQueue.isEmpty()) {
            System.out.println("-------Process-------阻塞队列为空");
            return false;
        }

        // 2.将特定进程由阻塞队列中摘下
        Process process = blockingQueue.removePCB(deviceType);
        if (process == null) {
            System.out.println("-------Process-------阻塞队列中不存在该进程");
            return false;
        }

        // 3.修改进程状态为就绪
        process.getPCB().setStatus(PROCESS_STATES.READY);

        // 4.链入就绪队列
        readyQueue.offerPCB(process);
        System.out.println("-------Process-------进程唤醒成功");
        return true;
    }

    // 阻塞 -> 就绪
    public boolean wake(Process process) {
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
    public boolean block(Process process) {
        // 1.判断当前进程是否为空
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return false;
        }

        // 2.保存运行进程的CPU现场
        if (!CpuController.getInstance().requestInterrupt(INTERRUPT.IO, process)) {
            System.out.println("-------Process-------保存现场失败");
            return false;
        }

        // 3.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.BLOCKED);

        // 4.将进程链入对应的阻塞队列
        blockingQueue.offerPCB(process);
        System.out.println("-------Process-------进程阻塞成功");
        return true;
    }

    // 运行 -> 就绪
    public boolean handoff(Process process) {
        // 1.判断当前进程是否为空
        if (process == null) {
            System.out.println("-------Process-------进程为空");
            return false;
        }

        // 2.保存运行进程的CPU现场
        if (!CpuController.getInstance().requestInterrupt(INTERRUPT.TimeSliceEnd, process)) {
            System.out.println("-------Process-------保存现场失败");
            return false;
        }

        // 3.修改进程状态
        process.getPCB().setStatus(PROCESS_STATES.READY);

        // 4.将进程链入对应的就绪队列
        readyQueue.offerPCB(process);
        System.out.println("-------Process-------进程切换成功");
        return true;
    }
    
}


