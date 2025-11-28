package com.zzhow.magicmibbackend.controller;

import com.zzhow.magicmibbackend.pojo.dto.AuthenticationDTO;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 身份认证信息控制类
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/28
 */
@Slf4j
@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * 设置身份认证信息
     *
     * @param authenticationDTO 身份认证信息传输模型
     * @return 保存信息
     */
    @PostMapping("/set")
    public Result<String> setAuthentication(@RequestBody AuthenticationDTO authenticationDTO) {
        log.info("收到认证信息：authenticationDTO = {}", authenticationDTO);

        return authenticationService.setAuthentication(authenticationDTO);
    }
}
