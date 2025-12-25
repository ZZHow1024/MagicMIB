package com.zzhow.magicmibbackend.ui.window;

import com.zzhow.magicmibbackend.MagicMibApplication;
import com.zzhow.magicmibbackend.ui.controller.AboutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * 关于窗口
 *
 * @author ZZHow
 * create 2025/12/23
 * update 2025/12/23
 */
public class AboutWindow {
    public static void open() {
        Stage stage = new Stage();
        stage.setTitle("MagicMIB - About");
        Image icon = new Image(Objects.requireNonNull(MagicMibApplication.class.getResourceAsStream("/image/MagicMIB.png")));
        stage.getIcons().add(icon);
        stage.setResizable(false);
        try {
            FXMLLoader loader = new FXMLLoader(AboutWindow.class.getResource("about-window.fxml"));
            loader.setController(new AboutController());
            Pane load = loader.load();
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
