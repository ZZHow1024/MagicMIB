package com.zzhow.magicmibbackend.ui.window;

import com.zzhow.magicmibbackend.MagicMibApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * 主窗口类
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/7
 */
public class MainWindow extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("main-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 200);
        stage.setTitle("MagicMIB");
        stage.setScene(scene);
        Image icon = new Image(Objects.requireNonNull(MagicMibApplication.class.getResourceAsStream("/image/MagicMIB.png")));
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setOnHiding(windowEvent -> {
            System.exit(0);
        });
        stage.show();
    }

    public static void show() {
        launch();
    }
}