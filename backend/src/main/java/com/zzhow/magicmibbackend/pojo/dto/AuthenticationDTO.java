package com.zzhow.magicmibbackend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 身份认证信息传输模型
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDTO {
    private String address = "127.0.0.1";
    private Integer port = 161;
    private String readCommunity = "public";
    private String writeCommunity = "public";
    private Integer snmpVersion = 1;
}
