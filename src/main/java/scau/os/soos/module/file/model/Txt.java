package scau.os.soos.module.file.model;

import java.nio.charset.StandardCharsets;

public class Txt extends Item {
    private StringBuilder context;

    public Txt(Disk disk, byte[] data) {
        super(disk, data);
    }

    public Txt(Disk disk, Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        super(disk, parent,name, type, readOnly, systemFile, regularFile, isDirectory, startBlockNum,size);
        this.context = new StringBuilder();
    }

    public String getContext() {
        return context.toString();
    }

    public void updateSize(){
        setSize(context.length());
    }

    public void initFromString(String content) {
        this.context = new StringBuilder(context);
        setSize(content.length());
    }

    public void initFromDisk() {
        Disk disk = super.getDisk();
        this.context = new StringBuilder();

        byte[][] content = super.readContentFromDisk(disk);

        int size = 0;
        for (byte[] block : content) {
            // 使用StringBuilder的append方法高效连接字符串
            for(byte b : block){
                this.context.append((char) b);
                size += block.length;
            }

        }

        setSize(size);
    }

    public boolean writeContentToDisk() {
        // 将context转换为字节数组
        byte[] contextBytes = context.toString().getBytes(StandardCharsets.US_ASCII);

        // 计算需要多少个数据块来存储所有子项
        int blockNum = (int) Math.ceil((double) contextBytes.length / getDisk().BYTES_PER_BLOCK);
        byte[][] allItemsData = new byte[blockNum][getDisk().BYTES_PER_BLOCK];

        // 将内容复制到数据块中
        int byteIndex = 0;
        for (int block = 0; block < blockNum; block++) {
            byte[] currentBlock = allItemsData[block];
            int bytesToCopy = Math.min(contextBytes.length - byteIndex, getDisk().BYTES_PER_BLOCK);
            System.arraycopy(contextBytes, byteIndex, currentBlock, 0, bytesToCopy);
            byteIndex += bytesToCopy;
        }

        // 调用父类方法，将整合后的数据写入磁盘
        return super.writeContentToDisk(allItemsData);
    }
}
