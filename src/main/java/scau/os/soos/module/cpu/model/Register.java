package scau.os.soos.module.cpu.model;

public class Register {
    private int AX;
    private int PC;
    private int IR;
    private int PSW;

    public Register() {
        this.PSW = 0b000;
    }

    public int getPSW() {
        return PSW;
    }

    public void setPSW(int PSW) {
        this.PSW = PSW;
    }

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
