package com.zzhow.magicmibbackend.service;

import com.zzhow.magicmibbackend.pojo.dto.SnmpGetDTO;
import com.zzhow.magicmibbackend.result.Result;

/**
 * SNMP 服务类接口
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/28
 */
public interface SnmpService {
    /**
     * 执行 SNMP Get 请求
     *
     * @param snmpGetDTO SNMP Get 请求信息传输模型
     * @return 结果字符串
     */
    Result<String> get(SnmpGetDTO snmpGetDTO);
}
