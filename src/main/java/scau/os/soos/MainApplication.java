package scau.os.soos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scau.os.soos.common.OS;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.common.ThreadsPool;
import scau.os.soos.common.enums.OS_STATES;
import scau.os.soos.module.Module;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.memory.MemoryController;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.module.terminal.TerminalController;
import scau.os.soos.ui.TaskBarManager;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

        stage.setOnCloseRequest((e) -> {
            OS.state = OS_STATES.STOPPED;
            System.out.println(OS.state);
            ThreadsPool.stop();
        });

        GlobalUI.scene = scene;
        GlobalUI.stage = stage;
        setupModule();
    }

    private void setupModule() {
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
            OS.state = OS_STATES.RUNNING;
            while (!OS.state.equals(OS_STATES.STOPPED)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (OS.state.equals(OS_STATES.PAUSE)) {
                    continue;
                }
                System.out.println("clock:" + OS.clock.get());
                OS.clock.inc();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}