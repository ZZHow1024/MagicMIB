package com.zzhow.magicmibbackend.ui.controller;

import com.zzhow.magicmibbackend.ui.service.WebService;
import com.zzhow.magicmibbackend.ui.service.impl.WebServiceImpl;
import com.zzhow.magicmibbackend.ui.window.AboutWindow;
import com.zzhow.magicmibbackend.util.MessageBoxUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * 主窗口控制类
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/23
 */
public class MainController {
    // 服务是否已启动
    private boolean serviceIsStarted = false;
    // Web 服务
    private final WebService webService = new WebServiceImpl();

    @FXML
    private Label urlLabel;

    @FXML
    private TextField portTextField;

    @FXML
    private CheckBox allowCheckBox;

    @FXML
    private Button startServiceButton;

    @FXML
    private void onStartOrStopServiceClicked() {
        if (serviceIsStarted) {
            portTextField.setDisable(false);
            allowCheckBox.setDisable(false);
            webService.stopService();
            serviceIsStarted = false;
            startServiceButton.setText("启动服务");
            urlLabel.setText("服务未启动");
            MessageBoxUtil.success("停止成功", "MagicMIB 服务停止成功");

            return;
        }

        byte i = webService.startService(portTextField.getText(), allowCheckBox.isSelected());
        switch (i) {
            case 0 -> {
                portTextField.setDisable(true);
                allowCheckBox.setDisable(true);
                serviceIsStarted = true;
                startServiceButton.setText("停止服务");
                urlLabel.setText("Web页面：http://localhost:" + portTextField.getText());
                MessageBoxUtil.success("启动成功", "MagicMIB 服务启动成功");
            }
            case 1 -> MessageBoxUtil.error("端口号错误", "端口号应为 1～65535 的整数");
            case 2 -> MessageBoxUtil.error("端口号被占用", "请尝试更换端口号");
        }
    }

    @FXML
    private void onButtonAboutClicked() {
        AboutWindow.open();
    }

    @FXML
    private void onExitClicked() {
        System.exit(0);
    }
}
