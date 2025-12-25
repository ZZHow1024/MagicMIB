package com.zzhow.magicmibbackend.util;

import java.io.IOException;
import java.net.*;

/**
 * Internet 工具类
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/7
 */
public class InternetUtil {
    /**
     * 检查端口是否被占用
     *
     * @param port 端口号
     * @return true-端口被占用，false-端口未被占用
     */
    public static boolean isPortInUse(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
