package scau.os.soos.apps.mindmap;

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
import scau.os.soos.ui.components.base.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class MindMapApp extends Window {
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
        super("思维导图","main-view.fxml",1024, 640);
    }

    @Override
    public void initialize() {
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