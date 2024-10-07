package scau.os.soos.apps.mindmap.service.sidebar;

import scau.os.soos.apps.mindmap.MindMapApp;
import scau.os.soos.apps.mindmap.service.NodeService;
import scau.os.soos.apps.mindmap.service.SidebarService;
import scau.os.soos.apps.mindmap.service.layout.LayoutFactory;
import scau.os.soos.apps.mindmap.store.SettingStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DesignService implements SidebarService {
    private final Parent root;
    private ChoiceBox<String> layoutChoiceBox;
    private ChoiceBox<String> lineChoiceBox;
    private Spinner<Integer> marginSpinner;
    private final String[] layoutNames = new String[]{"右侧树布局", "左侧树布局", "思维导图布局"};
    private final String[] layoutServices = new String[]{"RightTreeLayout", "LeftTreeLayout", "MindMapLayout"};
    private final String[] lineNames = new String[]{"直线", "折线", "曲线"};
    private final String[] lineServices = new String[]{"StraightLine", "TwoPolyLine", "CurveLine"};

    public DesignService() {
        FXMLLoader fxmlLoader = new FXMLLoader(MindMapApp.class.getResource("design-sidebar.fxml"));
        try {
            root = fxmlLoader.load();
            layoutChoiceBox = (ChoiceBox<String>) root.lookup("#layout-choice-box");
            lineChoiceBox = (ChoiceBox<String>) root.lookup("#line-choice-box");
            marginSpinner = (Spinner<Integer>) root.lookup("#margin-spinner");

            layoutChoiceBox.getItems().addAll(layoutNames);
            lineChoiceBox.getItems().addAll(lineNames);

            marginSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(16, 128, 56));

            sync();
            addListener();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addListener() {
        layoutChoiceBox.setOnAction(actionEvent -> {
            for (int i = 0; i < layoutNames.length; i++) {
                if (layoutChoiceBox.getValue().equals(layoutNames[i])) {
                    SettingStore.setLayout(layoutServices[i]);
                    SettingStore.setLayoutService(LayoutFactory.getInstance().getService(layoutServices[i]));
                    NodeService.getInstance().renderNodeTree();
                    break;
                }
            }

        });
        lineChoiceBox.setOnAction(actionEvent -> {
            for (int i = 0; i < lineNames.length; i++) {
                if (lineChoiceBox.getValue().equals(lineNames[i])) {
                    SettingStore.setLine(lineServices[i]);

                    NodeService.getInstance().renderNodeTree();
                    break;
                }
            }
        });
        marginSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
            SettingStore.setMarginH(t1);

            SettingStore.getLayoutService().layout();
        });
    }

    @Override
    public Node render() {
        return root;
    }

    @Override
    public void sync() {
        for (int i = 0; i < lineNames.length; i++) {
            if (layoutServices[i].equals(SettingStore.getLayout())) {
                layoutChoiceBox.getSelectionModel().select(i);
            }
            if (lineServices[i].equals(SettingStore.getLine())) {
                SettingStore.setLine(lineServices[i]);
                lineChoiceBox.getSelectionModel().select(i);
            }
        }
    }
}
