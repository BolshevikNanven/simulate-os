package scau.os.soos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.common.OS;
import scau.os.soos.ui.DesktopManager;
import scau.os.soos.ui.TaskBarManager;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainUIController implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private Label time;
    @FXML
    private Label date;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm:ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GlobalUI.rootNode = root;
        setupUI();
    }

    private void setupUI() {
        OS.clock.bind(() ->
                Platform.runLater(() -> {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    time.setText(timeFormatter.format(localDateTime));
                    date.setText(dateFormatter.format(localDateTime));
                })
        );
        TaskBarManager.getInstance().init();
        DesktopManager.getInstance().init();
    }
}
