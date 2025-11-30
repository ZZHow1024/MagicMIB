package com.zzhow.magicmibbackend.util;

import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import lombok.extern.slf4j.Slf4j;
import net.percederberg.mibble.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * MIB 文件解析工具类
 * 基于 Mibble 2.10.1 版本实现，用于解析标准 MIB 文件并构建树形结构
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
@Slf4j
@Component
public class MibParseUtil {

    /**
     * 支持的标准MIB文件列表（基于mibble src/mibs目录）
     */
    private static final String[] STANDARD_MIBS = {
            // IETF标准MIB文件
            "SNMPv2-SMI", "SNMPv2-TC", "SNMPv2-CONF", "SNMPv2-MIB",
            "SNMP-COMMUNITY-MIB", "SNMP-FRAMEWORK-MIB", "SNMP-PROXY-MIB",
            "SNMP-TARGET-MIB", "SNMP-NOTIFICATION-MIB",
            "RFC1213-MIB", "IF-MIB", "IP-MIB",
            "TCP-MIB", "UDP-MIB",
            "RIPv2-MIB", "RMON-MIB", "RMON2-MIB", "HOST-RESOURCES-MIB",
            "ENTITY-SENSOR-MIB", "VRRP-MIB",
            // IANA标准MIB文件
            "IANAifType-MIB", "IANA-ADDRESS-FAMILY-NUMBERS-MIB",
            "IANA-PRINTER-MIB", "IANA-MAU-MIB"
    };

    /**
     * 解析 MIB 文件名数组并构建 MIB 节点树
     * 从 Mibble 自带的标准 MIB 文件中加载指定的 MIB 模块
     * 自动包含基础节点：iso(1), org(3), dod(6), internet(1)等
     * 返回以根节点为起点的完整树形结构
     *
     * @param mibFileNames MIB 文件名数组，每个元素为 MIB 文件名
     * @return 解析后的 MibNode 对象集合（以根节点为起点的树形结构）
     * @throws RuntimeException 当 MIB 解析失败时抛出
     */
    public List<MibNode> parseMibFiles(String[] mibFileNames) throws RuntimeException {
        log.info("开始解析 MIB 文件，文件数量: {}", mibFileNames.length);

        Map<String, MibNode> nodeMap = new HashMap<>();
        MibNode root = null;

        try {
            // 首先创建基础 MIB 节点树结构
            root = createBasicMibTree(nodeMap);

            // 创建 MIB 加载器，自动加载 Mibble 自带的标准 MIB 文件
            MibLoader loader = new MibLoader();
            // 添加 Mibble 内置的资源目录，这样可以直接加载标准 MIB 文件
            loader.addResourceDir("net/percederberg/mibble/mibs/iana");
            loader.addResourceDir("net/percederberg/mibble/mibs/ietf");

            // 加载指定的 MIB 模块
            for (int i = 0; i < mibFileNames.length; i++) {
                try {
                    String mibName = mibFileNames[i].trim();
                    if (mibName.isEmpty()) {
                        log.warn("第{}个MIB文件名为空，跳过", i + 1);
                        continue;
                    }

                    // 加载指定的 MIB 模块
                    Mib mib = loader.load(mibName);

                    // 将 MIB 模块的节点添加到树结构中
                    addMibToTree(mib, nodeMap);

                    log.info("成功加载MIB模块: {}", mibName);

                } catch (Exception e) {
                    log.error("加载MIB模块{}失败: {}", mibFileNames[i], e.getMessage());
                    throw new RuntimeException("无法加载MIB模块: " + mibFileNames[i] + ", 错误: " + e.getMessage(), e);
                }
            }

            // 返回只包含根节点的列表（根节点包含完整的树形结构）
            List<MibNode> result = new ArrayList<>();
            if (root != null) {
                result.add(root);
            }
            log.info("MIB文件解析完成，根节点为{}", root != null ? root.getLabel() : "null");
            return result;
        } catch (Exception e) {
            log.error("解析MIB文件过程中发生异常", e);
            throw new RuntimeException("MIB解析异常: " + e.getMessage(), e);
        }
    }

