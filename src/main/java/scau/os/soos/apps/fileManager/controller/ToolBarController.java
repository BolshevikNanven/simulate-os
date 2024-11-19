package scau.os.soos.apps.fileManager.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.apps.fileManager.enums.SORT_TYPE;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.common.Clipboard;
import scau.os.soos.apps.fileManager.util.MatchUtil;
import scau.os.soos.apps.fileManager.util.TipUtil;
import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.common.exception.DiskSpaceInsufficientException;
import scau.os.soos.common.exception.IllegalPathException;
import scau.os.soos.common.exception.ItemAlreadyExistsException;
import scau.os.soos.common.exception.ItemNotFoundException;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.model.Txt;
import scau.os.soos.ui.components.Dialog;

import java.net.URL;
import java.util.*;

public class ToolBarController implements Initializable {
    @FXML
    public Button leftBtn;
    @FXML
    public Button rightBtn;
    @FXML
    public Button upBtn;
    @FXML
    public Button refreshBtn;

    @FXML
    public TextField currentDirectory;
    @FXML
    public Button goToBtn;
    @FXML
    public TextField searchTextField;
    @FXML
    public Button searchBtn;
    @FXML
    public Region searchIcon;

    private boolean searchState; // 判断是否处于搜索状态

    @FXML
    public MenuButton createBtn;
    @FXML
    public MenuItem createTxtBtn;
    @FXML
    public MenuItem createExeBtn;
    @FXML
    public MenuItem createDirectoryBtn;


    private boolean isShear;
    @FXML
    public Button shearBtn;
    @FXML
    public Button copyBtn;
    @FXML
    public Button pasteBtn;
    @FXML
    public Button reNameBtn;
    @FXML
    public Button deleteBtn;

    // 映射RadioMenuItem到SORT_TYPE
    private final Map<RadioMenuItem, SORT_TYPE> sortRuleMap = new HashMap<>();
    private final Map<RadioMenuItem, Boolean> sortOrderMap = new HashMap<>();
    private final Map<RadioMenuItem, FILE_TYPE> selectItemMap = new HashMap<>();


    @FXML
    public MenuButton sortItemMenu;
    @FXML
    public RadioMenuItem sortByNameItem;
    @FXML
    public RadioMenuItem sortByTypeItem;
    @FXML
    public RadioMenuItem sortBySizeItem;

    ToggleGroup sortRuleGroup = new ToggleGroup();

    @FXML
    public RadioMenuItem sortAscendingItem;
    @FXML
    public RadioMenuItem sortDescendingItem;

    ToggleGroup sortOrderGroup = new ToggleGroup();

    @FXML
    public MenuButton selectItemMenu;
    @FXML
    public RadioMenuItem selectAllItem;
    @FXML
    public RadioMenuItem selectTxtItem;
    @FXML
    public RadioMenuItem selectExeItem;
    @FXML
    public RadioMenuItem selectDirectoryItem;

    ToggleGroup selectItemGroup = new ToggleGroup();

    HashMap<Button, String> buttonToTooltipMap = new HashMap<>();

    private static ToolBarController instance;

    public static ToolBarController getInstance() {
        if (instance == null) {
            throw new RuntimeException("未初始化ToolBarController");
        }
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
    }

    public void init() {
        // 为后退按钮添加监听器
        addListenerForBackwardButton();
        // 为前进按钮添加监听器
        addListenerForForwardButton();
        // 为上级目录按钮添加监听器
        addListenerForUpButton();
        // 为刷新按钮添加监听器
        addListenerForRefreshButton();

        // 为当前目录文本框添加监听器
        addListenerForCurrentDirectoryTextField();
        // 为跳转到指定目录按钮添加监听器
        addListenerForGoToButton();
        // 为搜索文本框添加监听器
        addListenerForSearchTextField();
        // 为搜索按钮添加监听器
        addListenerForSearchButton();


        // 为创建按钮添加监听器
        addListenerForCreateButton();
        // 为创建文本文件按钮添加监听器
        addListenerForCreateTxtButton();
        // 为创建可执行文件按钮添加监听器
        addListenerForCreateExeButton();
        // 为创建目录按钮添加监听器
        addListenerForCreateDirectoryButton();


        // 为剪切按钮添加监听器
        addListenerForShearButton();
        // 为复制按钮添加监听器
        addListenerForCopyButton();
        // 为粘贴按钮添加监听器
        addListenerForPasteButton();
        // 为重命名按钮添加监听器
        addListenerForRenameButton();
        // 为删除按钮添加监听器
        addListenerForDeleteButton();

        // 初始化映射
        initializeMaps();
        // 添加排序菜单的监听器
        addListenerForSortMenu();
        // 添加选择菜单的监听器
        addListenerForSelectMenu();

        // 初始化提示
        initTooltip();
    }


