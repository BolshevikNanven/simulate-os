package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.List;

public class Fat {
    private final int[] fat;
    private static  int BLOCKS_PER_DISK = 256;
    public Fat() {
        this.fat = new int[BLOCKS_PER_DISK];
    }

    public int[] getFat() {
        return fat;
    }

    public static int getBlocksPerDisk() {
        return BLOCKS_PER_DISK;
    }

    public static void setBlocksPerDisk(int blocksPerDisk) {
        BLOCKS_PER_DISK = blocksPerDisk;
    }
    public boolean isEmptyDisk(int diskNum){
       if(getFat()[diskNum]==0){
           return true;
       }
       return false;
    }
}
