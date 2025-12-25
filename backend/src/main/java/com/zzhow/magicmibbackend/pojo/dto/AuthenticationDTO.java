package com.zzhow.magicmibbackend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 身份认证信息传输模型
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDTO {
    // IP 地址
    private String address;
    // 端口号
    private Integer port;
    // 只读共同体名
    private String readCommunity;
    // 读写共同体名
    private String writeCommunity;
    // SNMP 版本
    private Integer snmpVersion;
}
