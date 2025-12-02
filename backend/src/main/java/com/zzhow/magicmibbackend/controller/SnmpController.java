package com.zzhow.magicmibbackend.controller;

import com.zzhow.magicmibbackend.pojo.dto.GetBulkDTO;
import com.zzhow.magicmibbackend.pojo.dto.SnmpGetDTO;
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
 * update 2025/12/2
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
     * @param snmpGetDTO SNMP Get/GetNext 请求信息传输模型
     * @return Get 信息
     */
    @GetMapping("/get")
    public Result<String> get(SnmpGetDTO snmpGetDTO) {
        log.info("发起 SNMP Get 请求：snmpGetDTO = {}", snmpGetDTO);

        return snmpService.get(snmpGetDTO);
    }

    /**
     * 发起 SNMP GetNext 请求
     *
     * @param snmpGetDTO SNMP Get/GetNext 请求信息传输模型
     * @return GetNext 信息
     */
    @GetMapping("/get-next")
    public Result<String> getNext(SnmpGetDTO snmpGetDTO) {
        log.info("发起 SNMP GetNext 请求：snmpGetDTO = {}", snmpGetDTO);

        return snmpService.getNext(snmpGetDTO);
    }

    /**
     * 发起 SNMP GetBulk 请求
     *
     * @param getBulkDTO GetBulk 请求信息传输模型
     * @return GetBulk 信息
     */
    @GetMapping("/get-bulk")
    public Result<String> getBulk(GetBulkDTO getBulkDTO) {
        log.info("发起 SNMP GetBulk 请求：getBulkDTO = {}", getBulkDTO);

        return snmpService.getBulk(getBulkDTO);
    }
}
