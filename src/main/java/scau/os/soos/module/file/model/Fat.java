package scau.os.soos.module.file.model;

public class Fat {
    private final Disk disk;
    public static final int FREE = 0;
    public static final int TERMINATED = 1;

    private final byte[] fat;

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

    public void reset() {
        for (int i = 0; i < Disk.BLOCKS_PER_DISK; i++) {
            fat[i] = FREE;
        }
        for (int i = 0; i < Disk.BLOCKS_PER_DISK / Disk.BYTES_PER_BLOCK; i++) {
            fat[i] = TERMINATED;
        }
    }

    public boolean setNextBlock(int diskNum, int nextDisk) {
        if (isFreeBlock(diskNum)) {
            fat[diskNum] = (byte) nextDisk;
        } else {
            System.out.println("Block is not free");
            return false;
        }
        return true;
    }

    public int getNextBlockNum(int diskNum) {
        return fat[diskNum];
    }

    public boolean isFreeBlock(int diskNum) {
        if (diskNum < 0 || diskNum >= fat.length) {
            System.out.println("Block is out of range");
        }
        return fat[diskNum] == FREE;
    }

    public boolean updateDisk() {
        byte[] data = new byte[Disk.BLOCKS_PER_DISK];

        int index = 0;
        for (int i : Disk.FAT_BLOCK_NUMS) {
            for (int j = 0; j < Disk.BLOCKS_PER_DISK; j++, index++) {
                data[j] = fat[index];
            }
            disk.setDiskBlock(i, data);
        }

        return true;
    }
}
