## APP窗口开发

### 示例  
```java
public class TerminalApp extends Window implements Initializable {
    @FXML
    private BorderPane TerminalApp;

    public TerminalApp() {
        super("终端", "main.fxml", 900, 520);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setup(TerminalApp);
    }
}

// 使用方法：新建窗口
TaskBarManager.getInstance().addTask(new TerminalApp())
```  
### 指南
1. 继承`Window`并实现`Initializable`
2. 调用`Window`父类构造方法。会自动载入fxml文件以及icon图标，载入完成后将执行`initialize`。参数：super(窗口名称，fxml文件名，窗口宽度，窗口高度)
3. 在`initialize`方法中首先调用`setup`方法。会将对应的示例在窗口中渲染出来
### 规范
- **所有resources文件需放置在与该类相同的目录名下**，创建窗口时会以此作为根目录。如：`java/scau.os.soos/apps/terminal/TerminalApp.java`对应目录`resources/scau.os.soos/apps/terminal/`
- 默认查找`icon.png`或`icon.jpg`作为图标
- **不要**在fxml文件中绑定controller
- setup中的实例一般为最外层的container