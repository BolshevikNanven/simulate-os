package scau.os.soos.apps.mindmap;

import scau.os.soos.apps.AppUtil;
import scau.os.soos.apps.mindmap.controller.SidebarController;
import scau.os.soos.apps.mindmap.service.*;
import scau.os.soos.apps.mindmap.service.layout.LayoutFactory;
import scau.os.soos.apps.mindmap.service.sidebar.SidebarFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import scau.os.soos.ui.components.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class MindMapApp extends Window implements Initializable {
    @FXML
    private BorderPane mindMapApp;
    @FXML
    private ScrollPane canvasContainer;
    @FXML
    private Pane canvas;
    @FXML
    private BorderPane toolbar;
    @FXML
    private HBox sideBarTab;
    @FXML
    private BorderPane sidebar;
    @FXML
    private AnchorPane sidebarContent;

    public MindMapApp() {
        super("思维导图", "apps/terminal/icon.png",1024, 640);
        AppUtil.loadFXML(this, "main-view.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        load(mindMapApp);

        SidebarFactory.init(sidebarContent);
        LayoutFactory.init(canvas);

        CanvasService.init(canvasContainer, canvas);
        UndoAndRedoService.init();
        LineService.init(canvas);
        NodeService.init(canvasContainer, canvas);
        ToolbarService.init(toolbar);

        SidebarController.init(sideBarTab, sidebar, sidebarContent);
    }
}