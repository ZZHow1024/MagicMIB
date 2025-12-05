package com.zzhow.magicmibbackend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SNMP Set 请求信息传输模型
 *
 * @author ZZHow
 * create 2025/12/5
 * update 2025/12/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpSetDTO {
    // 目标 OID
    private String oid;

    // 设置的值
    private String value;

    // 数据类型
    private String type;
}
