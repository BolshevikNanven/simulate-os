package scau.os.soos.module.file.model;

public class Fat {
    private final Disk disk;
    public static final int FREE = 0;
    public static final int TERMINATED = 1;

    private final byte[] fat;

    /**
     * Fat类的构造函数
     *
     * @param disk 硬盘对象
     *             硬盘对象，用于初始化Fat对象中的disk属性，并从中读取FAT信息
     */
    public Fat(Disk disk) {
        this.disk = disk;
        this.fat = new byte[disk.BLOCKS_PER_DISK];

        int index = 0;
        for (int i : disk.FAT_BLOCK_NUMS) {
            byte[] content = disk.getDiskBlock(i);
            for (byte b : content) {
                fat[index] = b;
                index++;
            }
        }
    }

    /**
     * 重置FAT表
     * 将FAT表中的所有块状态重置为FREE（空闲）状态，除了最后一个块设置为TERMINATED（终止）状态。
     */
    public void resetFat() {
        for (int i = 0; i < disk.BLOCKS_PER_DISK; i++) {
            fat[i] = FREE;
        }
        for (int i = 0; i < disk.BLOCKS_PER_DISK / disk.BYTES_PER_BLOCK; i++) {
            fat[i] = TERMINATED;
        }
    }

    /**
     * 设置指定磁盘块的下一个磁盘块索引
     */
    public void setNextBlockIndex(int diskNum, int nextDisk) {
        fat[diskNum] = (byte) nextDisk;
    }

    /**
     * 获取指定磁盘块的下一个磁盘块索引
     */
    public int getNextBlockIndex(int diskNum) {
        return fat[diskNum];
    }

    public boolean isFreeBlock(int diskNum) {
        if (diskNum < 0 || diskNum >= fat.length) {
            System.out.println("Block is out of range");
        }
        return fat[diskNum] == FREE;
    }

    /**
     * 更新磁盘块信息
     * 将FAT表中的数据更新到磁盘中对应的磁盘块中
     *
     * @return 总是返回true，表示更新操作成功
     */
    public boolean writeFatToDisk() {
        byte[] data = new byte[disk.BLOCKS_PER_DISK];

        int index = 0;
        for (int i : disk.FAT_BLOCK_NUMS) {
            for (int j = 0; j < disk.BYTES_PER_BLOCK; j++, index++) {
                data[j] = fat[index];
            }
            disk.setDiskBlock(i, data);
        }

        return true;
    }
}
