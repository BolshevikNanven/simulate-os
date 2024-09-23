package scau.os.soos.module.cpu.model;

public class Register {
    private int AX;
    private int PC;
    private int IR;

    public int getAX() {
        return AX;
    }

    public void setAX(int AX) {
        this.AX = AX;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getIR() {
        return IR;
    }

    public void setIR(int IR) {
        this.IR = IR;
    }
}
