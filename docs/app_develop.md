## APP窗口开发

### 示例  
```java
public class TerminalApp extends Window{
    @FXML
    private TextField input;
    public TerminalApp() {
        super("终端", "main.fxml", 900, 520);
    }
    @Override
    protected void initialize() {
        input.setText("6");
    }
}

// 使用方法：新建窗口
TaskBarManager.getInstance().addTask(new TerminalApp())
```  
### 指南
1. 继承`Window`
2. 调用`Window`父类构造方法。参数：super(窗口名称，fxml文件名，窗口宽度，窗口高度)
3. 会自动载入fxml文件以及icon图标，载入完成后将执行`initialize`。
### 规范
- **所有resources文件需放置在与该类相同的目录名下**，创建窗口时会以此作为根目录。如：`java/scau.os.soos/apps/terminal/TerminalApp.java`对应目录`resources/scau.os.soos/apps/terminal/`
- 默认查找`icon.png`或`icon.jpg`作为图标
- **不要**在fxml文件中绑定controller
- fxml中的最外层container需设置`fx:id="body"`