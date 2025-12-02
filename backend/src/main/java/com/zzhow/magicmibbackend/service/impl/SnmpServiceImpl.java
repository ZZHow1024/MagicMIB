package com.zzhow.magicmibbackend.service.impl;

import com.zzhow.magicmibbackend.pojo.dto.GetBulkDTO;
import com.zzhow.magicmibbackend.pojo.dto.SnmpGetDTO;
import com.zzhow.magicmibbackend.repository.AuthenticationRepository;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.SnmpService;
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
 * SNMP 服务实现类（封装 SNMP4J）
 *
 * @author ZZHow
 * create 2025/11/27
 * update 2025/12/2
 */
@Slf4j
@Service
public class SnmpServiceImpl implements SnmpService {
    public enum SnmpOperation {
        GET,
        GET_NEXT,
        GET_BULK
    }

    private final Snmp snmp;

    @Autowired
    public SnmpServiceImpl(Snmp snmp) {
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
     * 执行 SNMP Get 请求
     *
     * @param snmpGetDTO SNMP Get 请求信息传输模型
     * @return 结果字符串
     */
    @Override
    public Result<String> get(SnmpGetDTO snmpGetDTO) {
        return this.performSnmpGet(AuthenticationRepository.address, AuthenticationRepository.port, snmpGetDTO.getOid(), AuthenticationRepository.readCommunity);
    }

    /**
     * 执行 SNMP GetNext 请求
     *
     * @param snmpGetDTO SNMP Get/GetNext 请求信息传输模型
     * @return 结果字符串
     */
    @Override
    public Result<String> getNext(SnmpGetDTO snmpGetDTO) {
        return this.performSnmpGetNext(AuthenticationRepository.address, AuthenticationRepository.port, snmpGetDTO.getOid(), AuthenticationRepository.readCommunity);
    }

    /**
     * 执行 SNMP GetBulk 请求
     *
     * @param getBulkDTO GetBulk 请求信息传输模型
     * @return 结果字符串
     */
    @Override
    public Result<String> getBulk(GetBulkDTO getBulkDTO) {
        return this.performSnmpGetBulk(AuthenticationRepository.address, AuthenticationRepository.port, getBulkDTO.getOids(), getBulkDTO.getNonRepeaters(), getBulkDTO.getMaxRepetitions(), AuthenticationRepository.readCommunity);
    }

    /**
     * 执行 SNMP Get 请求
     *
     * @param agentIp   Agent IP
     * @param port      Agent 端口号
     * @param oid       目标 OID
     * @param community 共同体名
     * @return 结果字符串
     */
    public Result<String> performSnmpGet(String agentIp, Integer port, String oid, String community) {
        return performSnmp(agentIp, port, oid, community, SnmpOperation.GET);
    }

    /**
     * 执行 SNMP GetNext 请求
     *
     * @param agentIp   Agent IP
     * @param port      Agent 端口号
     * @param oid       起始 OID
     * @param community 共同体名
     * @return 下一条 OID 及其值
     */
    public Result<String> performSnmpGetNext(String agentIp, Integer port, String oid, String community) {
        return performSnmp(agentIp, port, oid, community, SnmpOperation.GET_NEXT);
    }

    /**
     * 执行 SNMP GetBulk 请求
     *
     * @param agentIp   Agent IP
     * @param port      Agent 端口号
     * @param vbs       变量绑定数组
     * @param n         非重复变量数量
     * @param m         最大重复次数
     * @param community 共同体名
     * @return GetBulk 结果
     */
    public Result<String> performSnmpGetBulk(String agentIp, Integer port, String[] vbs, String n, String m, String community) {
        // 创建目标地址
        Address targetAddress = new UdpAddress(agentIp + "/" + port);

        // 配置目标
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c); // GetBulk 需要 SNMPv2c 或更高版本

        // 创建 PDU
        PDU pdu = new PDU();
        pdu.setType(PDU.GETBULK);

        // 设置 non-repeaters 和 max-repetitions
        try {
            pdu.setNonRepeaters(Integer.parseInt(n));
            pdu.setMaxRepetitions(Integer.parseInt(m));
        } catch (NumberFormatException e) {
            log.error("Invalid n or m parameter: n={}, m={}", n, m);
            return Result.error("参数 n 或 m 格式错误");
        }

        // 添加变量绑定
        if (vbs != null && vbs.length > 0) {
            for (String oid : vbs) {
                pdu.add(new VariableBinding(new OID(oid)));
            }
        } else {
            return Result.error("变量绑定列表不能为空");
        }

        try {
            // 发送请求
            ResponseEvent responseEvent = snmp.send(pdu, target);

            if (responseEvent != null && responseEvent.getResponse() != null) {
                PDU response = responseEvent.getResponse();

                if (response.getErrorIndex() == 0) {
                    // 构建返回结果
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < response.size(); i++) {
                        VariableBinding vb = response.get(i);
                        result.append(vb.getOid().toDottedString())
                                .append(" = ")
                                .append(vb.getVariable().toString());
                        if (i < response.size() - 1) {
                            result.append("\n");
                        }
                    }
                    return Result.success(result.toString());
                } else {
                    return Result.error(response.getErrorStatusText());
                }
            } else {
                log.error("连接超时");
                return Result.error("连接超时");
            }
        } catch (IOException e) {
            log.error("SNMP communication error: {}", e.getMessage());
            return Result.error("系统错误");
        }
    }

    /**
     * 执行通用 SNMP 请求
     *
     * @param agentIp   Agent IP
     * @param port      Agent 端口号
     * @param oid       目标 OID
     * @param community 共同体名
     * @param operation SNMP 操作类型
     * @return 结果字符串
     */
    public Result<String> performSnmp(String agentIp, Integer port, String oid, String community, SnmpOperation operation) {
        // 创建目标地址 (默认端口 161)
        Address targetAddress = new UdpAddress(agentIp + "/" + port);

        // 配置目标
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version1);

        // 创建 PDU（根据操作类型设置）
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        if (operation == SnmpOperation.GET) {
            pdu.setType(PDU.GET);
        } else {
            pdu.setType(PDU.GETNEXT);
        }

        try {
            // 发送请求
            ResponseEvent responseEvent = snmp.send(pdu, target);

            if (responseEvent != null && responseEvent.getResponse() != null) {
                // 解析响应
                VariableBinding vb = responseEvent.getResponse().get(0);
                if (responseEvent.getResponse().getErrorIndex() == 0) {
                    if (operation == SnmpOperation.GET) {
                        return Result.success(vb.getVariable().toString());
                    } else {
                        return Result.success(vb.getOid().toDottedString() + " = " + vb.getVariable().toString());
                    }
                } else {
                    return Result.error(responseEvent.getResponse().getErrorStatusText());
                }
            } else {
                log.error("连接超时");
                return Result.error("连接超时");
            }
        } catch (IOException e) {
            log.error("SNMP communication error: {}", e.getMessage());
            return Result.error("系统错误");
        }
    }
}
