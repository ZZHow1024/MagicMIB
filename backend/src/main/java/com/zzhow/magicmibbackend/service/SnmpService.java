package com.zzhow.magicmibbackend.service;

import com.zzhow.magicmibbackend.pojo.dto.GetBulkDTO;
import com.zzhow.magicmibbackend.pojo.dto.SnmpGetDTO;
import com.zzhow.magicmibbackend.pojo.vo.SnmpResultVO;
import com.zzhow.magicmibbackend.result.Result;

/**
 * SNMP 服务类接口
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/12/3
 */
public interface SnmpService {
    /**
     * 执行 SNMP Get 请求
     *
     * @param snmpGetDTO SNMP Get 请求信息传输模型
     * @return SNMP 结果视图（包含单条数据）
     */
    Result<SnmpResultVO> get(SnmpGetDTO snmpGetDTO);

    /**
     * 执行 SNMP GetNext 请求
     *
     * @param snmpGetDTO SNMP GetNext 请求信息传输模型
     * @return SNMP 结果视图（包含单条数据）
     */
    Result<SnmpResultVO> getNext(SnmpGetDTO snmpGetDTO);

    /**
     * 执行 SNMP GetBulk 请求
     *
     * @param getBulkDTO GetBulk 请求信息传输模型
     * @return SNMP 结果视图（包含多条数据）
     */
    Result<SnmpResultVO> getBulk(GetBulkDTO getBulkDTO);

    /**
     * 执行 SNMP Walk 操作
     *
     * @param snmpGetDTO SNMP Walk 请求信息传输模型
     * @return SNMP 结果视图（包含多条数据）
     */
    Result<SnmpResultVO> walk(SnmpGetDTO snmpGetDTO);
}
