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
 * update 2025/12/7
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
     * OID 冲突解决策略
     * PREFER_FIRST: 保留第一次加载的节点，忽略后续的
     * PREFER_LAST: 保留最后一次加载的节点，覆盖之前的
     * MERGE: 合并信息，创建复合节点
     */
    private enum ConflictResolution {
        PREFER_FIRST,
        PREFER_LAST,
        MERGE
    }

    // 默认使用保留第一个的策略（RFC1213-MIB 通常更基础）
    private static final ConflictResolution CONFLICT_STRATEGY = ConflictResolution.PREFER_FIRST;

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
     * 处理 OID 冲突问题
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
                    // 处理 OID 冲突
                    String oid = node.getOid();
                    MibNode existingNode = nodeMap.get(oid);

                    if (existingNode != null) {
                        // 发现冲突，根据策略处理
                        handleOidConflict(existingNode, node, oid);
                        continue; // 跳过当前节点，保留现有节点
                    }

                    // 将节点添加到映射表
                    nodeMap.put(oid, node);

                    // 建立父子关系
                    String parentOid = findParentOid(oid);
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
     * 处理 OID 冲突
     */
    private void handleOidConflict(MibNode existingNode, MibNode newNode, String oid) {
        switch (CONFLICT_STRATEGY) {
            // 保留现有节点，忽略新节点
            case PREFER_FIRST:
                break;

            // 覆盖现有节点，使用新节点
            case PREFER_LAST:
                // 更新现有节点的信息
                mergeNodeInfo(existingNode, newNode);
                break;

            // 合并节点信息
            case MERGE:
                mergeNodeInfo(existingNode, newNode);
                break;
        }
    }

    /**
     * 合并节点信息
     */
    private void mergeNodeInfo(MibNode target, MibNode source) {
        // 合并描述信息
        if (source.getDescription() != null && !source.getDescription().isEmpty()) {
            if (target.getDescription() == null || target.getDescription().isEmpty()) {
                target.setDescription(source.getDescription());
            } else if (!target.getDescription().contains(source.getMib())) {
                target.setDescription(target.getDescription() +
                        "\n[" + source.getMib() + "]: " + source.getDescription());
            }
        }

        // 更新MIB信息
        if (!target.getMib().contains(source.getMib())) {
            target.setMib(target.getMib() + ", " + source.getMib());
        }

        // 优先使用有访问权限的节点
        if (source.getAccess() != null && !"not-accessible".equals(source.getAccess()) &&
                (target.getAccess() == null || "not-accessible".equals(target.getAccess()))) {
            target.setAccess(source.getAccess());
        }

        // 优先使用有语法的节点
        if (source.getSyntax() != null && !source.getSyntax().isEmpty() &&
                (target.getSyntax() == null || target.getSyntax().isEmpty())) {
            target.setSyntax(source.getSyntax());
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
            String access = "not-accessible"; // 默认访问权限
            String status = "current"; // 默认状态

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

            // 尝试从 MIB 符号中获取 access 和 status 信息
            // 对于 OBJECT-TYPE 宏，尝试获取 access 和 status
            try {
                // 获取 ACCESS 子句
                String accessValue = getMacroClause(symbol, "ACCESS");
                if (accessValue != null && !accessValue.trim().isEmpty()) {
                    access = accessValue.toLowerCase().replace("_", "-");
                }

                // 获取 STATUS 子句
                String statusValue = getMacroClause(symbol, "STATUS");
                if (statusValue != null && !statusValue.trim().isEmpty()) {
                    status = statusValue.toLowerCase().replace("_", "-");
                }
            } catch (Exception e) {
                log.info("获取 access/status 信息失败: {}", e.getMessage());
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
     * 从宏符号中获取指定子句的值
     *
     * @param symbol MIB 值符号
     * @param clause 子句名称（如 "ACCESS", "STATUS"）
     * @return 子句的值，如果未找到则返回 null
     */
    private String getMacroClause(MibValueSymbol symbol, String clause) {
        try {
            // 从符号的完整文本定义中解析子句
            if (symbol.getText() != null) {
                String text = symbol.getText();

                // 根据子句类型调用相应的解析方法
                if ("ACCESS".equalsIgnoreCase(clause)) {
                    return extractAccessFromText(text);
                }

                if ("STATUS".equalsIgnoreCase(clause)) {
                    return extractStatusFromText(text);
                }
            }

            // 备用方法：尝试从符号的字符串表示中解析
            String symbolString = symbol.toString();
            if ("ACCESS".equalsIgnoreCase(clause)) {
                return extractAccessFromText(symbolString);
            }

            if ("STATUS".equalsIgnoreCase(clause)) {
                return extractStatusFromText(symbolString);
            }

            return null;
        } catch (Exception e) {
            log.info("获取宏子句 {} 失败: {}", clause, e.getMessage());
            return null;
        }
    }

    /**
     * 从文本中提取访问权限
     * 支持 standard MIB ACCESS 子句的各种格式
     */
    private String extractAccessFromText(String text) {
        // 定义所有可能的访问权限类型
        String[] accessTypes = {
                "read-only", "read-write", "write-only", "not-accessible",
                "accessible-for-notify", "read-create"
        };

        // 首先尝试简单匹配
        String lowerText = text.toLowerCase();
        for (String access : accessTypes) {
            if (lowerText.contains("access " + access) ||
                    lowerText.contains("access{" + access) ||
                    lowerText.contains(access + "}") ||
                    lowerText.contains(access + ",") ||
                    lowerText.contains(access + "\n") ||
                    lowerText.contains(access + "\r")) {
                return access;
            }
        }

        // 使用正则表达式进行更精确的匹配
        String[] patterns = {
                "access\\s+\\{?([^}\\n\\r,;]+)\\}?",
                "access\\s+([^\\n\\r,;]+)"
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = regex.matcher(text);

            if (matcher.find()) {
                String value = matcher.group(1).trim().toLowerCase().replace("_", "-");
                // 验证提取的值是否是有效的访问权限
                for (String validAccess : accessTypes) {
                    if (validAccess.equals(value)) {
                        return value;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 从文本中提取状态
     * 支持 standard MIB STATUS 子句的各种格式
     */
    private String extractStatusFromText(String text) {
        // 定义所有可能的状态类型
        String[] statusTypes = {"current", "deprecated", "obsolete", "mandatory", "optional"};

        // 首先尝试简单匹配
        String lowerText = text.toLowerCase();
        for (String status : statusTypes) {
            if (lowerText.contains("status " + status) ||
                    lowerText.contains("status{" + status) ||
                    lowerText.contains(status + "}") ||
                    lowerText.contains(status + ",") ||
                    lowerText.contains(status + "\n") ||
                    lowerText.contains(status + "\r")) {
                return status;
            }
        }

        // 使用正则表达式进行更精确的匹配
        String[] patterns = {
                "status\\s+\\{?([^}\\n\\r,;]+)\\}?",
                "status\\s+([^\\n\\r,;]+)"
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = regex.matcher(text);

            if (matcher.find()) {
                String value = matcher.group(1).trim().toLowerCase().replace("_", "-");
                // 验证提取的值是否是有效的状态
                for (String validStatus : statusTypes) {
                    if (validStatus.equals(value)) {
                        return value;
                    }
                }
            }
        }

        return null;
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

    // 缓存已解析的 MIB 树
    private Map<String, MibNode> oidNodeCache = new HashMap<>();

    /**
     * 根据 OID 查找 MIB 节点信息
     *
     * @param oid 要查找的 OID
     * @return MIB 节点信息，如果未找到则返回 null
     */
    public MibNode findNodeByOid(String oid) {
        if (oid == null || oid.trim().isEmpty()) {
            return null;
        }

        // 如果缓存为空，先加载标准 MIB
        if (oidNodeCache.isEmpty()) {
            loadStandardMibs();
        }

        // 直接查找
        MibNode node = oidNodeCache.get(oid);
        if (node != null) {
            return node;
        }

        // 如果没找到，尝试查找最接近的父节点
        return findClosestParentNode(oid);
    }

    /**
     * 加载标准 MIB 文件到缓存
     */
    private void loadStandardMibs() {
        try {
            log.info("开始加载标准 MIB 文件到缓存");
            // 只加载一些关键的标准 MIB 文件，避免加载过多文件导致性能问题
            String[] coreMibs = {
                    "SNMPv2-MIB",    // 基础 SNMPv2 MIB
                    "RFC1213-MIB",    // 基础 MIB-II
                    "IF-MIB",         // 接口 MIB
                    "IP-MIB",         // IP MIB
                    "TCP-MIB",        // TCP MIB
                    "UDP-MIB"         // UDP MIB
            };

            List<MibNode> nodes = parseMibFiles(coreMibs);

            if (!nodes.isEmpty()) {
                buildOidCache(nodes.get(0));
                log.info("标准 MIB 文件缓存加载完成，缓存节点数: {}", oidNodeCache.size());
            }
        } catch (Exception e) {
            log.warn("加载标准 MIB 文件到缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 递归构建 OID 缓存
     */
    private void buildOidCache(MibNode node) {
        if (node == null) return;

        // 将当前节点添加到缓存
        if (node.getOid() != null && !node.getOid().isEmpty()) {
            oidNodeCache.put(node.getOid(), node);
        }

        // 递归处理子节点
        if (node.getChildren() != null) {
            for (MibNode child : node.getChildren()) {
                buildOidCache(child);
            }
        }
    }

    /**
     * 查找最接近的父节点
     */
    private MibNode findClosestParentNode(String oid) {
        String[] parts = oid.split("\\.");

        // 从最长的可能 OID 开始查找
        for (int i = parts.length; i >= 1; i--) {
            StringBuilder parentOid = new StringBuilder();
            for (int j = 0; j < i; j++) {
                if (j > 0) {
                    parentOid.append(".");
                }
                parentOid.append(parts[j]);
            }

            MibNode node = oidNodeCache.get(parentOid.toString());
            if (node != null) {
                return node;
            }
        }

        return null;
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        oidNodeCache.clear();
        log.info("MIB 节点缓存已清空");
    }
}