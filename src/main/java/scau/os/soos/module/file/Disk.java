package scau.os.soos.module.file;

import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Item;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Disk {
    public static final int BLOCKS_PER_DISK = 256;
    public static final int BYTES_PER_BLOCK = 64;
    public static final int[] FAT_BLOCK_NUMS = {0, 1, 2, 3};
    public static final int PARTITION_BLOCK_NUM = 4;

    private byte[][] disk;
    private final Fat fat;
    private Directory partitionDirectory;

    public Disk() {
        this.disk = new byte[BLOCKS_PER_DISK][BYTES_PER_BLOCK];
        disk2file();
//        this.file2disk();
//        for (byte[]bytes:getDisk()){
//            System.out.println(Arrays.toString(bytes));
//        }
        this.fat = new Fat(this);
        this.fat.init();
//        init();
    }

    public void init() {
        this.partitionDirectory = new Directory(
                null,
                "",
                (byte)0,
                false,
                false,
                false,
                true,
                PARTITION_BLOCK_NUM,
                0);
        Directory c = new Directory(
                partitionDirectory,
                "C:",
                (byte)0,
                false,
                false,
                false,
                true,
                5,
                0);
        partitionDirectory.addChildren(c);
        fat.setNextBlockIndex(PARTITION_BLOCK_NUM, Fat.TERMINATED);
        partitionDirectory.isRoot(true);
        c.isRoot(true);
        partitionDirectory.setPath();
        partitionDirectory.initFromDisk();
        partitionDirectory.writeContentToDisk();
        fat.writeFatToDisk();
    }

    public void test(){
        for (byte[] bytes : disk) {
            for (byte b : bytes) System.out.print((b & 0xFF) + " ");
            System.out.println();
        }
    }

    public Directory getPartitionDirectory() {
        return partitionDirectory;
    }

    public Fat getFat() {
        return fat;
    }

    public boolean isItemExist(Item item) {
        int rootBlockNum = item.getRootParent().getStartBlockNum();
        return !fat.isFreeBlock(item.getStartBlockNum(),rootBlockNum);
    }

    public int findFreeDiskBlock(int rootBlockNum) {
        //从第3块磁盘块开始查询，如果找到空闲磁盘块则返回该编号，否则返回-1
        for (int i = PARTITION_BLOCK_NUM + 1; i < BLOCKS_PER_DISK; i++) {
            if (fat.isFreeBlock(i,rootBlockNum)) {
                return i;
            }
        }
        return -1;
    }

    public List<Integer> findFreeDiskBlock(int num,int rootBlockNum){

        List<Integer> list = new ArrayList<>();
        int n=0;

        for (int i = PARTITION_BLOCK_NUM + 1; i < BLOCKS_PER_DISK; i++) {
            if(n>=num){
                break;
            }
            if (fat.isFreeBlock(i,rootBlockNum)) {
                list.add(i);
                n++;
            }
        }

        return list;
    }

    public List<Integer> findFreeDiskBlockFromTail(int num,int rootBlockNum){

        List<Integer> list = new ArrayList<>();
        int n=0;

        for(int i = BLOCKS_PER_DISK-1; i >=PARTITION_BLOCK_NUM + 1; i--){
            if(n>=num){
                break;
            }
            if (fat.isFreeBlock(i,rootBlockNum)) {
                list.add(i);
                n++;
            }
        }

        List<Integer> list2 = new ArrayList<>();
        for(int i = list.size()-1; i >= 0; i--){
            list2.add(list.get(i));
        }

        return list2;
    }

    public int findLastDisk(int startDisk) {
        int endDisk = startDisk;
        while (fat.getNextBlockIndex(endDisk) != Fat.TERMINATED) {
            endDisk = fat.getNextBlockIndex(endDisk);
        }
        return endDisk;
    }

    public byte[] getDiskBlock(int blockNumber) {
        // 检查blockNumber是否在有效范围内
        if (blockNumber < 0 || blockNumber >= BLOCKS_PER_DISK) {
            return null;
        }
        return disk[blockNumber];
    }

    public void setDiskBlock(int blockNumber, byte[] newContent) {
        // 检查blockNumber是否在有效范围内
        if (blockNumber < 0 || blockNumber >= BLOCKS_PER_DISK) {
            return;
        }
        System.arraycopy(newContent, 0, getDiskBlock(blockNumber), 0, BYTES_PER_BLOCK);
    }

    public void copyDiskBlock(int src, int dest) {
        System.arraycopy(getDiskBlock(src), 0, getDiskBlock(dest), 0, BYTES_PER_BLOCK);
    }

    public void formatFatTable(int startBlockNum,int rootBlockNum) {
        int currentIndex = startBlockNum;
        int nextIndex;

        while (true) {
            nextIndex = fat.getNextBlockIndex(currentIndex);
            fat.setNextBlockIndex(currentIndex, rootBlockNum);
            if (nextIndex == Fat.TERMINATED) {
                break;
            }
            currentIndex = nextIndex;
        }
    }

    public void disk2file() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("disk.dat"))) {
            oos.writeObject(disk); // 写入 Disk 对象
        } catch (IOException e) {
            System.out.println("disk2file fail!");
            throw new RuntimeException(e);
        }
    }

    public void file2disk() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("disk.dat"))) {
            disk = (byte[][]) ois.readObject(); // 读取 Disk 对象
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("file2disk fail!");
            throw new RuntimeException(e);
        }
    }
}
