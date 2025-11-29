package com.zzhow.magicmibbackend.controller;

import com.zzhow.magicmibbackend.pojo.dto.AuthenticationDTO;
import com.zzhow.magicmibbackend.pojo.vo.AuthenticationVO;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 身份认证信息控制类
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/29
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

    /**
     * 获取身份认证信息
     *
     * @return 保存信息
     */
    @GetMapping("/get")
    public Result<AuthenticationVO> getAuthentication() {
        log.info("获取认证信息");

        return authenticationService.getAuthentication();
    }
}
