package com.zzhow.magicmibbackend.ui.service.impl;

import com.zzhow.magicmibbackend.ui.service.WebService;
import com.zzhow.magicmibbackend.util.Application;
import com.zzhow.magicmibbackend.util.InternetUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Web 服务类实现类
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/7
 */
public class WebServiceImpl implements WebService {
    private ConfigurableApplicationContext applicationContext;

    /**
     * 启动 Web 服务
     *
     * @param portStr 端口号
     * @return 0-启动成功；1-端口号错误；2-端口被占用
     */
    @Override
    public byte startService(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            if (port < 1 || port > 65535)
                return 1;
            if (InternetUtil.isPortInUse(port))
                return 2;
            else {
                applicationContext = Application.startService("--server.port=" + port);
                return 0;
            }
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * 停止 Web 服务
     */
    @Override
    public void stopService() {
        SpringApplication.exit(applicationContext, () -> 0);
    }
}
