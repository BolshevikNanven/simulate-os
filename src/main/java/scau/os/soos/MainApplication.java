package scau.os.soos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scau.os.soos.common.OS;
import scau.os.soos.common.ThreadsPool;
import scau.os.soos.module.Module;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.memory.MemoryController;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.module.terminal.TerminalController;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        Module cpu = CpuController.getInstance();
        Module process = ProcessController.getInstance();
        Module memory = MemoryController.getInstance();
        Module device = DeviceController.getInstance();
        Module file = FileController.getInstance();
        Module terminal = TerminalController.getInstance();

        cpu.run();
        process.run();
        memory.run();
        device.run();
        file.run();
        terminal.run();

        //启动时钟
        ThreadsPool.run(() -> {
            while (true) {
                OS.clock.inc();
                //测试用延时
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    public static void main(String[] args) {
        launch();
    }
}