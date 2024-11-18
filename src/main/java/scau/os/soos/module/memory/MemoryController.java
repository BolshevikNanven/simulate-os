package scau.os.soos.module.memory;

import scau.os.soos.module.Module;
import scau.os.soos.module.memory.view.MemoryReadView;
import scau.os.soos.module.process.model.PCB;
import scau.os.soos.module.process.model.Process;

import java.util.Map;

public class MemoryController implements Module {
    private static MemoryController instance;
    private final MemoryService memoryService;

    public synchronized static MemoryController getInstance() {
        if (instance == null) {
            instance = new MemoryController();
        }
        return instance;
    }

    private MemoryController() {
        memoryService = new MemoryService();
    }

    public boolean allocate(PCB pcb, int size) {
        return memoryService.allocateMemory(pcb, size);
    }

    public boolean free(PCB pcb) {
        return memoryService.freeMemory(pcb);
    }

    public int read(int address) {
        return memoryService.read(address);
    }

    public boolean write(int address, int content) {
        return memoryService.write(address, content);
    }

    public MemoryReadView getData() {
        return memoryService.analyse();
    }

    public Map<Integer,Integer> getProcessUsage(){
        return memoryService.getUsage();
    }
    @Override
    public void run() {

    }

    public static void main(String[] args) {
        PCB pcb1 = new Process(new PCB(), 1).getPCB();
        PCB pcb2 = new Process(new PCB(), 2).getPCB();
        PCB pcb3 = new Process(new PCB(), 3).getPCB();

        getInstance().allocate(pcb1, 128);
        getInstance().allocate(pcb2, 44);
        getInstance().allocate(pcb3, 15);

        getInstance().free(pcb2);
        getInstance().free(pcb1);

        getInstance().memoryService.printMemory();
    }
}
