package com.zzhow.magicmibbackend.service.impl;

import com.zzhow.magicmibbackend.pojo.dto.GetBulkDTO;
import com.zzhow.magicmibbackend.pojo.dto.SnmpGetDTO;
import com.zzhow.magicmibbackend.pojo.dto.SnmpSetDTO;
import com.zzhow.magicmibbackend.pojo.vo.SnmpResultVO;
import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import com.zzhow.magicmibbackend.repository.AuthenticationRepository;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.SnmpService;
import com.zzhow.magicmibbackend.util.MibParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

import java.io.IOException;

/**
 * SNMP 服务实现类（封装 SNMP4J）
 *
 * @author ZZHow
 * create 2025/11/27
 * update 2025/12/5
 */
@Slf4j
@Service
public class SnmpServiceImpl implements SnmpService {
    public enum SnmpOperation {
        GET,
        GET_NEXT,
        GET_BULK,
        SET
    }

    private final Snmp snmp;
    private final MibParseUtil mibParseUtil;

    @Autowired
    public SnmpServiceImpl(Snmp snmp, MibParseUtil mibParseUtil) {
        this.snmp = snmp;
        this.mibParseUtil = mibParseUtil;
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
     * @return SNMP 结果视图（包含单条数据）
     */
    @Override
    public Result<SnmpResultVO> get(SnmpGetDTO snmpGetDTO) {
        long startTime = System.currentTimeMillis();
        try {
            Result<String> result = this.performSnmpGet(AuthenticationRepository.address, AuthenticationRepository.port, snmpGetDTO.getOid(), AuthenticationRepository.readCommunity);
            long executionTime = System.currentTimeMillis() - startTime;

            if (result.getCode() == 0) {
                SnmpResultVO.SnmpDataVO dataVO = createSnmpDataVO(snmpGetDTO.getOid(), result.getData());

                SnmpResultVO resultVO = SnmpResultVO.builder()
                        .operation("GET")
                        .address(AuthenticationRepository.address)
                        .port(AuthenticationRepository.port)
                        .data(java.util.Arrays.asList(dataVO))
                        .success(true)
                        .executionTime(executionTime)
                        .build();

                return Result.success(resultVO);
            } else {
                return Result.error(result.getMessage());
            }
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return Result.error("SNMP Get 操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行 SNMP GetNext 请求
     *
     * @param snmpGetDTO SNMP GetNext 请求信息传输模型
     * @return SNMP 结果视图（包含单条数据）
     */
    @Override
    public Result<SnmpResultVO> getNext(SnmpGetDTO snmpGetDTO) {
        long startTime = System.currentTimeMillis();
        try {
            Result<String> result = this.performSnmpGetNext(AuthenticationRepository.address, AuthenticationRepository.port, snmpGetDTO.getOid(), AuthenticationRepository.readCommunity);
            long executionTime = System.currentTimeMillis() - startTime;

            if (result.getCode() == 0) {
                String response = result.getData();
                // 解析 "oid = value" 格式
                String[] parts = response.split(" = ", 2);
                String oid = parts.length > 0 ? parts[0] : snmpGetDTO.getOid();
                String value = parts.length > 1 ? parts[1] : "";

                SnmpResultVO.SnmpDataVO dataVO = createSnmpDataVO(oid, value);

                SnmpResultVO resultVO = SnmpResultVO.builder()
                        .operation("GETNEXT")
                        .address(AuthenticationRepository.address)
                        .port(AuthenticationRepository.port)
                        .data(java.util.Arrays.asList(dataVO))
                        .success(true)
                        .executionTime(executionTime)
                        .build();

                return Result.success(resultVO);
            } else {
                return Result.error(result.getMessage());
            }
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return Result.error("SNMP GetNext 操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行 SNMP GetBulk 请求
     *
     * @param getBulkDTO GetBulk 请求信息传输模型
     * @return SNMP 结果视图（包含多条数据）
     */
    @Override
    public Result<SnmpResultVO> getBulk(GetBulkDTO getBulkDTO) {
        long startTime = System.currentTimeMillis();
        java.util.List<SnmpResultVO.SnmpDataVO> dataList = new java.util.ArrayList<>();

        try {
            // 创建目标地址
            Address targetAddress = new UdpAddress(AuthenticationRepository.address + "/" + AuthenticationRepository.port);

            // 配置目标
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(AuthenticationRepository.readCommunity));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(1500);
            target.setVersion(SnmpConstants.version2c); // GetBulk 需要 SNMPv2c 或更高版本

            // 创建 PDU
            PDU pdu = new PDU();
            pdu.setType(PDU.GETBULK);

            // 设置 non-repeaters 和 max-repetitions
            try {
                pdu.setNonRepeaters(Integer.parseInt(getBulkDTO.getNonRepeaters()));
                pdu.setMaxRepetitions(Integer.parseInt(getBulkDTO.getMaxRepetitions()));
            } catch (NumberFormatException e) {
                log.error("Invalid nonRepeaters or maxRepetitions parameter: nonRepeaters={}, maxRepetitions={}", 
                         getBulkDTO.getNonRepeaters(), getBulkDTO.getMaxRepetitions());
                return Result.error("参数 nonRepeaters 或 maxRepetitions 格式错误");
            }

            // 添加变量绑定
            String[] oids = getBulkDTO.getOids();
            if (oids != null && oids.length > 0) {
                for (String oid : oids) {
                    pdu.add(new VariableBinding(new OID(oid)));
                }
            } else {
                return Result.error("变量绑定列表不能为空");
            }

            // 发送请求
            ResponseEvent responseEvent = snmp.send(pdu, target);

            if (responseEvent != null && responseEvent.getResponse() != null) {
                PDU response = responseEvent.getResponse();

                if (response.getErrorIndex() == 0) {
                    // 直接从 PDU 响应中解析每个 VariableBinding
                    for (int i = 0; i < response.size(); i++) {
                        VariableBinding vb = response.get(i);
                        String oid = vb.getOid().toDottedString();
                        String value = vb.getVariable().toString();

                        SnmpResultVO.SnmpDataVO dataVO = createSnmpDataVO(oid, value);
                        dataList.add(dataVO);
                    }

                    long executionTime = System.currentTimeMillis() - startTime;
                    SnmpResultVO resultVO = SnmpResultVO.builder()
                            .operation("GETBULK")
                            .address(AuthenticationRepository.address)
                            .port(AuthenticationRepository.port)
                            .data(dataList)
                            .success(true)
                            .executionTime(executionTime)
                            .build();

                    return Result.success(resultVO);
                } else {
                    return Result.error(response.getErrorStatusText());
                }
            } else {
                log.error("连接超时");
                return Result.error("连接超时");
            }
        } catch (IOException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("SNMP GetBulk 操作异常: {}", e.getMessage(), e);
            return Result.error("SNMP GetBulk 操作失败: " + e.getMessage());
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("SNMP GetBulk 操作异常: {}", e.getMessage(), e);
            return Result.error("SNMP GetBulk 操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行 SNMP Walk 操作（遍历所有结点，不限 OID 前缀）
     *
     * @param snmpGetDTO SNMP Walk 请求信息传输模型
     * @return SNMP 结果视图（包含多条数据）
     */
    @Override
    public Result<SnmpResultVO> walk(SnmpGetDTO snmpGetDTO) {
        long startTime = System.currentTimeMillis();
        java.util.List<SnmpResultVO.SnmpDataVO> dataList = new java.util.ArrayList<>();
        String startOid = snmpGetDTO.getOid();
        String currentOid = startOid;

        try {
            // 限制最大步行次数，避免无限循环
            int maxWalks = 10000;
            int walkCount = 0;

            while (walkCount < maxWalks) {
                Result<String> result = this.performSnmpGetNext(AuthenticationRepository.address, AuthenticationRepository.port, currentOid, AuthenticationRepository.readCommunity);

                if (result.getCode() != 0) {
                    log.info("SNMP Walk 结束，原因: GetNext 失败 - {}", result.getMessage());
                    break;
                }

                String response = result.getData();
                if (response == null || response.trim().isEmpty()) {
                    log.info("SNMP Walk 结束，原因: 空响应");
                    break;
                }

                String[] parts = response.split(" = ", 2);
                String nextOid = parts.length > 0 ? parts[0] : "";
                String value = parts.length > 1 ? parts[1] : "";

                // 添加到结果列表（Walk不限制OID前缀，获取所有结点）
                SnmpResultVO.SnmpDataVO dataVO = createSnmpDataVO(nextOid, value);
                dataList.add(dataVO);

                // 更新当前 OID
                currentOid = nextOid;
                walkCount++;
            }

            long executionTime = System.currentTimeMillis() - startTime;
            SnmpResultVO resultVO = SnmpResultVO.builder()
                    .operation("WALK")
                    .address(AuthenticationRepository.address)
                    .port(AuthenticationRepository.port)
                    .data(dataList)
                    .success(true)
                    .executionTime(executionTime)
                    .build();

            log.info("SNMP Walk 完成，起始 OID: {}, 获取数据条数: {}, 耗时: {}ms",
                    startOid, dataList.size(), executionTime);

            return Result.success(resultVO);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("SNMP Walk 操作异常: {}", e.getMessage(), e);
            return Result.error("SNMP Walk 操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行 SNMP Set 请求
     *
     * @param snmpSetDTO SNMP Set 请求信息传输模型
     * @return SNMP 结果视图（包含单条数据）
     */
    @Override
    public Result<SnmpResultVO> set(SnmpSetDTO snmpSetDTO) {
        long startTime = System.currentTimeMillis();
        try {
            Result<String> result = this.performSnmpSet(AuthenticationRepository.address, AuthenticationRepository.port, snmpSetDTO.getOid(), snmpSetDTO.getValue(), snmpSetDTO.getType(), AuthenticationRepository.writeCommunity);
            long executionTime = System.currentTimeMillis() - startTime;

            if (result.getCode() == 0) {
                SnmpResultVO.SnmpDataVO dataVO = createSnmpDataVO(snmpSetDTO.getOid(), result.getData());

                SnmpResultVO resultVO = SnmpResultVO.builder()
                        .operation("SET")
                        .address(AuthenticationRepository.address)
                        .port(AuthenticationRepository.port)
                        .data(java.util.Arrays.asList(dataVO))
                        .success(true)
                        .executionTime(executionTime)
                        .build();

                return Result.success(resultVO);
            } else {
                return Result.error(result.getMessage());
            }
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return Result.error("SNMP Set 操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行 SNMP GetSubtree 操作（获取指定 OID 子树的所有数据）
     *
     * @param snmpGetDTO SNMP GetSubtree 请求信息传输模型
     * @return SNMP 结果视图（包含多条数据）
     */
    @Override
    public Result<SnmpResultVO> getSubtree(SnmpGetDTO snmpGetDTO) {
        long startTime = System.currentTimeMillis();
        java.util.List<SnmpResultVO.SnmpDataVO> dataList = new java.util.ArrayList<>();
        String startOid = snmpGetDTO.getOid();
        String currentOid = startOid;

        try {
            // 限制最大步行次数，避免无限循环
            int maxWalks = 10000;
            int walkCount = 0;

            while (walkCount < maxWalks) {
                Result<String> result = this.performSnmpGetNext(AuthenticationRepository.address, AuthenticationRepository.port, currentOid, AuthenticationRepository.readCommunity);

                if (result.getCode() != 0) {
                    log.info("SNMP GetSubtree 结束，原因: GetNext 失败 - {}", result.getMessage());
                    break;
                }

                String response = result.getData();
                if (response == null || response.trim().isEmpty()) {
                    log.info("SNMP GetSubtree 结束，原因: 空响应");
                    break;
                }

                String[] parts = response.split(" = ", 2);
                String nextOid = parts.length > 0 ? parts[0] : "";
                String value = parts.length > 1 ? parts[1] : "";

                // 检查是否超出范围（返回的 OID 必须以起始 OID 开头）
                if (!nextOid.startsWith(startOid)) {
                    log.info("SNMP GetSubtree 结束，原因: OID {} 超出范围 {}", nextOid, startOid);
                    break;
                }

                // 添加到结果列表（GetSubtree只获取相同OID前缀的结点）
                SnmpResultVO.SnmpDataVO dataVO = createSnmpDataVO(nextOid, value);
                dataList.add(dataVO);

                // 更新当前 OID
                currentOid = nextOid;
                walkCount++;
            }

            long executionTime = System.currentTimeMillis() - startTime;
            SnmpResultVO resultVO = SnmpResultVO.builder()
                    .operation("GETSUBTREE")
                    .address(AuthenticationRepository.address)
                    .port(AuthenticationRepository.port)
                    .data(dataList)
                    .success(true)
                    .executionTime(executionTime)
                    .build();

            log.info("SNMP GetSubtree 完成，起始 OID: {}, 获取数据条数: {}, 耗时: {}ms",
                    startOid, dataList.size(), executionTime);

            return Result.success(resultVO);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("SNMP GetSubtree 操作异常: {}", e.getMessage(), e);
            return Result.error("SNMP GetSubtree 操作失败: " + e.getMessage());
        }
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
     * 执行 SNMP Set 请求
     *
     * @param agentIp   Agent IP
     * @param port      Agent 端口号
     * @param oid       目标 OID
     * @param value     设置的值
     * @param type      数据类型
     * @param community 共同体名
     * @return 设置结果
     */
    public Result<String> performSnmpSet(String agentIp, Integer port, String oid, String value, String type, String community) {
        // 创建目标地址
        Address targetAddress = new UdpAddress(agentIp + "/" + port);

        // 配置目标
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version1);

        // 创建 PDU
        PDU pdu = new PDU();
        pdu.setType(PDU.SET);

        // 根据类型创建相应的 Variable
        Variable variable;
        try {
            switch (type.toLowerCase()) {
                case "i":  // INTEGER
                    variable = new Integer32(Integer.parseInt(value));
                    break;
                case "s":  // OCTET STRING
                    variable = new OctetString(value);
                    break;
                case "x":  // HEX-STRING
                    variable = OctetString.fromHexString(value);
                    break;
                case "d":  // DECIMAL STRING
                    variable = new OctetString(value);
                    break;
                case "a":  // IPADDRESS
                    variable = new IpAddress(value);
                    break;
                case "o":  // OBJECTID
                    variable = new OID(value);
                    break;
                case "t":  // TIMETICKS
                    variable = new TimeTicks(Long.parseLong(value));
                    break;
                case "u":  // UNSIGNED32
                    variable = new UnsignedInteger32(Long.parseLong(value));
                    break;
                case "c":  // COUNTER32
                    variable = new Counter32(Long.parseLong(value));
                    break;
                case "g":  // GAUGE32
                    variable = new Gauge32(Long.parseLong(value));
                    break;
                default:
                    return Result.error("不支持的数据类型: " + type);
            }
        } catch (Exception e) {
            log.error("解析设置值失败: type={}, value={}, error={}", type, value, e.getMessage());
            return Result.error("解析设置值失败: " + e.getMessage());
        }

        // 添加变量绑定
        pdu.add(new VariableBinding(new OID(oid), variable));

        try {
            // 发送请求
            ResponseEvent responseEvent = snmp.send(pdu, target);

            if (responseEvent != null && responseEvent.getResponse() != null) {
                PDU response = responseEvent.getResponse();

                if (response.getErrorIndex() == 0) {
                    // 解析响应
                    VariableBinding vb = response.get(0);
                    return Result.success(vb.getVariable().toString());
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

    /**
     * 创建 SnmpDataVO 对象，从 MIB 信息中填充 name、access、description
     *
     * @param oid   OID
     * @param value 值
     * @return 填充了 MIB 信息的 SnmpDataVO
     */
    private SnmpResultVO.SnmpDataVO createSnmpDataVO(String oid, String value) {
        // 从 MIB 中查找节点信息
        MibNode mibNode = mibParseUtil.findNodeByOid(oid);

        return SnmpResultVO.SnmpDataVO.builder()
                .name(mibNode != null ? mibNode.getLabel() : "")
                .oid(oid)
                .value(value)
                .type(getDataTypeFromValue(value))
                .access(mibNode != null ? mibNode.getAccess() : "unknown")
                .description(mibNode != null && mibNode.getDescription() != null ? mibNode.getDescription() : "")
                .build();
    }

    /**
     * 根据值判断数据类型
     *
     * @param value SNMP 值
     * @return 数据类型字符串
     */
    private String getDataTypeFromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "NULL";
        }

        // 检查是否为数字
        try {
            Integer.parseInt(value);
            return "INTEGER";
        } catch (NumberFormatException e) {
            // 不是整数
        }

        try {
            Long.parseLong(value);
            return "COUNTER64";
        } catch (NumberFormatException e) {
            // 不是长整数
        }

        // 检查是否为时间戳类型
        if (value.matches("\\d+ days, \\d{2}:\\d{2}:\\d{2}.\\d{2}")) {
            return "TIMETICKS";
        }

        // 检查是否为十六进制字符串
        if (value.startsWith("0x") || value.matches("[0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2})*")) {
            return "HEX-STRING";
        }

        // 检查是否为 IP 地址
        if (value.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            return "IPADDRESS";
        }

        // 检查是否为 OID
        if (value.matches("\\d+(\\.\\d+)*")) {
            return "OBJECTID";
        }

        // 检查是否为枚举值（常见 SNMP 枚举）
        if (value.matches("(up|down|testing|unknown|true|false|enabled|disabled|on|off)")) {
            return "ENUM";
        }

        // 默认为字符串
        return "OCTETSTRING";
    }
}
