package com.zzhow.magicmibbackend.service;

import com.zzhow.magicmibbackend.pojo.dto.AuthenticationDTO;
import com.zzhow.magicmibbackend.pojo.vo.AuthenticationVO;
import com.zzhow.magicmibbackend.result.Result;

/**
 * 身份认证信息服务类接口
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/29
 */
public interface AuthenticationService {
    /**
     * 设置身份认证信息
     *
     * @param authenticationDTO 身份认证信息传输模型
     * @return 保存信息
     */
    Result<String> setAuthentication(AuthenticationDTO authenticationDTO);

    /**
     * 获取身份认证信息
     *
     * @return 身份认证信息视图
     */
    Result<AuthenticationVO> getAuthentication();
}
