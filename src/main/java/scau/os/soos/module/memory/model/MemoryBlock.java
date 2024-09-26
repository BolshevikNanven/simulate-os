package scau.os.soos.module.memory.model;

public class MemoryBlock {
    private int address;
    private int size;
    private int isFree;
    private int pid;
    private MemoryBlock pre;
    private MemoryBlock next;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public MemoryBlock getPre() {
        return pre;
    }

    public void setPre(MemoryBlock pre) {
        this.pre = pre;
    }

    public MemoryBlock getNext() {
        return next;
    }

    public void setNext(MemoryBlock next) {
        this.next = next;
    }
}
