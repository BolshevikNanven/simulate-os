package scau.os.soos.module.file.model;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.file.Disk;
import scau.os.soos.module.file.FileService;

import java.util.ArrayList;
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
        for (int i = 0; i < Disk.PARTITION_BLOCK_NUM ; i++) {
            fat[i] = TERMINATED;
        }
        fat[Disk.PARTITION_BLOCK_NUM] = Disk.PARTITION_BLOCK_NUM;
        writeFatToDisk();
    }

    public void init(){
        for (int i = 5; i < fat.length ; i++) {
            fat[i]=(byte)5;
        }
        writeFatToDisk();
    }

    /**
     * 重置FAT表
     * 将FAT表中的所有块状态重置为FREE（空闲）状态，除了最后一个块设置为TERMINATED（终止）状态。
     */
    public int diskPartition(String s_path,int diskNum){
        Directory s_root = (Directory) FileService.find(s_path, FILE_TYPE.DIRECTORY);

        List<Integer> blockNums = disk.findFreeDiskBlockFromTail(diskNum,s_root.getStartBlockNum());

        for(int i:blockNums){
            fat[i] = (byte) (blockNums.get(0)& 0xFF);
        }
        return blockNums.get(0);
    }

    public void resetFat(String path) {
        Directory root = (Directory) FileService.find(path, FILE_TYPE.DIRECTORY);
        List<Integer> blockNums = new ArrayList<>();
        getDiskAreaBlockNum(root,blockNums);//获取指定磁盘区域中的所有占用磁盘块索引
        for (int i : blockNums) {
            fat[i] = (byte) root.getStartBlockNum();
        }
        //[0,1,2,3,4]块磁盘占用
        for (int i = 0; i < Disk.PARTITION_BLOCK_NUM; i++) {
            fat[i] = TERMINATED;
        }
        fat[Disk.PARTITION_BLOCK_NUM] = Disk.PARTITION_BLOCK_NUM;
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
//        System.out.println(startBlockNum);
//        Directory root = disk.getPartitionDirectory();
//        for (Item e : root.getChildren()) {
//            FREE.add(e.getStartBlockNum());

        return fat[diskNum] ==  (startBlockNum & 0xFF);
        //看fat[diskNum]是否在FREE列表中，即fat[diskNum]指向的块是否是根目录块，是则为空
        //return FREE.contains(fat[diskNum]);
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
