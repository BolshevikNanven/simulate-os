package scau.os.soos.module.memory;

import scau.os.soos.module.Module;

public class MemoryController implements Module {
    private static MemoryController instance;
    private final MemoryService memoryService;

    public static MemoryController getInstance() {
        if (instance == null) {
            instance = new MemoryController();
        }
        return instance;
    }

    private MemoryController() {
        memoryService = new MemoryService();
    }

    /**
     * 内存分配
     * @param size 申请内存大小
     * @param content 申请内存内容
     * @return 内存地址 失败时返回-1
     */
    public int allocate(int size, Object content) {
        return 0;
    }

    /**
     * 内存回收
     * @param address 内存地址
     * @param size 内存大小
     *             失败时返回false,成功返回true
     */
    public boolean free(int address, int size) {

    }


    /**
     * 读取内存内容
     * @param address 内存地址
     * @return 内存内容
     */
    public Object readMemoryContent(int address) {
        return null;
    }





    @Override
    public void run() {

    }
}
