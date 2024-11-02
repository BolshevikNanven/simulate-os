package scau.os.soos.module.device;

import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.device.model.Device;
import scau.os.soos.module.device.model.DeviceQueue;
import scau.os.soos.module.device.model.Task;
import scau.os.soos.module.process.model.Process;

import java.util.ArrayList;
import java.util.List;

public class DeviceService {
    private final DeviceQueue deviceQueue;
    private final List<Device> devices;

    public DeviceService() {
        deviceQueue = new DeviceQueue(DEVICE_TYPE.A, DEVICE_TYPE.B, DEVICE_TYPE.C);
        devices = new ArrayList<>();

        devices.add(new Device(DEVICE_TYPE.A));
        devices.add(new Device(DEVICE_TYPE.A));
        devices.add(new Device(DEVICE_TYPE.B));
        devices.add(new Device(DEVICE_TYPE.B));
        devices.add(new Device(DEVICE_TYPE.B));
        devices.add(new Device(DEVICE_TYPE.C));
        devices.add(new Device(DEVICE_TYPE.C));
        devices.add(new Device(DEVICE_TYPE.C));
    }

    public void assignDevice(DEVICE_TYPE deviceType, int time, Process process) {
        Task task = new Task(process, time);
        Device freeDevice = getFreeDevice(deviceType);
        if (freeDevice == null) {
            deviceQueue.add(deviceType, task);
        } else {
            assignTask(freeDevice, task);
        }
    }

    public void checkDevice() {
        for (Device device : devices) {
            if (!device.isBusy()) {
                continue;
            }
            if (device.getTime() > 0) {
                device.setTime(device.getTime() - 1);
            } else {
                releaseDevice(device);
            }
        }
    }

    private void releaseDevice(Device device) {
        if (!CpuController.getInstance().requestInterrupt(INTERRUPT.IO, device.getProcess())) {
            return;
        }
        device.setBusy(false);
        assignTask(device);
    }

    private void assignTask(Device device) {
        Task task = deviceQueue.poll(device.getType());
        if (task == null) {
            return;
        }
        assignTask(device, task);
    }

    private void assignTask(Device device, Task task) {
        device.setProcess(task.getProcess());
        device.setTime(task.getTime());
        device.setBusy(true);
    }

    private Device getFreeDevice(DEVICE_TYPE deviceType) {
        for (Device device : devices) {
            if (!device.isBusy() && device.getType() == deviceType) {
                return device;
            }
        }
        return null;
    }

    public void printDevices() {
        for (Device device : devices) {
            if (device.isBusy()) {
                System.out.printf("设备%s  忙碌  剩余时间:%d \n", device.getType(), device.getTime());
            } else {
                System.out.printf("设备%s  空闲  \n", device.getType());
            }
        }
    }
}
