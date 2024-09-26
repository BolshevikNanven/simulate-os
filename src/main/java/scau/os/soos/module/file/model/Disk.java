package scau.os.soos.module.file.model;

public class Disk {
    private static final int BLOCKS_PER_DISK = 256;
    private static final int BYTES_PER_BLOCK = 64;

    private final Object[][] disk;

    public Disk() {
        this.disk = new Object[BLOCKS_PER_DISK][BYTES_PER_BLOCK];
    }

    /**
     * 获取特定磁盘块的内容。
     *
     * @param blockNumber 磁盘块的编号，从0到BLOCKS_PER_DISK-1。
     * @return 指定编号的磁盘块的内容，如果编号无效则返回null。
     */
    public Object getDiskBlock(int blockNumber) {
        // 检查blockNumber是否在有效范围内
        if (blockNumber < 0 || blockNumber >= BLOCKS_PER_DISK) {
            return null;
        }
        return disk[blockNumber];
    }
}
