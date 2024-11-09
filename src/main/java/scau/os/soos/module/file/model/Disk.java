package scau.os.soos.module.file.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Disk {
    public final int BLOCKS_PER_DISK = 256;
    public final int BYTES_PER_BLOCK = 64;
    public final int[] FAT_BLOCK_NUMS = {0, 1, 2, 3};
    public final int ROOT_BLOCK_NUM = 4;

    private byte[][] disk;
    private final Fat fat;
    private final Directory rootDirectory;

    public Disk() {
//        this.disk = new byte[BLOCKS_PER_DISK][BYTES_PER_BLOCK];
//        disk2file();
        this.file2disk();
        for (byte[]bytes:getDisk()){
            System.out.println(Arrays.toString(bytes));
        }
        this.fat = new Fat(this);
        this.rootDirectory = new Directory(
                this,
                null,
                "",
                (byte)0,
                false,
                false,
                false,
                true,
                ROOT_BLOCK_NUM,
                0);
        fat.setNextBlockIndex(ROOT_BLOCK_NUM, Fat.TERMINATED);
        rootDirectory.setPath();
        rootDirectory.initFromDisk();
    }

    public byte[][] getDisk() {
        return disk;
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    public Fat getFat() {
        return fat;
    }

    public boolean isItemExist(Item item) {
        return !fat.isFreeBlock(item.getStartBlockNum());
    }

    public int findFreeDiskBlock() {
        //从第3块磁盘块开始查询，如果找到空闲磁盘块则返回该编号，否则返回-1
        for (int i = ROOT_BLOCK_NUM + 1; i < BLOCKS_PER_DISK; i++) {
            if (fat.isFreeBlock(i)) {
                return i;
            }
        }
        return -1;
    }

    public List<Integer> findFreeDiskBlock(int num) {
        List<Integer> list = new ArrayList<>();
        int n=0;

        for (int i = ROOT_BLOCK_NUM + 1; i < BLOCKS_PER_DISK; i++) {
            if (fat.isFreeBlock(i) && n<num) {
                list.add(i);
                n++;
            }
        }

        return list;
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

    public void formatFatTable(int startBlockNum) {
        int currentIndex = startBlockNum;
        int nextIndex;

        while (true) {
            nextIndex = fat.getNextBlockIndex(currentIndex);
            fat.setNextBlockIndex(currentIndex, Fat.FREE);
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
