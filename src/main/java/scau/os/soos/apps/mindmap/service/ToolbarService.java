package scau.os.soos.apps.mindmap.service;

import scau.os.soos.apps.mindmap.dao.NodeDao;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.store.SettingStore;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.util.CommandUtil;
import scau.os.soos.apps.mindmap.util.StyleUtil;
import scau.os.soos.apps.mindmap.view.NodeView;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Objects;

public class ToolbarService {
    private static ToolbarService instance;
    private NodeEntity node;
    private Button undoBtn;
    private Button redoBtn;
    private Slider scaleSlider;
    private ToggleButton boldBtn;
    private ToggleButton italicBtn;
    private ToggleButton underlineBtn;
    private Button decFontSizeBtn;
    private Label fontSizeLabel;
    private Button incFontSizeBtn;
    private MenuButton alignMenuBtn;
    private Region alignMenuIcon;
    private HBox alignMenuBtnContainer;
    private Region fontColorIcon;
    private ColorPicker fontColorPicker;
    private HBox fontColorPickerContainer;
    private ColorPicker backgroundColorPicker;
    private Region borderColorIcon;
    private ColorPicker borderColorPicker;
    private HBox borderColorPickerContainer;
    private Button deleteBtn;
    private MenuButton addMenuBtn;

    private ToolbarService() {
    }

    private ToolbarService(BorderPane toolbar) {
        this.undoBtn = (Button) toolbar.lookup("#undo-btn");
        this.redoBtn = (Button) toolbar.lookup("#redo-btn");
        this.scaleSlider = (Slider) toolbar.lookup("#scale-slider");
        this.boldBtn = (ToggleButton) toolbar.lookup("#bold-btn");
        this.italicBtn = (ToggleButton) toolbar.lookup("#italic-btn");
        this.underlineBtn = (ToggleButton) toolbar.lookup("#underline-btn");
        this.decFontSizeBtn = (Button) toolbar.lookup("#dec-font-size-btn");
        this.fontSizeLabel = (Label) toolbar.lookup("#font-size-label");
        this.incFontSizeBtn = (Button) toolbar.lookup("#inc-font-size-btn");

        this.alignMenuBtnContainer = (HBox) toolbar.lookup("#align-menu-btn-container");
        this.alignMenuBtn = (MenuButton) toolbar.lookup("#align-menu-btn");
        this.alignMenuIcon = (Region) toolbar.lookup("#align-menu-icon");

        this.fontColorPickerContainer = (HBox) toolbar.lookup("#font-color-picker-container");
        this.fontColorPicker = (ColorPicker) toolbar.lookup("#font-color-picker");
        this.fontColorIcon = (Region) toolbar.lookup("#font-color-icon");

        this.borderColorPickerContainer = (HBox) toolbar.lookup("#border-color-picker-container");
        this.borderColorPicker = (ColorPicker) toolbar.lookup("#border-color-picker");
        this.borderColorIcon = (Region) toolbar.lookup("#border-color-icon");

        this.backgroundColorPicker = (ColorPicker) toolbar.lookup("#background-color-picker");
        this.deleteBtn = (Button) toolbar.lookup("#delete-btn");
        this.addMenuBtn = (MenuButton) toolbar.lookup("#add-menu-btn");


        setDisable(true);
        addListener();
    }

    public static ToolbarService getInstance() {
        return instance;
    }

    public static void init(BorderPane toolbar) {
        if (null == instance) {
            instance = new ToolbarService(toolbar);
        }
    }

