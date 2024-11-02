package scau.os.soos.module.memory.model;

public class MemoryBlock {
    private int pid;
    private int address;
    private int size;
    private boolean isFree;
    private MemoryBlock pre;
    private MemoryBlock next;

    private MemoryBlock() {

    }
    public MemoryBlock(int size, boolean isFree) {
        this.size = size;
        this.isFree = isFree;
    }
    public MemoryBlock(int address, int size, boolean isFree) {
        this.address = address;
        this.size = size;
        this.isFree = isFree;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public void setPre(MemoryBlock pre) {
        this.pre = pre;
    }

    public void setNext(MemoryBlock next) {
        this.next = next;
    }

    public int getPid() {
        return pid;
    }

    public int getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }

    public boolean isFree() {
        return isFree;
    }

    public MemoryBlock getPre() {
        return pre;
    }

    public MemoryBlock getNext() {
        return next;
    }
}
