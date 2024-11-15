package scau.os.soos.module.terminal;

public interface Operation <T,R>{
    R execute(T t);
}