    /**
     * 创建基础 MIB 树结构
     * 创建标准 MIB 树结构的基础节点：iso(1), org(3), dod(6), internet(1)等
     * 返回根节点 iso
     *
     * @param nodeMap 节点映射表
     * @return 根节点（包含完整树结构）
     */
    private MibNode createBasicMibTree(Map<String, MibNode> nodeMap) {
        log.info("创建基础MIB树结构");

        // 创建根节点 - iso(1)
        MibNode iso = createBasicNode("iso", "1", "Root node of global OID namespace.", "root", "SEQUENCE", "not-accessible", "mandatory");
        nodeMap.put(iso.getOid(), iso);

        // org(3) 在 iso(1) 下，OID为 1.3
        MibNode org = createBasicNode("org", "1.3", "Routes object identifiers allocated to organizations.", "root", "SEQUENCE", "not-accessible", "mandatory");
        iso.getChildren().add(org);
        nodeMap.put(org.getOid(), org);

        // dod(6) 在 org(3) 下，OID为 1.3.6
        MibNode dod = createBasicNode("dod", "1.3.6", "Department of Defense branch.", "root", "SEQUENCE", "not-accessible", "mandatory");
        org.getChildren().add(dod);
        nodeMap.put(dod.getOid(), dod);

        // internet(1) 在 dod(6) 下，OID为 1.3.6.1
        MibNode internet = createBasicNode("internet", "1.3.6.1", "Internet-specific information.", "root", "SEQUENCE", "not-accessible", "mandatory");
        dod.getChildren().add(internet);
        nodeMap.put(internet.getOid(), internet);

        // directory(1) 在 internet(1) 下，OID为 1.3.6.1.1
        MibNode directory = createBasicNode("directory", "1.3.6.1.1", "Directory", "root", "SEQUENCE", "not-accessible", "mandatory");
        internet.getChildren().add(directory);
        nodeMap.put(directory.getOid(), directory);

        // mgmt(2) 在 internet(1) 下，OID为 1.3.6.1.2
        MibNode mgmt = createBasicNode("mgmt", "1.3.6.1.2", "Management branch containing standard MIBs.", "root", "SEQUENCE", "not-accessible", "mandatory");
        internet.getChildren().add(mgmt);
        nodeMap.put(mgmt.getOid(), mgmt);

        // experimental(3) 在 internet(1) 下，OID为 1.3.6.1.3
        MibNode experimental = createBasicNode("experimental", "1.3.6.1.3", "Experimental", "root", "SEQUENCE", "not-accessible", "mandatory");
        internet.getChildren().add(experimental);
        nodeMap.put(experimental.getOid(), experimental);

        // private(4) 在 internet(1) 下，OID为 1.3.6.1.4
        MibNode privateNode = createBasicNode("private", "1.3.6.1.4", "Private", "root", "SEQUENCE", "not-accessible", "mandatory");
        internet.getChildren().add(privateNode);
        nodeMap.put(privateNode.getOid(), privateNode);

        // enterprises(1) 在 private(4) 下，OID为 1.3.6.1.4.1
        MibNode enterprises = createBasicNode("enterprises", "1.3.6.1.4.1", "Enterprises", "root", "SEQUENCE", "not-accessible", "mandatory");
        privateNode.getChildren().add(enterprises);
        nodeMap.put(enterprises.getOid(), enterprises);

        // security(5) 在 internet(1) 下，OID为 1.3.6.1.5
        MibNode security = createBasicNode("security", "1.3.6.1.5", "Security", "root", "SEQUENCE", "not-accessible", "mandatory");
        internet.getChildren().add(security);
        nodeMap.put(security.getOid(), security);

        // snmpV2(6) 在 internet(1) 下，OID为 1.3.6.1.6
        MibNode snmpV2 = createBasicNode("snmpV2", "1.3.6.1.6", "SNMPv2", "root", "SEQUENCE", "not-accessible", "mandatory");
        internet.getChildren().add(snmpV2);
        nodeMap.put(snmpV2.getOid(), snmpV2);

        // mail(7) 在 internet(1) 下，OID为 1.3.6.1.7
        MibNode mail = createBasicNode("mail", "1.3.6.1.7", "Mail", "root", "SEQUENCE", "not-accessible", "mandatory");
        internet.getChildren().add(mail);
        nodeMap.put(mail.getOid(), mail);

        log.info("基础 MIB 树结构创建完成");
        return iso;
    }

