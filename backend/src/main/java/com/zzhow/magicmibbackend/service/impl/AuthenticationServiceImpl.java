package com.zzhow.magicmibbackend.service.impl;

import com.zzhow.magicmibbackend.pojo.dto.AuthenticationDTO;
import com.zzhow.magicmibbackend.repository.AuthenticationRepository;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 身份认证信息服务实现类
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/28
 */
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    /**
     * 设置身份认证信息
     *
     * @param authenticationDTO 身份认证信息传输模型
     * @return 保存信息
     */
    public Result<String> setAuthentication(AuthenticationDTO authenticationDTO) {
        AuthenticationRepository.address = authenticationDTO.getAddress();
        AuthenticationRepository.port = authenticationDTO.getPort();
        AuthenticationRepository.writeCommunity = authenticationDTO.getWriteCommunity();
        AuthenticationRepository.readCommunity = authenticationDTO.getReadCommunity();
        AuthenticationRepository.snmpVersion = authenticationDTO.getSnmpVersion();
        log.info("身份认证信息保存成功");

        return Result.success();
    }
}
