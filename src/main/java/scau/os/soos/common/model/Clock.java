package scau.os.soos.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// 全局系统时钟
public class Clock {
    private final AtomicInteger clock;

    private final List<Handler> listeners;

    public Clock() {
        clock = new AtomicInteger(0);
        listeners = new ArrayList<>();
    }

    // 监听时钟更新
    public void bind(Handler handler) {
        listeners.add(handler);
    }

    public void unBind(Handler handler) {
        listeners.remove(handler);
    }

    public void inc() {
        for (Handler listener : listeners) {
            listener.handle();
        }

        clock.incrementAndGet();
    }

    public int get() {
        return clock.get();
    }
}
