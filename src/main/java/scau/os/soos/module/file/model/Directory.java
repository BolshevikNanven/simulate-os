package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.List;

public class Directory extends Item{
    public static final int BYTES_PER_ITEM = 8;
    private final List<Item> children;

    public Directory(Disk disk,byte[]data){
        super(data);

        children = new ArrayList<>();

        initChildren(disk);
    }

    public void initChildren(Disk disk){
        byte[][] content = super.getContent(disk);

        for (byte[] block : content) {
            for(int i=0;i<block.length;i+=BYTES_PER_ITEM){
                byte[] itemData  = new byte[BYTES_PER_ITEM];
                System.arraycopy(block, i, itemData, 0, BYTES_PER_ITEM);
                Item item = new Item(itemData);
                if(item.isExist()){
                    children.add(item);
                }else{
                    break;
                }
            }
        }
    }

    public List<Item> getChildren(){
        return children;
    }
}