    /**
     * 创建基础 MIB 节点
     *
     * @param label       节点标签
     * @param oid         节点 OID
     * @param description 节点描述
     * @param mib         MIB 模块名称
     * @param syntax      语法类型
     * @param access      访问权限
     * @param status      状态
     * @return 创建的 MibNode 对象
     */
    private MibNode createBasicNode(String label, String oid, String description, String mib, String syntax, String access, String status) {
        MibNode node = new MibNode();
        node.setId(oid);
        node.setKey(oid);
        node.setLabel(label);
        node.setOid(oid);
        node.setMib(mib);
        node.setSyntax(syntax);
        node.setAccess(access);
        node.setStatus(status);
        node.setDescription(description);
        return node;
    }

    /**
     * 将 MIB 模块的节点添加到树结构中
     *
     * @param mib     已解析的 MIB 对象
     * @param nodeMap 节点映射表
     */
    private void addMibToTree(Mib mib, Map<String, MibNode> nodeMap) {
        // 处理所有符号
        List<MibSymbol> allSymbols = mib.getAllSymbols();
        for (MibSymbol symbol : allSymbols) {
            // 只处理值符号
            if (symbol instanceof MibValueSymbol) {
                MibValueSymbol valueSymbol = (MibValueSymbol) symbol;
                MibNode node = createNodeFromValueSymbol(valueSymbol, mib);
                if (node != null) {
                    // 将节点添加到映射表
                    nodeMap.put(node.getOid(), node);

                    // 建立父子关系
                    String parentOid = findParentOid(node.getOid());
                    if (parentOid != null && nodeMap.containsKey(parentOid)) {
                        MibNode parent = nodeMap.get(parentOid);
                        if (parent != null && !parent.getChildren().contains(node)) {
                            parent.getChildren().add(node);
                        }
                    }
                }
            }
        }
    }

    /**
     * 从值符号创建 MibNode 对象
     *
     * @param symbol MIB 值符号
     * @param mib    MIB 模块对象
     * @return 创建的 MibNode 对象，如果符号不适合则返回 null
     */
    private MibNode createNodeFromValueSymbol(MibValueSymbol symbol, Mib mib) {
        try {
            // 对于 MibValueSymbol，获取数值 OID
            String oid = symbol.getValue().toString();
            String name = symbol.getName();
            String mibName = mib.getName();

            // 获取符号类型
            String syntax = "";
            String description = "";
            String access = "read-only";
            String status = "current";

            if (symbol.getType() != null) {
                syntax = symbol.getType().getName();
                if (syntax == null) {
                    syntax = symbol.getType().getClass().getSimpleName();
                }
            }

            // 获取文本描述
            if (symbol.getText() != null) {
                description = symbol.getText().trim();
            }

            // 创建节点（使用 OID 作为唯一标识）
            MibNode node = new MibNode();
            node.setId(oid);
            node.setKey(oid);
            node.setLabel(name);
            node.setOid(oid);
            node.setMib(mibName);
            node.setSyntax(syntax);
            node.setAccess(access);
            node.setStatus(status);
            node.setDescription(description);

            return node;

        } catch (Exception e) {
            log.warn("创建节点失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据 OID 查找父节点 OID
     *
     * @param oid 当前节点 OID
     * @return 父节点 OID，如果没有父节点则返回 null
     */
    private String findParentOid(String oid) {
        if (oid == null || oid.isEmpty()) {
            return null;
        }

        String[] parts = oid.split("\\.");
        if (parts.length <= 2) {
            return null; // 根节点或太短的 OID 没有父节点
        }

        // 构建父 OID
        StringBuilder parentOid = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) {
                parentOid.append(".");
            }
            parentOid.append(parts[i]);
        }

        return parentOid.toString();
    }

    /**
     * 解析单个 MIB 文件
     *
     * @param mibName MIB 文件名
     * @return 解析后的 MibNode 对象集合
     * @throws RuntimeException 当 MIB 解析失败时抛出
     */
    public List<MibNode> parseSingleMibFile(String mibName) throws RuntimeException {
        return parseMibFiles(new String[]{mibName});
    }

    /**
     * 检查 MIB 文件名称是否为标准 MIB 文件
     *
     * @param mibName MIB 文件名称
     * @return 如果是标准 MIB 文件返回 true，否则返回 false
     */
    public boolean isStandardMib(String mibName) {
        if (mibName == null || mibName.trim().isEmpty()) {
            return false;
        }

        String normalizedName = mibName.trim();
        for (String standardMib : STANDARD_MIBS) {
            if (standardMib.equalsIgnoreCase(normalizedName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有支持的标准 MIB 文件名称
     *
     * @return 标准 MIB 文件名称数组
     */
    public String[] getStandardMibs() {
        return STANDARD_MIBS.clone();
    }
}