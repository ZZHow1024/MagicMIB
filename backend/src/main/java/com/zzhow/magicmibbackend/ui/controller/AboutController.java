package com.zzhow.magicmibbackend.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * 关于窗口控制类
 *
 * @author ZZHow
 * create 2025/12/23
 * update 2025/12/23
 */
public class AboutController {

    @FXML
    private Label githubLabel;

    @FXML
    private Label websiteLabel;

    @FXML
    public void initialize() {
        // 为GitHub标签添加点击事件
        if (githubLabel != null) {
            githubLabel.setOnMouseClicked(event -> {
                openBrowser("https://github.com/ZZHow1024/MagicMIB");
            });
            // 设置鼠标悬停效果
            githubLabel.setOnMouseEntered(event -> {
                githubLabel.setStyle("-fx-text-fill: #165dff; -fx-underline: true; -fx-cursor: hand;");
            });
            githubLabel.setOnMouseExited(event -> {
                githubLabel.setStyle("-fx-text-fill: #165dff; -fx-underline: true;");
            });
        }

        // 为网站标签添加点击事件
        if (websiteLabel != null) {
            websiteLabel.setOnMouseClicked(event -> {
                openBrowser("https://www.zzhow.com");
            });
            // 设置鼠标悬停效果
            websiteLabel.setOnMouseEntered(event -> {
                websiteLabel.setStyle("-fx-text-fill: #165dff; -fx-underline: true; -fx-cursor: hand;");
            });
            websiteLabel.setOnMouseExited(event -> {
                websiteLabel.setStyle("-fx-text-fill: #6b758b; -fx-font-size: 14px;");
            });
        }
    }

    /**
     * 通过默认浏览器打开URL
     *
     * @param url 要打开的URL
     */
    private void openBrowser(String url) {
        try {
            // 检查是否支持Desktop
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(URI.create(url));
            } else {
                // 如果不支持Desktop，尝试使用系统命令
                Runtime runtime = Runtime.getRuntime();
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    runtime.exec("open " + url);
                } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                    runtime.exec("xdg-open " + url);
                }
            }
        } catch (IOException e) {
            System.err.println("无法打开浏览器: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