    /**
     * 为后退按钮添加事件监听器。
     */
    private void addListenerForBackwardButton() {
        leftBtn.setOnAction(e -> DirectoryTreeController.getInstance().stepBackward());
        // 绑定到路径指针属性上，当路径指针小于等于0时，按钮被禁用
        leftBtn.disableProperty().bind(
                DirectoryTreeController.getInstance().getPathPointerProperty()
                        .lessThanOrEqualTo(0));
    }

    /**
     * 为前进按钮添加事件监听器。
     */
    private void addListenerForForwardButton() {
        rightBtn.setOnAction(e -> DirectoryTreeController.getInstance().stepForward());

        DirectoryTreeController controller = DirectoryTreeController.getInstance();
        // 如果路径指针大于或等于路径列表大小减1（表示最后一个元素的索引），或者路径指针小于0
        // 则按钮应该被禁用
        rightBtn.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        controller.getPathPointerProperty().get() >= controller.getPathListProperty().size() - 1
                                ||
                                controller.getPathPointerProperty().get() < 0,
                controller.getPathPointerProperty(),
                controller.getPathListProperty()));
    }

    /**
     * 为上移按钮添加事件监听器。
     */
    private void addListenerForUpButton() {
        upBtn.setOnAction(e -> DirectoryTreeController.getInstance().upDirectory());

        // 获取目录树的选中项模型
        MultipleSelectionModel<TreeItem<Item>> selectionModel = DirectoryTreeController.getInstance().directoryTree.selectionModelProperty().getValue();
        // 确保只有当选中项不是根项时，上移按钮才可用
        upBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            TreeItem<Item> value = selectionModel.getSelectedItem();
            return value == null ||
                    value.getParent() == null ||
                    value.getParent().getParent() == null;
        }, selectionModel.selectedItemProperty()));
    }

    /**
     * 为刷新按钮添加事件监听器。
     */
    private void addListenerForRefreshButton() {
        refreshBtn.setOnAction(e -> {
            // 刷新目录树
            DirectoryTreeController.getInstance().refreshCurrentDirectory();
            // 刷新文件列表
            FileManagerApp.getInstance().refreshCurrentDirectory();
        });
    }


    /**
     * 为当前目录文本框添加事件监听器。
     */
    private void addListenerForCurrentDirectoryTextField() {
        currentDirectory.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // 如果currentDirectory失去了焦点，并且goToBtn也没有获得焦点
            if (!newValue && !goToBtn.isFocused()) {
                // 根据当前目录项更新currentDirectory的文本内容
                Item directory = DirectoryTreeController.getInstance().getCurrentDirectory();
                currentDirectory.setText(directory == null ? null : directory.getPath());
            }
        });
        // 回车跳转到指定目录
        currentDirectory.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                handleGoToButtonClick();
            }
        });
    }

    /**
     * 为跳转到指定目录按钮添加事件监听器。
     */
    private void addListenerForGoToButton() {
        goToBtn.setOnAction(e -> handleGoToButtonClick());
    }

    private void handleGoToButtonClick() {
        String path = currentDirectory.getText();

        Item target = null;

        try {
            target = FileController.getInstance().findItem(path, FILE_TYPE.DIRECTORY);
            // 如果找到了目标目录，则跳转到该目录
            DirectoryTreeController.getInstance().goToDirectory(target);
        } catch (ItemNotFoundException e) {
            String errorMessage = "找不到:\"" + (path == null ? "" : path) + "\", 请检查拼写并重试";
            Label label = new Label(errorMessage);
            Item directory = DirectoryTreeController.getInstance().getCurrentDirectory();
            // 显示对话框, 将文本框内容设置为当前目录的路径
            Dialog.getDialog(FileManagerApp.getInstance(),
                    errorMessage,
                    true,
                    false,
                    confirm -> currentDirectory.setText(
                            directory == null ? null : directory.getPath()),
                    cancelOrClose -> currentDirectory.setText(
                            directory == null ? null : directory.getPath()),
                    null).show();
        }
    }

    public void showCurrentDirectory(String directory) {
        // 目录树调用
        // 显示当前目录
        currentDirectory.setText(directory); // 绝对路径
    }

    /**
     * 为搜索文本框添加事件监听器。
     */
    private void addListenerForSearchTextField() {
        searchTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // 如果searchTextField失去了焦点，并且searchBtn也没有获得焦点
            if (!newValue && !searchBtn.isFocused()) {
                // 搜索结束
                unSearch(true);
            }
        });
        // 回车搜索
        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                handleSearchButtonClick();
            }
        });
    }

    /**
     * 为搜索按钮添加事件监听器。
     */
    private void addListenerForSearchButton() {
        searchBtn.setOnAction(e -> handleSearchButtonClick());
    }

    public void resetSearchButton() {
        unSearch(false);
    }

    private void handleSearchButtonClick() {
        // 判断是否处于搜索状态 true 正在搜索态 false 非搜索态
        if (!searchState) {
            search();
        } else {
            unSearch(true);
        }
    }

    private void search() {
        // 获取用户输入
        String searchName = searchTextField.getText();
        // 搜索--遍历当前图片列表 计算编辑距离 重置imageModelList 并重新调用displayThumbnail
        List<Item> itemList = FileManagerApp.getInstance().getItemList();
        List<Item> searchResult = new ArrayList<>();
        Map<Item, Double> matchLevelMap = new HashMap<>();
        boolean heightMatch = false;
        for (Item item : itemList) {
            // 获取图片名称(不算后缀)
            String name = item.getName();
            int lastDotIndex = name.lastIndexOf(".");
            if (lastDotIndex != -1) {
                name = name.substring(0, lastDotIndex);
            }
            int maxCommonSubstring = MatchUtil.longestCommonSubstring(name, searchName);
            // 计算匹配水平
            double level = MatchUtil.matchLevel(name, searchName, 1, 4, 50);
            // 判断高度相似
            if (maxCommonSubstring > searchName.length() / 2) {
                heightMatch = true;
            }
            // 判断相关度
            if (maxCommonSubstring > 0 && (!heightMatch || maxCommonSubstring > searchName.length() / 2)) {
                // 做imageModel和level映射
                matchLevelMap.put(item, level);
                // 添加到搜索结果列表
                searchResult.add(item);
            }
        }
        // 按编辑距离升序排序搜索结果列表
        searchResult.sort((o1, o2) -> {
            double l1 = matchLevelMap.get(o1);
            double l2 = matchLevelMap.get(o2);
            return Double.compare(l1, l2);
        });
        // 加载搜索结果列表
        FileManagerApp.getInstance().setItemList(searchResult);
        FileManagerApp.getInstance().displayItem();

        // 按钮变成取消搜索（图标）图标后续更改 2024/3/25 css  // 搜所和功能改变的顺序要测试考虑！！！
        searchState = true;

        searchTextField.setDisable(true);

        searchIcon.getStyleClass().remove("search-icon");
        searchIcon.getStyleClass().add("cancel-icon");

        TipUtil.setTooltip(searchBtn, "cancel");
    }

    public void unSearch(boolean isRefresh) {
        if (isRefresh) {
            // 取消搜索--重新加载当前目录
            FileManagerApp.getInstance().refreshCurrentDirectory();
        }
        // 按钮变成搜索（图标）图标后续更改 2024/3/25 css
        searchState = false;

        searchTextField.setText("");
        searchTextField.setDisable(false);

        searchIcon.getStyleClass().remove("cancel-icon");
        searchIcon.getStyleClass().add("search-icon");

        TipUtil.setTooltip(searchBtn, "search");
    }

    private void addListenerForCreateButton() {
        createBtn.disableProperty().bind(DirectoryTreeController.getInstance().getCurrentDirectoryProperty().isNull());
    }


    /**
     * 为创建txt文件按钮添加事件监听器。
     */
    private void addListenerForCreateTxtButton() {
        createTxtBtn.setOnAction(e -> {
            Item cur = DirectoryTreeController.getInstance().getCurrentDirectory();
            if (cur == null) {
                return;
            }

            try {
                String filePath = generateUniqueFilePath(cur.getPath(), FILE_TYPE.TXT, "t", ".t");
                FileController.getInstance().createFile(filePath);
                FileManagerApp.getInstance().refreshCurrentDirectory();
            } catch (RuntimeException ex) {
                Dialog.getDialog(FileManagerApp.getInstance(), "请重命名部分文件",
                        true, false,
                        null, null,
                        null);
            } catch (DiskSpaceInsufficientException ex) {
                Dialog.getDialog(FileManagerApp.getInstance(), "磁盘空间不足，请清理部分空间后重试",
                        true, false,
                        null, null,
                        null);
            } catch (ItemAlreadyExistsException | ItemNotFoundException | IllegalPathException ex) {
                Dialog.getEmptyDialog(FileManagerApp.getInstance(), "error!!!");
            }
        });
    }

    /**
     * 为创建exe文件按钮添加事件监听器。
     */
    private void addListenerForCreateExeButton() {
        createExeBtn.setOnAction(e -> {
            Item cur = DirectoryTreeController.getInstance().getCurrentDirectory();
            if (cur == null) {
                return;
            }

            try {
                String filePath = generateUniqueFilePath(cur.getPath(), FILE_TYPE.EXE, "e", ".e");
                FileController.getInstance().createFile(filePath);
                FileManagerApp.getInstance().refreshCurrentDirectory();
            } catch (RuntimeException ex) {
                Dialog.getDialog(FileManagerApp.getInstance(), "请重命名部分文件",
                        true, false,
                        null, null,
                        null);
            } catch (DiskSpaceInsufficientException ex) {
                Dialog.getDialog(FileManagerApp.getInstance(), "磁盘空间不足，请清理部分空间后重试",
                        true, false,
                        null, null,
                        null);
            } catch (ItemAlreadyExistsException | ItemNotFoundException | IllegalPathException ex) {
                Dialog.getEmptyDialog(FileManagerApp.getInstance(), "error!!!");
            }
        });
    }


    /**
     * 为创建目录按钮添加事件监听器。
     */
    private void addListenerForCreateDirectoryButton() {
        createDirectoryBtn.setOnAction(e -> {
            Item cur = DirectoryTreeController.getInstance().getCurrentDirectory();
            if (cur == null) {
                return;
            }

            try {
                String filePath = generateUniqueFilePath(cur.getPath(), FILE_TYPE.DIRECTORY, "d", "");
                FileController.getInstance().createDirectory(filePath);
                FileManagerApp.getInstance().refreshCurrentDirectory();
            } catch (RuntimeException ex) {
                Dialog.getDialog(FileManagerApp.getInstance(), "请重命名部分文件!",
                        true, false,
                        null, null,
                        null);
            } catch (DiskSpaceInsufficientException ex) {
                Dialog.getDialog(FileManagerApp.getInstance(), "磁盘空间不足，请清理部分空间后重试",
                        true, false,
                        null, null,
                        null);
            } catch (ItemAlreadyExistsException | ItemNotFoundException ex) {
                Dialog.getEmptyDialog(FileManagerApp.getInstance(), "error!!!");
            }
        });
    }


    /**
     * 生成一个唯一的文件路径。
     *
     * @param dir       目录路径，文件将被保存在此目录下。
     * @param type      文件类型，用于文件查找时的类型匹配。
     * @param name      文件的基础名称，不包含扩展名。
     * @param extension 文件的扩展名，不包含点号（.）。
     * @return 返回生成的唯一文件路径。
     * @throws RuntimeException 如果达到最大重试次数仍无法生成唯一文件名，则抛出此异常。
     */
    private String generateUniqueFilePath(String dir, FILE_TYPE type, String name, String extension) {
        int counter = 0;
        String fileName = name + counter + extension; // 初始文件名
        while (true) {
            try {
                FileController.getInstance().findItem(dir + fileName, type);
            } catch (ItemNotFoundException e) {
                return dir + fileName;
            }

            if (counter > 99) {
                throw new RuntimeException("无法创建文件，已达到最大重试次数。");
            }
            counter++; // 计数器递增
            fileName = name + counter + extension; // 生成新的文件名
        }
    }

    /**
     * 为剪切按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForShearButton() {
        shearBtn.disableProperty().bind(FileManagerApp.getInstance().getSelectedCountProperty().lessThanOrEqualTo(0));

        shearBtn.setOnAction(e -> performClipboardOperation(true));
    }


    /**
     * 为复制按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForCopyButton() {
        copyBtn.disableProperty().bind(FileManagerApp.getInstance().getSelectedCountProperty().lessThanOrEqualTo(0));

        copyBtn.setOnAction(e -> performClipboardOperation(false));
    }


    private void performClipboardOperation(boolean isShear) {
        List<ThumbnailBox> selectedItems = FileManagerApp.getInstance().getSelectedList();
        this.isShear = isShear;

        if (!selectedItems.isEmpty()) {
            Clipboard.getInstance().copy(selectedItems);
        }
    }

    /**
     * 为粘贴按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForPasteButton() {
        pasteBtn.disableProperty().bind(DirectoryTreeController.getInstance().getCurrentDirectoryProperty().isNull()
                .or(Clipboard.getInstance().getCopiedItemsProperty().emptyProperty()));

        pasteBtn.setOnAction(e -> {
            List<ThumbnailBox> selectedItems = Clipboard.getInstance().getCopiedItems();
            String target = DirectoryTreeController.getInstance().getCurrentDirectory().getPath();

            for (ThumbnailBox item : selectedItems) {
                Item source = item.getItem();
                try {
                    if (isShear) {
                        FileController.getInstance().moveFile(source.getPath(), target);
                        Clipboard.getInstance().clear();
                    } else {
                        FileController.getInstance().copyFile(source.getPath(), target);
                    }
                } catch (ItemAlreadyExistsException ex) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("正在将").append(selectedItems.size()).append("个项目从 ")
                            .append(Clipboard.getInstance().getSourcePath()).append(" 复制到 ")
                            .append(target).append("\n目标已包含一个名为\"").append(source.getFullName()).append("\"的文件\n\t是否要替换它?");
                    Label message = new Label(sb.toString());
                    Dialog.getDialog(FileManagerApp.getInstance(), sb.toString(),
                            true, true,
                            confirm -> {
                                try {
                                    FileController.getInstance().deleteFile(target + "/" + source.getFullName());
                                    if (isShear) {
                                        FileController.getInstance().moveFile(source.getPath(), target);
                                    } else {
                                        FileController.getInstance().copyFile(source.getPath(), target);
                                    }
                                } catch (Exception exc) {
                                    Dialog.getEmptyDialog(FileManagerApp.getInstance(), "Error!!!").show();
                                }
                            }, null,
                            null).show();
                } catch (DiskSpaceInsufficientException ex) {
                    Dialog.getDialog(FileManagerApp.getInstance(), "磁盘空间不足，请清理部分空间后重试",
                            true, false,
                            null, null,
                            null);
                } catch (IllegalPathException ex) {
                    Dialog.getEmptyDialog(FileManagerApp.getInstance(), "IllegalPathException!!!").show();
                } catch (ItemNotFoundException ex) {
                    Dialog.getEmptyDialog(FileManagerApp.getInstance(), "ItemNotFoundException!!!").show();
                }
            }

            DirectoryTreeController.getInstance().refreshCurrentDirectory();
            FileManagerApp.getInstance().refreshCurrentDirectory();
        });
    }

    /**
     * 为重命名按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForRenameButton() {
        reNameBtn.disableProperty().bind(FileManagerApp.getInstance().getSelectedCountProperty().lessThanOrEqualTo(0));
        reNameBtn.setOnAction(e -> {
            List<ThumbnailBox> selectedItems = FileManagerApp.getInstance().getSelectedList();
            if (selectedItems.isEmpty()) {
                return;
            }
            ThumbnailBox targetItem = selectedItems.get(0);
            targetItem.startRenaming();
        });
    }

    /**
     * 为删除按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForDeleteButton() {
        deleteBtn.disableProperty().bind(FileManagerApp.getInstance().getSelectedCountProperty().lessThanOrEqualTo(0));
        deleteBtn.setOnAction(e -> {
            List<ThumbnailBox> selectedItems = FileManagerApp.getInstance().getSelectedList();
            if (selectedItems.isEmpty()) {
                return;
            }
            if (selectedItems.size() == 1) {
                handleDeleteItem(selectedItems.getFirst());
            } else {
                handleDeleteItems(selectedItems);
            }
        });
    }

    private void handleDeleteItem(ThumbnailBox selectItemBox) {
        Item item = selectItemBox.getItem();
        String message = "确定要永久性地删除此文件" + (item.getType() == 0 ? "夹" : "") + "吗？";
        Label label = new Label(message);
        HBox hbox = new HBox();
        ImageView imageView = new ImageView(selectItemBox.getImagePath());
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        StringBuilder itemInfo = new StringBuilder();
        itemInfo.append("名称:\t").append(item.getFullName()).append("\n");
        if (item.getType() != 0) {
            itemInfo.append("类型:\t").append((char) item.getType()).append("\n");
        }
        itemInfo.append("大小:\t").append(item.getSize()).append(" byte\n");
        Label label1 = new Label(itemInfo.toString());

        hbox.getChildren().addAll(imageView, label1);
        hbox.setPadding(new Insets(10, 0, 0, 0));
        hbox.setSpacing(10);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 10, 30));
        borderPane.setTop(label);
        borderPane.setCenter(hbox);

        // 显示对话框, 将文本框内容设置为当前目录的路径
        Dialog.getDialog(FileManagerApp.getInstance(),
                "删除文件" + (item.getType() == 0 ? "夹" : ""),
                true,
                true,
                confirm -> {
                    deleteSpecificItem(item);
                    FileManagerApp.getInstance().refreshCurrentDirectory();
                },
                null,
                borderPane).show();
    }

    private void handleDeleteItems(List<ThumbnailBox> selectedItems) {
        String message = "确定要永久性地删除这 " + selectedItems.size() + " 项吗？";
        Label label = new Label(message);

        // 显示对话框, 将文本框内容设置为当前目录的路径
        Dialog.getDialog(FileManagerApp.getInstance(),
                message,
                true,
                true,
                confirm -> {
                    for (ThumbnailBox itemBox : selectedItems) {
                        Item item = itemBox.getItem();
                        if (item == null) {
                            continue;
                        }
                        deleteSpecificItem(item);
                    }
                    FileManagerApp.getInstance().refreshCurrentDirectory();
                },
                null,
                null).show();
    }

    private void deleteSpecificItem(Item item) {
        try {
            if (item instanceof Directory) {
                FileController.getInstance().deleteDirectory(item.getPath());
                DirectoryTreeController.getInstance().refreshCurrentDirectory(item);
            } else if (item instanceof Txt || item instanceof Exe) {
                FileController.getInstance().deleteFile(item.getPath());
            }
        } catch (Exception e) {
            Dialog.getEmptyDialog(FileManagerApp.getInstance(), "ItemNotFoundException").show();
        }
    }


    /**
     * 为排序菜单项添加事件监听器。
     */
    private void addListenerForSortMenu() {
        sortItemMenu.disableProperty().bind(DirectoryTreeController.getInstance().getCurrentDirectoryProperty().isNull());

        sortByNameItem.setToggleGroup(sortRuleGroup);
        sortByTypeItem.setToggleGroup(sortRuleGroup);
        sortBySizeItem.setToggleGroup(sortRuleGroup);

        sortAscendingItem.setToggleGroup(sortOrderGroup);
        sortDescendingItem.setToggleGroup(sortOrderGroup);

        // 添加一个监听器到ToggleGroup，以便在用户更改排序规则或顺序时应用排序
        sortRuleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                applySort());
        sortOrderGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                applySort());
    }

    // 初始化映射
    private void initializeMaps() {
        sortRuleMap.put(sortByNameItem, SORT_TYPE.SORT_BY_NAME);
        sortRuleMap.put(sortByTypeItem, SORT_TYPE.SORT_BY_TYPE);
        sortRuleMap.put(sortBySizeItem, SORT_TYPE.SORT_BY_SIZE);

        sortOrderMap.put(sortAscendingItem, true);
        sortOrderMap.put(sortDescendingItem, false);

        selectItemMap.put(selectAllItem, null);
        selectItemMap.put(selectTxtItem, FILE_TYPE.TXT);
        selectItemMap.put(selectExeItem, FILE_TYPE.EXE);
        selectItemMap.put(selectDirectoryItem, FILE_TYPE.DIRECTORY);
    }

    public void resetSortMenu() {
        sortByNameItem.setSelected(true);
        sortByTypeItem.setSelected(false);
        sortBySizeItem.setSelected(false);
        sortAscendingItem.setSelected(true);
        sortDescendingItem.setSelected(false);
    }

    private void applySort() {
        RadioMenuItem selectedSortRule = (RadioMenuItem) sortRuleGroup.getSelectedToggle();
        RadioMenuItem selectedSortOrder = (RadioMenuItem) sortOrderGroup.getSelectedToggle();
        if (selectedSortRule == null || selectedSortOrder == null) {
            return;
        }
        SORT_TYPE sortType = sortRuleMap.get(selectedSortRule);
        boolean ascending = sortOrderMap.get(selectedSortOrder);

        if (sortType != null) {
            sortItemList(sortType, ascending);
        }
    }

    private void sortItemList(SORT_TYPE filterType, boolean ascending) {
        List<Item> itemList = FileManagerApp.getInstance().getItemList();
        Comparator<Item> comparator = switch (filterType) {
            case SORT_BY_NAME -> Comparator.comparing(Item::getName);
            case SORT_BY_TYPE -> Comparator.comparing(this::getTypeComparatorKey);
            case SORT_BY_SIZE -> Comparator.comparingLong(Item::getSize);
        };
        if (comparator != null) {
            itemList.sort(ascending ? comparator : comparator.reversed());
            FileManagerApp.getInstance().displayItem();
        }
    }

    // 辅助方法，用于类型排序的比较键
    private int getTypeComparatorKey(Item item) {
        if (item instanceof Txt) {
            return FILE_TYPE.valueOf("TXT").ordinal();
        } else if (item instanceof Exe) {
            return FILE_TYPE.valueOf("EXE").ordinal();
        } else if (item instanceof Directory) {
            return FILE_TYPE.valueOf("DIRECTORY").ordinal();
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 为选择菜单项添加事件监听器。
     */
    private void addListenerForSelectMenu() {
        selectItemMenu.disableProperty().bind(DirectoryTreeController.getInstance().getCurrentDirectoryProperty().isNull());

        selectAllItem.setToggleGroup(selectItemGroup);
        selectTxtItem.setToggleGroup(selectItemGroup);
        selectExeItem.setToggleGroup(selectItemGroup);
        selectDirectoryItem.setToggleGroup(selectItemGroup);

        selectAllItem.setSelected(true);

        // 添加一个监听器到ToggleGroup，以便在用户更改选择时过滤文件列表
        selectItemGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                applySelectionFilter());
    }

    public void resetSelectMenu() {
        selectAllItem.setSelected(true);
    }

    private void applySelectionFilter() {
        RadioMenuItem selectedItem = (RadioMenuItem) selectItemGroup.getSelectedToggle();
        if (selectedItem == null)
            return;
        if (selectedItem == selectAllItem) {
            FileManagerApp.getInstance().refreshCurrentDirectory();
            return;
        }

        FILE_TYPE filterType = selectItemMap.get(selectedItem);

        if (filterType != null) {
            FileManagerApp.getInstance().refreshCurrentDirectory();
            filterItemList(filterType);
        }
    }

    private void filterItemList(FILE_TYPE filterType) {
        // 临时列表来存储过滤后的项目
        List<Item> itemList = FileManagerApp.getInstance().getItemList();
        List<Item> filteredItemList = new ArrayList<>();
        switch (filterType) {
            case TXT:
                for (Item item : itemList) {
                    if (item instanceof Txt) {
                        filteredItemList.add(item);
                    }
                }
                break;
            case EXE:
                for (Item item : itemList) {
                    if (item instanceof Exe) {
                        filteredItemList.add(item);
                    }
                }
                break;
            case DIRECTORY:
                for (Item item : itemList) {
                    if (item instanceof Directory) {
                        filteredItemList.add(item);
                    }
                }
                break;
        }
        // 加载搜索结果列表
        FileManagerApp.getInstance().setItemList(filteredItemList);
        FileManagerApp.getInstance().displayItem();
    }

    // 初始化提示框
    private void initTooltip() {
        buttonToTooltipMap.put(leftBtn, "StepBackward");
        buttonToTooltipMap.put(rightBtn, "StepForward");
        buttonToTooltipMap.put(upBtn, "UpDirectory");
        buttonToTooltipMap.put(refreshBtn, "Refresh");
        buttonToTooltipMap.put(goToBtn, "GoToDirectory");
        buttonToTooltipMap.put(searchBtn, "Search");
        buttonToTooltipMap.put(shearBtn, "Shear");
        buttonToTooltipMap.put(copyBtn, "Copy");
        buttonToTooltipMap.put(pasteBtn, "Paste");
        buttonToTooltipMap.put(reNameBtn, "ReName");
        buttonToTooltipMap.put(deleteBtn, "Delete");

        for (HashMap.Entry<Button, String> entry : buttonToTooltipMap.entrySet()) {
            TipUtil.setTooltip(entry.getKey(), entry.getValue());
        }
    }
}
