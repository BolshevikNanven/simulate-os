package scau.os.soos.module.file.model;

public class Disk {
    public static final int BLOCKS_PER_DISK = 256;
    public static final int BYTES_PER_BLOCK = 64;
    public static final int[] FAT_BLOCK_NUMS = {0, 1, 2, 3};
    public static final int ROOT_BLOCK_NUM = 4;

    private final byte[][] disk;
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

    public boolean isItemExist(Item item){
        return !fat.isFreeBlock(item.getStartBlockNum());
    }

    public byte[] getDiskBlock(int blockNumber) {
        // 检查blockNumber是否在有效范围内
        if (blockNumber < 0 || blockNumber >= BLOCKS_PER_DISK) {
            return null;
        }
        return disk[blockNumber];
    }

    public boolean setDiskBlock(int blockNumber, byte[] newContent) {
        // 检查blockNumber是否在有效范围内
        if (blockNumber < 0 || blockNumber >= BLOCKS_PER_DISK) {
            return false;
        }
        System.arraycopy(newContent, 0, disk[blockNumber], 0, BYTES_PER_BLOCK);
        return true;
    }

    public boolean isFreeBlock(int diskBlock) {
        return fat.isFreeBlock(diskBlock);
    }

    public boolean readDisk() {

        return true;
    }

    public boolean writeDisk() {

        return true;
    }
}
