package scau.os.soos.apps.mindmap.store;

import scau.os.soos.apps.mindmap.controller.SidebarController;
import scau.os.soos.apps.mindmap.entity.SettingEntity;
import scau.os.soos.apps.mindmap.service.LayoutService;
import scau.os.soos.apps.mindmap.service.layout.LayoutFactory;

public class SettingStore {
    static {
        setting = new SettingEntity();
        setLayout("RightTreeLayout");
        setLine("TwoPolyLine");
        setMarginH(56);
        layoutService = LayoutFactory.getInstance().getService("RightTreeLayout");
    }

    private static SettingEntity setting;
    private static LayoutService layoutService;
    public static SettingEntity getSetting(){
        return setting;
    }
    public static void setSetting(SettingEntity settingEntity){
        setting=settingEntity;
        layoutService=LayoutFactory.getInstance().getService(settingEntity.getLayout());
        layoutService.layout();
        SidebarController.getInstance().sync();
    }
    public static Integer getMarginH() {
        return setting.getMarginH();
    }

    public static void setMarginH(Integer marginH) {
        setting.setMarginH(marginH);
    }

    public static String getLayout() {
        return setting.getLayout();
    }

    public static void setLayout(String layout) {
        setting.setLayout(layout);
    }

    public static String getLine() {
        return setting.getLine();
    }

    public static void setLine(String line) {
        setting.setLine(line);
    }

    public static LayoutService getLayoutService() {
        return layoutService;
    }

    public static void setLayoutService(LayoutService layoutService) {
        SettingStore.layoutService = layoutService;
    }
}