    private void addListener() {
        this.addMenuBtn.getItems().forEach(item -> {
            if (item.getId().equals("add-sub-menu-item")) {
                item.setOnAction(e -> {
                    NodeService.getInstance().addSubNode();
                });
            } else if (item.getId().equals("add-bro-menu-item")) {
                item.setOnAction(e -> {
                    NodeService.getInstance().addBroNode();
                });
            }
        });
        this.alignMenuBtnContainer.setOnMouseClicked(mouseEvent -> alignMenuBtn.show());
        this.alignMenuBtn.getItems().forEach(item -> {
            if (item.getId().equals("align-left-menu-item")) {

                item.setOnAction(e -> {
                    NodeEntity n = node;
                    Pos oldAlignment = n.getAlignment();
                    UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                        n.setAlignment((Pos) param);
                    }, Pos.CENTER_LEFT, oldAlignment));
                    syncState();
                });
            } else if (item.getId().equals("align-center-menu-item")) {

                item.setOnAction(e -> {
                    NodeEntity n = node;
                    Pos oldAlignment = n.getAlignment();
                    UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                        n.setAlignment((Pos) param);
                    }, Pos.CENTER, oldAlignment));
                    syncState();
                });
            } else if (item.getId().equals("align-right-menu-item")) {

                item.setOnAction(e -> {
                    NodeEntity n = node;
                    Pos oldAlignment = n.getAlignment();
                    UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                        n.setAlignment((Pos) param);
                    }, Pos.CENTER_RIGHT, oldAlignment));
                    syncState();
                });
            }
        });
        this.boldBtn.setOnAction(e -> {
            NodeEntity n = node;
            if (this.boldBtn.isSelected()) {
                UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                    n.setFont(StyleUtil.mergeFont(n.getFont(), (FontWeight) param));
                    syncState();
                }, FontWeight.BOLD, FontWeight.NORMAL));
            } else {
                UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                    n.setFont(StyleUtil.mergeFont(n.getFont(), (FontWeight) param));
                    syncState();
                }, FontWeight.NORMAL, FontWeight.BOLD));
            }
        });
        this.italicBtn.setOnAction(e -> {
            if (this.italicBtn.isSelected()) {
                node.setFont(StyleUtil.mergeFont(node.getFont(), FontPosture.ITALIC));
            } else node.setFont(StyleUtil.mergeFont(node.getFont(), FontPosture.REGULAR));
        });
        this.underlineBtn.setOnAction(e -> {
            NodeEntity n = node;
            if (this.underlineBtn.isSelected()) {
                UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                    n.setFontUnderline((Boolean) param);
                    syncState();
                }, true, false));
            } else {
                UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                    n.setFontUnderline((Boolean) param);
                    syncState();
                }, false, true));
            }
        });


        this.incFontSizeBtn.setOnAction(e -> {
            double size = Double.parseDouble(this.fontSizeLabel.getText());
            double hasNotIncreasedSize = size;
            NodeEntity n = node;
            if (size < 56) size++;
            UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                n.setFont(StyleUtil.mergeFont(n.getFont(), (double) param));
                syncState();
            }, size, hasNotIncreasedSize));


        });
        this.decFontSizeBtn.setOnAction(e -> {
            double size = Double.parseDouble(this.fontSizeLabel.getText());
            double hasNotDecreasedSize = size;
            NodeEntity n = node;
            if (size > 8) size--;
            UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                node.setFont(StyleUtil.mergeFont(node.getFont(), (double) param));
                syncState();
            }, size, hasNotDecreasedSize));

        });
        this.fontColorPickerContainer.setOnMouseClicked(mouseEvent -> fontColorPicker.show());
        this.fontColorPicker.setOnAction(actionEvent -> {
            NodeEntity n = node;
            Paint c = n.getColor();
            UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                n.setColor((Color) param);
                syncState();
            }, fontColorPicker.getValue(), c));
        });
        this.backgroundColorPicker.setOnAction(actionEvent -> {
            NodeEntity n = node;
            String c = StyleUtil.getBackgroundColor(n.getBackground());
            UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                n.setBackground(StyleUtil.newBackground((String) param));
                syncState();
            }, backgroundColorPicker.getValue().toString(), c));
        });
        this.borderColorPickerContainer.setOnMouseClicked(mouseEvent -> borderColorPicker.show());
        this.borderColorPicker.setOnAction(actionEvent -> {
            NodeEntity n = node;
            String c = StyleUtil.getBorderColor(n.getBorder());
            UndoAndRedoService.getInstance().execute(CommandUtil.generate(param -> {
                n.setBorder(StyleUtil.newBorder((String) param));
                syncState();
            }, borderColorPicker.getValue().toString(), c));
        });
        this.deleteBtn.setOnAction(e -> {
            NodeEntity node = SystemStore.getSelectedNode();
            NodeDao.deleteNode(node);
            syncState();
        });
        this.undoBtn.setOnAction(e -> {
            UndoAndRedoService.getInstance().undo();
        });
        this.redoBtn.setOnAction(e -> {
            UndoAndRedoService.getInstance().redo();
        });
        this.scaleSlider.valueProperty().bindBidirectional(SystemStore.canvasScaleProperty());
    }

    private void setDisable(Boolean state) {
        if (state) {
            this.boldBtn.setDisable(true);
            this.italicBtn.setDisable(true);
            this.underlineBtn.setDisable(true);
            this.decFontSizeBtn.setDisable(true);
            this.incFontSizeBtn.setDisable(true);
            this.alignMenuBtnContainer.setDisable(true);
            this.fontColorPickerContainer.setDisable(true);
            this.backgroundColorPicker.setDisable(true);
            this.borderColorPickerContainer.setDisable(true);

            this.fontSizeLabel.setDisable(true);
            this.deleteBtn.setDisable(true);
        } else {
            this.boldBtn.setDisable(false);
            this.italicBtn.setDisable(false);
            this.underlineBtn.setDisable(false);
            this.decFontSizeBtn.setDisable(false);
            this.incFontSizeBtn.setDisable(false);
            this.alignMenuBtnContainer.setDisable(false);
            this.fontColorPickerContainer.setDisable(false);
            this.backgroundColorPicker.setDisable(false);
            this.borderColorPickerContainer.setDisable(false);

            this.fontSizeLabel.setDisable(false);
            this.deleteBtn.setDisable(false);
        }

    }

    public void syncState() {
        NodeEntity selectedNode = SystemStore.getSelectedNode();

        if (selectedNode == null) {
            setDisable(true);
            return;
        }

        node = selectedNode;

        boolean underline = node.isFontUnderline();
        boolean bold = node.getFont().getStyle().contains("Bold");
        boolean italic = node.getFont().getStyle().contains("Italic");
        int fontSize = (int) node.getFont().getSize();
        String backgroundColor = StyleUtil.getBackgroundColor(node.getBackground());
        String borderColor = StyleUtil.getBorderColor(node.getBorder());
        String fontColor = node.getColor().toString();
        Pos alignment = node.getAlignment();

        this.boldBtn.setSelected(bold);
        this.underlineBtn.setSelected(underline);
        this.italicBtn.setSelected(italic);

        this.fontSizeLabel.setText(Integer.toString(fontSize));
        this.fontColorIcon.setBackground(StyleUtil.newBackground(fontColor));
        this.fontColorPicker.setValue(Color.valueOf(fontColor));
        this.backgroundColorPicker.setValue(Color.valueOf(backgroundColor));

        String borderOpacity = borderColor.substring(borderColor.length() - 2);
        if (Objects.equals(borderOpacity, "00")) {
            this.borderColorIcon.setBackground(StyleUtil.newBackground(backgroundColor));
        } else {
            this.borderColorIcon.setBackground(StyleUtil.newBackground(borderColor));
        }
        this.borderColorPicker.setValue(Color.valueOf(borderColor));

        switch (alignment) {
            case CENTER -> {
                this.alignMenuIcon.getStyleClass().removeAll("align-center-icon", "align-left-icon", "align-right-icon");
                this.alignMenuIcon.getStyleClass().addAll("align-center-icon");
            }
            case CENTER_LEFT -> {
                this.alignMenuIcon.getStyleClass().removeAll("align-center-icon", "align-left-icon", "align-right-icon");
                this.alignMenuIcon.getStyleClass().addAll("align-left-icon");
            }
            case CENTER_RIGHT -> {
                this.alignMenuIcon.getStyleClass().removeAll("align-center-icon", "align-left-icon", "align-right-icon");
                this.alignMenuIcon.getStyleClass().addAll("align-right-icon");
            }
        }

        setDisable(false);
    }

}
