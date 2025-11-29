package com.zzhow.magicmibbackend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 身份认证信息视图
 *
 * @author ZZHow
 * create 2025/11/29
 * update 2025/11/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationVO {
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
