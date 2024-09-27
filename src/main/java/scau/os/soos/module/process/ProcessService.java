package scau.os.soos.module.process;

import scau.os.soos.common.enums.PROCESS_STATES;
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
    private EmptyPCBQueue emptyPCBQueue;
    // 就绪队列
    private ReadyQueue readyQueue;
    // 阻塞队列
    private BlockingQueue blockingQueue;

    public ProcessService() {
        this.emptyPCBQueue = new EmptyPCBQueue(MAX_PROCESS_COUNT);
        this.readyQueue = new ReadyQueue();
        this.blockingQueue = new BlockingQueue();
    }

    public Process create(MyFile file){
        // 1.申请空白进程控制块
        if(emptyPCBQueue.isEmpty()){
            System.out.println("进程控制块申请失败");
            return null;
        }
        PCB newPCB = emptyPCBQueue.applyEmptyPCB();

        // 2.初始化进程
        Process process = new Process(newPCB,processCount++);
        // 设置进程为新建态
        process.getPCB().setStatus(PROCESS_STATES.NEW);
        // 装入内存系统区
        MemoryController.getInstance().allocate(process.getPCB());

        // 3.申请内存空间
//        // Memory memory = MemoryController.getInstance().allocate(process);
//        if (memory == null) {
//            System.out.println("进程内存申请失败");
//            return null;
//        }


        // 4.将进程放入就绪队列
        readyQueue.offerPCB(process);
        // 设置进程为就绪态度
        process.getPCB().setStatus(PROCESS_STATES.READY);

        return process;
    }

    public void clockSchedule() {
        // 判断就绪队列是否为空

        // 判断cpu空闲

        // 调度进程上处理机

    }

    public void processSchedule() {
        System.out.println("调度新进程");
    }





}
