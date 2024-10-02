package scau.os.soos.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import scau.os.soos.MainApplication;
import scau.os.soos.common.enums.WINDOW_STATES;
import scau.os.soos.ui.TaskBarManager;

public class TaskButton extends Button {
    private Window window;
    private TaskMenu taskMenu;

    private TaskButton() {
    }

    public TaskButton(Window window) {
        super("", newIcon(window.getIconUrl()));
        this.getStyleClass().add("task-btn");

        this.window = window;
        this.taskMenu = new TaskMenu(window);

        addListener();
    }

    public Window getWindow() {
        return window;
    }

    private void addListener() {
        // 响应窗口状态来改变样式
        this.window.getStateProperty().addListener((observableValue, windowStates, t1) -> {
            if (t1 == WINDOW_STATES.ACTIVE) {
                this.getStyleClass().add("active");
            } else {
                this.getStyleClass().remove("active");
            }
        });
        // 选择task
        this.setOnAction(actionEvent -> {
            if (window.getState() == WINDOW_STATES.ACTIVE) {
                window.setStates(WINDOW_STATES.HIDE);
            } else {
                TaskBarManager.getInstance().selectTask(window);
            }
        });
        // 右键菜单
        this.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                taskMenu.render(this);
            }
        });
    }

    private static BorderPane newIcon(String url) {
        BorderPane borderPane = new BorderPane();
        borderPane.setMaxWidth(Double.MAX_VALUE);
        borderPane.setMaxHeight(Double.MAX_VALUE);
        BorderPane.setAlignment(borderPane, Pos.CENTER);

        Region topIndicator = new Region();
        Region bottomIndicator = new Region();

        topIndicator.setPrefHeight(3);
        bottomIndicator.setPrefHeight(3);
        bottomIndicator.setMaxWidth(Region.USE_PREF_SIZE);
        bottomIndicator.getStyleClass().add("task-indicator");

        ImageView imageView = new ImageView();
        imageView.setImage(new Image(MainApplication.class.getResource(url).toExternalForm()));

        imageView.getStyleClass().add("task-icon");
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);

        HBox bottomContainer = new HBox(bottomIndicator);
        bottomContainer.setAlignment(Pos.CENTER);

        borderPane.setTop(topIndicator);
        borderPane.setCenter(imageView);
        borderPane.setBottom(bottomContainer);

        return borderPane;
    }

}
