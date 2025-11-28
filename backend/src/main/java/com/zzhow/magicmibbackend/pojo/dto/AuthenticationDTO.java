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
    // IP 地址
    private String address = "127.0.0.1";
    // 端口号
    private Integer port = 161;
    // 只读共同体名
    private String readCommunity = "public";
    // 读写共同体名
    private String writeCommunity = "public";
    // SNMP 版本
    private Integer snmpVersion = 1;
}
