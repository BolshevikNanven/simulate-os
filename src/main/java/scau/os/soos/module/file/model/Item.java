package scau.os.soos.module.file.model;

import java.util.Arrays;

public class Item {
    private final byte[] name = new byte[3];
    private final byte type;
    private final byte attribute;
    private final byte startBlockNum;
    private final byte[] size = new byte[2];
    private boolean exist;

    public Item(byte[] data) {
        System.arraycopy(data, 0, name, 0, 3);
        type = data[3];
        attribute = data[4];
        startBlockNum = data[5];
        System.arraycopy(data, 6, size, 0, 2);
        exist = startBlockNum != 0x00;
    }

    public String getName() {
        return Arrays.toString(name);
    }

    public char getType() {
        return (char) type;
    }

    public char getAttribute() {
        return (char) attribute;
    }

    public int getStartBlockNum() {
        return (int) startBlockNum;
    }

    public int getSize() {
        return size[0] << 8 + size[1];
    }

    public void updateExist(){
        exist = !exist;
    }

    public boolean isExist() {
        return exist;
    }

    protected byte[][] getContent(Disk disk) {
        Fat fat = disk.getFat();
        int bytesPerBlock = Disk.BYTES_PER_BLOCK;
        int startBlockNum = this.startBlockNum;

        int blockCount = calculateTotalBlockNum(fat);

        byte[][] content = new byte[blockCount][bytesPerBlock];

        // 重新遍历FAT表，以实际读取数据块并填充二维数组
        int currentBlock = startBlockNum;
        for(int i = 0; i < blockCount; i++){
            byte[] blockData = disk.getDiskBlock(currentBlock);
            // 复制数据到二维数组的当前行
            System.arraycopy(blockData, 0, content[i], 0, bytesPerBlock);
            currentBlock = fat.getNextBlockNum(currentBlock);
        }

        return content; // 返回填充后的数组
    }

    private int calculateTotalBlockNum(Fat fat) {
        int blockCount = 0; // 用于记录占用的磁盘块数量
        int currentBlock = startBlockNum;
        while (currentBlock != Fat.TERMINATED) {
            blockCount++;
            currentBlock = fat.getNextBlockNum(currentBlock);
        }
        return blockCount;
    }
}
