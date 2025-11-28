package com.zzhow.magicmibbackend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SNMP Get 请求信息传输模型
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpGetDTO {
    // 请求的 OID
    private String oid;
}
