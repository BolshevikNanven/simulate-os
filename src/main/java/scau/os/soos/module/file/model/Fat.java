package scau.os.soos.module.file.model;

import scau.os.soos.module.file.Disk;

import java.util.List;

public class Fat {
    private final Disk disk;
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
        this.fat = new byte[Disk.BLOCKS_PER_DISK];

        int index = 0;
        for (int i : Disk.FAT_BLOCK_NUMS) {
            byte[] content = disk.getDiskBlock(i);
            for (byte b : content) {
                fat[index] = b;
                index++;
            }
        }
    }

    public void init(){
        for (int i = 0; i < Disk.PARTITION_BLOCK_NUM+1 ; i++) {
            fat[i] = TERMINATED;
        }
        for (int i = 5; i < fat.length ; i++) {
            fat[i]=(byte)5;
        }
        writeFatToDisk();
    }

    //该方法移植到FileService上
    private void getDiskAreaBlockNum(Directory root,List<Integer> blockNums) {
        for (Item e : root.getChildren()) {
            if(e instanceof Directory) {
                blockNums.add(e.getStartBlockNum());
                getDiskAreaBlockNum((Directory) e,blockNums);
            }else{
                blockNums.add(e.getStartBlockNum());
            }
        }
    }

    public void refresh(int oldFree,int newFree) {
        for(int i = Disk.PARTITION_BLOCK_NUM; i < Disk.BLOCKS_PER_DISK; i++){
            if(isFreeBlock(i,oldFree))
                setNextBlockIndex(i,newFree);
        }
    }

    /**
     * 设置指定磁盘块的下一个磁盘块索引
     */
    public void setNextBlockIndex(int diskNum, int nextDisk) {
        fat[diskNum] = (byte) ( nextDisk & 0xFF);
    }

    /**
     * 获取指定磁盘块的下一个磁盘块索引
     */
    public int getNextBlockIndex(int diskNum) {
        return fat[diskNum] & 0xFF;
    }

    public boolean isFreeBlock(int diskNum,int startBlockNum) {
        if (diskNum < 0 || diskNum >= fat.length) {
            System.out.println("Block is out of range");
        }

        return (fat[diskNum]&0xFF) ==  (startBlockNum & 0xFF);
    }

    /**
     * 更新磁盘块信息
     * 将FAT表中的数据更新到磁盘中对应的磁盘块中
     *
     * @return 总是返回true，表示更新操作成功
     */
    public boolean writeFatToDisk() {
        byte[] data = new byte[Disk.BLOCKS_PER_DISK];

        int index = 0;
        for (int i : Disk.FAT_BLOCK_NUMS) {
            for (int j = 0; j < Disk.BYTES_PER_BLOCK; j++, index++) {
                data[j] = fat[index];
            }
            disk.setDiskBlock(i, data);
        }

        return true;
    }
}
