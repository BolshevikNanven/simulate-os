package scau.os.soos.apps.terminal;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import scau.os.soos.MainApplication;
import scau.os.soos.ui.components.Window;

import java.io.IOException;

public class TerminalApp extends Window {
    public TerminalApp() {
        super("终端", "apps/terminal/icon.png", loadFXML());

    }

    private static BorderPane loadFXML() {
        BorderPane node;
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("apps/terminal/main.fxml"));
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return node;
    }
}
