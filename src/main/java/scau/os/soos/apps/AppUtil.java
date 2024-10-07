package scau.os.soos.apps;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import scau.os.soos.MainApplication;
import scau.os.soos.ui.components.Window;

import java.io.IOException;

public class AppUtil {
    public static void loadFXML(Window controller, String url) {
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(url));
            loader.setController(controller);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
