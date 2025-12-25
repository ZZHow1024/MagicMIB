package com.zzhow.magicmibbackend.util;

import com.zzhow.magicmibbackend.MagicMibApplication;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * 信息框工具类
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/7
 */
public class MessageBoxUtil {

    public static void alert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image icon = new Image(Objects.requireNonNull(MagicMibApplication.class.getResourceAsStream("/image/MagicMIB.png")));
        stage.getIcons().add(icon);

        alert.showAndWait();
    }

    public static void error(String headerText, String contentText) {
        alert(Alert.AlertType.ERROR,
                "错误",
                headerText,
                contentText);
    }

    public static void success(String headerText, String contentText) {
        alert(Alert.AlertType.INFORMATION,
                "成功",
                headerText,
                contentText);
    }

}
