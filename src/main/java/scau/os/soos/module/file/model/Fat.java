package scau.os.soos.module.file.model;

public class Fat {
    public static final int BLOCK_SIZE = 512;
    public static final int FREE = 0;
    public static final int TERMINATED = 1;

    private final byte[] fat;

    public Fat(Disk disk, int block_per_disk, int[] nums) {
        this.fat = new byte[block_per_disk];

        int index = 0;
        for (int i : nums) {
            byte[] content = disk.getDiskBlock(i);
            for (byte b : content) {
                fat[index] = b;
                index++;
            }
        }
    }

    public void reset(int block_per_disk, int bytes_per_block) {
        for (int i = 0; i < block_per_disk; i++) {
            fat[i] = FREE;
        }
        for (int i = 0; i < block_per_disk / bytes_per_block; i++) {
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
}
