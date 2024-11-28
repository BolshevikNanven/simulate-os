package scau.os.soos.module.file.model;

import scau.os.soos.module.file.Disk;

import java.nio.charset.StandardCharsets;

public class Txt extends Item {
    private StringBuilder context;

    public Txt(byte[] data) {
        super(data);
        this.context = new StringBuilder();
    }

    public Txt(Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        super(parent,name, type, readOnly, systemFile, regularFile, isDirectory, startBlockNum,size);
        this.context = new StringBuilder();
    }

    public void setContext(String context){
        this.context = new StringBuilder(context);
    }

    public String getContext() {
        return context.toString();
    }

    public void updateSize(){
        setSize(context.toString().trim().getBytes().length);
    }

    public void initFromDisk() {
        this.context = new StringBuilder();


        byte[][] content = super.readContentFromDisk();
        byte[] contentBytes = new byte[content.length*Disk.BYTES_PER_BLOCK];

        for(int i=0;i<content.length;i++){
            System.arraycopy(content[i],0,contentBytes,(i*Disk.BYTES_PER_BLOCK),Disk.BYTES_PER_BLOCK);
        }
        context.append(new String(contentBytes, StandardCharsets.UTF_8));

        updateSize();
    }

    public boolean writeContentToDisk() {
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
        return super.writeContentToDisk(allItemsData);
    }

    public Item copy(){
        Txt newItem = new Txt(
                null,
                this.getName(),
                this.getType(),
                this.isReadOnly(),
                this.isSystemFile(),
                this.isRegularFile(),
                this.isDirectory(),
                0,
                0);
        newItem.setContext(this.getContext());
        newItem.updateSize();
        return newItem;
    }

    public String toString(){
        return "Txt: "+
                super.toString()+
                " Context: " + getContext();
    }
}
