package scau.os.soos.module.device.model;

import scau.os.soos.common.enums.DEVICE_TYPE;

import java.util.*;

public class DeviceQueue {
    private final Map<DEVICE_TYPE, Queue<Task>> queueMap;

    public DeviceQueue(DEVICE_TYPE... deviceTypes) {
        queueMap = new HashMap<>();
        for (DEVICE_TYPE type : deviceTypes) {
            queueMap.put(type, new LinkedList<>());
        }
    }

    public Task poll(DEVICE_TYPE type) {
        return queueMap.get(type).poll();
    }

    public void add(DEVICE_TYPE type, Task process) {
        queueMap.get(type).add(process);
    }
    public List<Task> getItemList(DEVICE_TYPE type){
        return queueMap.get(type).stream().toList();
    }
}
