package scau.os.soos.apps.mindmap.controller;

import scau.os.soos.apps.mindmap.service.SidebarService;
import scau.os.soos.apps.mindmap.service.sidebar.SidebarFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class SidebarController {
    private static SidebarController instance;
    private final SimpleIntegerProperty selectedTab = new SimpleIntegerProperty(0);
    private HBox sideBarTab;
    private BorderPane sidebar;
    private AnchorPane sidebarContent;
    private Button foldBtn;
    private Label title;
    private SidebarService current;
    private final Map<Integer, String> tabMap = new HashMap<>() {{
        put(0, "outline");
        put(1, "design");
    }};

    public static void init(HBox sideBarTab, BorderPane sidebar, AnchorPane sidebarContent) {
        if (null == instance) {
            instance = new SidebarController(sideBarTab, sidebar, sidebarContent);
        }
    }

    public static SidebarController getInstance() {
        return instance;
    }

    public void sync() {
        if (current != null && selectedTab.get() != -1) {
            current.sync();
        }
    }

    private SidebarController() {
    }

    private SidebarController(HBox sideBarTab, BorderPane sidebar, AnchorPane sidebarContent) {
        this.sideBarTab = sideBarTab;
        this.sidebar = sidebar;
        this.sidebarContent = sidebarContent;

        this.foldBtn = (Button) sidebar.lookup("#sidebar-fold-btn");
        this.title = (Label) sidebar.lookup("#sidebar-title");

        addListener();
        updateState(selectedTab.get());
    }

    private void addListener() {
        for (int i = 0; i < sideBarTab.getChildren().size(); i++) {
            Button btn = (Button) sideBarTab.getChildren().get(i);
            int finalI = i;
            btn.setOnAction(e -> {
                if (selectedTab.get() != finalI) {
                    selectedTab.set(finalI);
                    title.setText(btn.getText());
                } else selectedTab.set(-1);
            });
        }
        selectedTab.addListener((e) -> {
            if (selectedTab.get() == -1) {
                sidebar.setPrefWidth(0);
                sidebar.setVisible(false);
            } else {
                sidebar.setPrefWidth(294);
                sidebar.setVisible(true);
            }
            updateState(selectedTab.get());
        });
        foldBtn.setOnAction(e -> {
            selectedTab.set(-1);
        });
    }

    private void updateState(int index) {
        for (int i = 0; i < sideBarTab.getChildren().size(); i++) {
            if (index == i) {
                current = SidebarFactory.getInstance().getService(tabMap.get(selectedTab.get()));
                Node content = current.render();

                sideBarTab.getChildren().get(i).getStyleClass().add("active");
                sidebarContent.getChildren().clear();

                AnchorPane.setTopAnchor(content, 0.0);
                AnchorPane.setRightAnchor(content, 0.0);
                AnchorPane.setBottomAnchor(content, 0.0);
                AnchorPane.setLeftAnchor(content, 0.0);
                sidebarContent.getChildren().add(content);

            } else {
                sideBarTab.getChildren().get(i).getStyleClass().remove("active");
            }
        }

    }
}
