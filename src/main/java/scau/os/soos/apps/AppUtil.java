package scau.os.soos.apps;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import scau.os.soos.MainApplication;

import java.io.IOException;

public class AppUtil {
    public static <T extends Node> T loadFXML(String url){
        T node;
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(url));
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return node;
    }
}
