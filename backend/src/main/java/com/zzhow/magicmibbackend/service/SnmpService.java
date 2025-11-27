package com.zzhow.magicmibbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

import java.io.IOException;

/**
 * SNMP 服务类（封装 SNMP4J）
 *
 * @author ZZHow
 * create 2025/11/27
 * update 2025/11/27
 */
@Slf4j
@Service
public class SnmpService {

    private final Snmp snmp;

    @Autowired
    public SnmpService(Snmp snmp) {
        this.snmp = snmp;
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (snmp != null) {
            snmp.close();
            log.info("SNMP Listener closed.");
        }
    }

    /**
     * 执行 SNMP GET 请求
     *
     * @param agentIp   Agent IP
     * @param oid       目标 OID
     * @param community 共同体名
     * @return 结果字符串
     */
    public String performSnmpGet(String agentIp, String oid, String community) {
        // 创建目标地址 (默认端口 161)
        Address targetAddress = new UdpAddress(agentIp + "/161");

        // 配置目标
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version1);

        // 创建 PDU（GET 操作）
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        try {
            // 发送请求
            ResponseEvent responseEvent = snmp.send(pdu, target);

            if (responseEvent != null && responseEvent.getResponse() != null) {
                // 解析响应
                VariableBinding vb = responseEvent.getResponse().get(0);
                return vb.getVariable().toString();
            } else {
                log.error("No Response from target device");
                return "No Response from target device.";
            }
        } catch (IOException e) {
            log.error("SNMP communication error: {}", e.getMessage());
            return "SNMP communication error.";
        }
    }
}
