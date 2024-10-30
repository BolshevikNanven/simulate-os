package scau.os.soos.module.file.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Disk {
    public static final int BLOCKS_PER_DISK = 256;
    public static final int BYTES_PER_BLOCK = 64;
    public static final int[] FAT_BLOCK_NUMS = {0, 1, 2, 3};
    public static final int ROOT_BLOCK_NUM = 4;

    private byte[][] disk;
    private final Fat fat;
    private final Directory rootDirectory;

    public Disk() {
        this.disk = new byte[BLOCKS_PER_DISK][BYTES_PER_BLOCK];
        this.fat = new Fat(this);
        this.rootDirectory = new Directory(this, disk[ROOT_BLOCK_NUM]);
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    public Fat getFat() {
        return fat;
    }

    public Item find(String path) {
        return rootDirectory.find(path);
    }

    public boolean isItemExist(Item item) {
        return !fat.isFreeBlock(item.getStartBlockNum());
    }

    public boolean isFreeBlock(int diskBlock) {
        return fat.isFreeBlock(diskBlock);
    }

    public int findFreeDiskBlock() {
        //从第3块磁盘块开始查询，如果找到空闲磁盘块则返回该编号，否则返回-1
        for (int i = ROOT_BLOCK_NUM + 1; i < Disk.BLOCKS_PER_DISK; i++) {
            if (fat.isFreeBlock(i)) {
                return i;
            }
        }
        return -1;
    }

    public List<Integer> findFreeDiskBlock(int num) {
        List<Integer> list = new ArrayList<>();
        for (int i = ROOT_BLOCK_NUM + 1; i < Disk.BLOCKS_PER_DISK; i++) {
            if (fat.isFreeBlock(i)) {
                list.add(i);
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
        System.arraycopy(newContent, 0, disk[blockNumber], 0, BYTES_PER_BLOCK);
    }

    public void occupyDiskBlock(int blockNumber) {
        fat.setNextBlockIndex(blockNumber, Fat.TERMINATED);
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
            e.printStackTrace();
        }
    }

    public void file2disk() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("disk.dat"))) {
            disk = (byte[][]) ois.readObject(); // 读取 Disk 对象
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
