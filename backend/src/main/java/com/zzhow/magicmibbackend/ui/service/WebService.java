package com.zzhow.magicmibbackend.ui.service;

/**
 * Web 服务类接口
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/7
 */
public interface WebService {
    /**
     * 启动 Web 服务
     *
     * @param port 端口号
     * @return 0-启动成功；1-端口号错误；2-端口被占用
     */
    byte startService(String port);

    /**
     * 停止 Web 服务
     */
    void stopService();
}
