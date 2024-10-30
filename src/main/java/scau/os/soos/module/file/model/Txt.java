package scau.os.soos.module.file.model;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Txt extends Item {
    private StringBuilder context;

    public Txt(Disk disk, byte[] data) {
        super(disk, data);

        initFromDisk(disk);
    }

    public Txt(String name, char type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, Disk disk, Item parent, String context) {
        super(name, type, readOnly, systemFile, regularFile, isDirectory, disk, parent);
        initFromString(context);
    }

    public void initFromDisk(Disk disk) {
        this.context = new StringBuilder();

        byte[][] content = super.readContentFromDisk(disk);

        if (content != null) { // 检查content是否为null
            for (byte[] block : content) {
                if (block != null) { // 检查block是否为null
                    // 使用StringBuilder的append方法高效连接字符串
                    this.context.append(Arrays.toString(block)); // 假设我们想要将byte数组转换为字符串形式并追加
                }
            }
        }
    }

    public void initFromString(String instructions) {
        this.context = new StringBuilder(context);
    }

    /**
     * 将内容写入磁盘
     *
     * @param disk 磁盘对象，用于存储内容
     */
    public void writeContentToDisk(Disk disk) {
        // 将context转换为字节数组
        byte[] contextBytes = context.toString().getBytes(StandardCharsets.UTF_8);

        // 计算需要多少个数据块来存储所有子项
        int blockNum = (int) Math.ceil((double) contextBytes.length / Disk.BYTES_PER_BLOCK);
        byte[][] allItemsData = new byte[blockNum][Disk.BYTES_PER_BLOCK];

        // 将内容复制到数据块中
        int byteIndex = 0;
        for (int block = 0; block < blockNum; block++) {
            byte[] currentBlock = allItemsData[block];
            int bytesToCopy = Math.min(contextBytes.length - byteIndex, Disk.BYTES_PER_BLOCK);
            System.arraycopy(contextBytes, byteIndex, currentBlock, 0, bytesToCopy);
            byteIndex += bytesToCopy;
        }

        // 调用父类方法，将整合后的数据写入磁盘
        super.writeContentToDisk(disk, allItemsData);
    }

    public String getContext() {
        return context.toString();
    }
}
