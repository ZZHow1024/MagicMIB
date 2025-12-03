package com.zzhow.magicmibbackend.controller;

import com.zzhow.magicmibbackend.pojo.dto.GetBulkDTO;
import com.zzhow.magicmibbackend.pojo.dto.SnmpGetDTO;
import com.zzhow.magicmibbackend.pojo.vo.SnmpResultVO;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.SnmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * SNMP 请求信息控制类
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/12/3
 */
@Slf4j
@RestController
@RequestMapping("/api/snmp")
public class SnmpController {
    @Autowired
    private SnmpService snmpService;

    /**
     * 发起 SNMP Get 请求
     *
     * @param snmpGetDTO SNMP Get 请求信息传输模型
     * @return SNMP 结果视图（包含单条数据）
     */
    @GetMapping("/get")
    public Result<SnmpResultVO> get(SnmpGetDTO snmpGetDTO) {
        log.info("发起 SNMP Get 请求：snmpGetDTO = {}", snmpGetDTO);

        return snmpService.get(snmpGetDTO);
    }

    /**
     * 发起 SNMP GetNext 请求
     *
     * @param snmpGetDTO SNMP GetNext 请求信息传输模型
     * @return SNMP 结果视图（包含单条数据）
     */
    @GetMapping("/get-next")
    public Result<SnmpResultVO> getNext(SnmpGetDTO snmpGetDTO) {
        log.info("发起 SNMP GetNext 请求：snmpGetDTO = {}", snmpGetDTO);

        return snmpService.getNext(snmpGetDTO);
    }

    /**
     * 发起 SNMP GetBulk 请求
     *
     * @param getBulkDTO GetBulk 请求信息传输模型
     * @return SNMP 结果视图（包含多条数据）
     */
    @GetMapping("/get-bulk")
    public Result<SnmpResultVO> getBulk(GetBulkDTO getBulkDTO) {
        log.info("发起 SNMP GetBulk 请求：getBulkDTO = {}", getBulkDTO);

        return snmpService.getBulk(getBulkDTO);
    }

    /**
     * 发起 SNMP Walk 请求
     *
     * @param snmpGetDTO SNMP Walk 请求信息传输模型
     * @return SNMP 结果视图（包含多条数据）
     */
    @GetMapping("/walk")
    public Result<SnmpResultVO> walk(SnmpGetDTO snmpGetDTO) {
        log.info("发起 SNMP Walk 请求：snmpGetDTO = {}", snmpGetDTO);

        return snmpService.walk(snmpGetDTO);
    }
}
