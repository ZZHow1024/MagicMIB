package com.zzhow.magicmibbackend.ui.controller;

import com.zzhow.magicmibbackend.ui.service.WebService;
import com.zzhow.magicmibbackend.ui.service.impl.WebServiceImpl;
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
 * update 2025/12/7
 */
public class MainController {

    @FXML
    private void onExitClicked() {
        System.exit(0);
    }
}
