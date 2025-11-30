package com.zzhow.magicmibbackend;

import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import com.zzhow.magicmibbackend.util.MibParseUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MIB 解析工具类的测试类
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
@SpringBootTest
public class SimpleMibParseUtilTest {
    @Autowired
    private MibParseUtil mibParseUtil;

    @Test
    @DisplayName("测试解析标准 MIB 文件名称")
    public void testStandardMibNames() {
        String[] standardMibs = mibParseUtil.getStandardMibs();
        System.out.println("支持的标准 MIB 文件数量: " + standardMibs.length);

        // 测试一些常见 MIB 是否为标准 MIB
        System.out.println("IF-MIB 是否为标准MIB: " + mibParseUtil.isStandardMib("IF-MIB"));
        System.out.println("RFC1213-MIB 是否为标准MIB: " + mibParseUtil.isStandardMib("RFC1213-MIB"));
        System.out.println("CUSTOM-MIB 是否为标准MIB: " + mibParseUtil.isStandardMib("CUSTOM-MIB"));

        // 测试加载几个常见的标准 MIB 文件
        try {
            String[] mibNames = {"SNMPv2-MIB", "IF-MIB"};
            List<MibNode> nodes = mibParseUtil.parseMibFiles(mibNames);
            System.out.println("成功加载标准MIB文件，根节点数量: " + nodes.size());

            // 打印根节点信息
            for (int i = 0; i < nodes.size(); i++) {
                MibNode node = nodes.get(i);
                System.out.printf("根节点%d: 标签=%s, OID=%s, 子节点数=%d%n",
                        i + 1, node.getLabel(), node.getOid(),
                        node.getChildren() != null ? node.getChildren().size() : 0);

                // 打印前几个子节点
                if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                    int childCount = 0;
                    for (MibNode child : node.getChildren()) {
                        if (childCount < 3) {
                            System.out.printf("  子节点%d: %s (%s)%n",
                                    childCount + 1, child.getLabel(), child.getOid());
                        }
                        childCount++;
                    }
                    System.out.println("  ... (更多子节点)");
                }
            }
        } catch (Exception e) {
            System.err.println("加载标准 MIB 文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("测试解析单个标准MIB文件")
    public void testParseSingleMibFile() {
        try {
            // 测试加载单个标准 MIB 文件
            List<MibNode> nodes = mibParseUtil.parseSingleMibFile("SNMPv2-MIB");
            System.out.println("解析SNMPv2-MIB完成，根节点数量: " + nodes.size());

            // 验证根节点
            if (nodes.size() == 1) {
                MibNode root = nodes.get(0);
                System.out.printf("根节点: %s (%s)%n", root.getLabel(), root.getOid());

                // 递归统计节点总数
                int totalNodeCount = countTotalNodes(root);
                System.out.println("SNMPv2-MIB总节点数: " + totalNodeCount);

                // 打印树形结构前几层
                System.out.println("\n树形结构前3层:");
                printTreeStructure(root, 0, 3);

                // 查找并打印 mgmt 分支下的节点
                MibNode mgmt = findNodeByPath(root, "1.3.6.1.2");
                if (mgmt != null) {
                    System.out.printf("\nmgmt分支: %s (%s), 子节点数: %d%n",
                            mgmt.getLabel(), mgmt.getOid(), mgmt.getChildren().size());

                    // 打印 mgmt 下的子节点
                    int childCount = 0;
                    for (MibNode child : mgmt.getChildren()) {
                        if (childCount < 5) {
                            System.out.printf("  %s (%s) - 子节点数: %d%n",
                                    child.getLabel(), child.getOid(),
                                    child.getChildren() != null ? child.getChildren().size() : 0);
                        }
                        childCount++;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("解析 SNMPv2-MIB 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("测试解析多个标准 MIB 文件")
    public void testParseMultipleMibFiles() {
        try {
            // 测试加载多个标准 MIB 文件
            String[] mibNames = {"SNMPv2-MIB", "IF-MIB", "IP-MIB"};
            List<MibNode> nodes = mibParseUtil.parseMibFiles(mibNames);

            System.out.println("解析完成，根节点数量: " + nodes.size());

            if (nodes.size() == 1) {
                MibNode root = nodes.get(0);
                System.out.printf("根节点: %s (%s)%n", root.getLabel(), root.getOid());

                // 统计总节点数和各 MIB 模块的节点数量
                int totalNodeCount = countTotalNodes(root);
                System.out.println("总节点数: " + totalNodeCount);

                Map<String, Integer> mibNodeCount = new HashMap<>();
                countMibNodes(root, mibNodeCount);

                System.out.println("各MIB模块节点统计:");
                for (Map.Entry<String, Integer> entry : mibNodeCount.entrySet()) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " 个节点");
                }

                // 查找并显示 mib-2 分支
                MibNode mib2 = findNodeByPath(root, "1.3.6.1.2.1");
                if (mib2 != null) {
                    System.out.printf("\nmib-2 分支: %s (%s), 直接子节点数: %d%n",
                            mib2.getLabel(), mib2.getOid(), mib2.getChildren().size());

                    // 打印 mib-2 下的主要子节点
                    int childCount = 0;
                    for (MibNode child : mib2.getChildren()) {
                        if (childCount < 10) { // 只打印前 10 个子节点
                            System.out.printf("  %s (%s) - MIB: %s, 子节点数: %d%n",
                                    child.getLabel(), child.getOid(), child.getMib(),
                                    child.getChildren() != null ? child.getChildren().size() : 0);
                        }
                        childCount++;
                    }
                    if (mib2.getChildren().size() > 10) {
                        System.out.println("  ... (还有 " + (mib2.getChildren().size() - 10) + " 个子节点)");
                    }
                }

                // 查找并显示 IF-MIB 相关的节点
                System.out.println("\nIF-MIB 相关节点:");
                findAndPrintMibNodes(root, "IF-MIB", 5);
            }

            // 测试加载更多标准 MIB 文件
            System.out.println("\n--- 测试加载更多标准 MIB 文件 ---");
            String[] moreMibs = {"RFC1213-MIB", "TCP-MIB", "UDP-MIB"};
            try {
                List<MibNode> moreNodes = mibParseUtil.parseMibFiles(moreMibs);
                if (moreNodes.size() == 1) {
                    int moreTotalCount = countTotalNodes(moreNodes.get(0));
                    System.out.println("成功加载 " + moreMibs.length + " 个额外 MIB 文件，总节点数: " + moreTotalCount);
                }
            } catch (Exception e) {
                System.out.println("加载部分 MIB 文件时遇到问题（这可能是正常的，某些 MIB 可能不存在）: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("解析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 递归统计节点总数
     */
    private int countTotalNodes(MibNode node) {
        if (node == null) return 0;

        int count = 1; // 当前节点
        if (node.getChildren() != null) {
            for (MibNode child : node.getChildren()) {
                count += countTotalNodes(child);
            }
        }
        return count;
    }

    /**
     * 递归统计各 MIB 模块的节点数量
     */
    private void countMibNodes(MibNode node, Map<String, Integer> mibNodeCount) {
        if (node == null) return;

        String mibName = node.getMib();
        mibNodeCount.put(mibName, mibNodeCount.getOrDefault(mibName, 0) + 1);

        if (node.getChildren() != null) {
            for (MibNode child : node.getChildren()) {
                countMibNodes(child, mibNodeCount);
            }
        }
    }

    /**
     * 根据 OID 路径查找节点
     */
    private MibNode findNodeByPath(MibNode root, String oidPath) {
        if (root == null || oidPath == null) return null;

        if (oidPath.equals(root.getOid())) {
            return root;
        }

        if (root.getChildren() != null) {
            for (MibNode child : root.getChildren()) {
                MibNode found = findNodeByPath(child, oidPath);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * 打印树形结构
     */
    private void printTreeStructure(MibNode node, int depth, int maxDepth) {
        if (node == null || depth > maxDepth) return;

        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }

        System.out.printf("%s%s (%s) - MIB: %s%n",
                indent.toString(), node.getLabel(), node.getOid(), node.getMib());

        if (node.getChildren() != null) {
            for (MibNode child : node.getChildren()) {
                printTreeStructure(child, depth + 1, maxDepth);
            }
        }
    }

    /**
     * 查找并打印指定 MIB 模块的节点，返回找到的数量
     */
    private int findAndPrintMibNodes(MibNode node, String mibName, int maxCount) {
        if (node == null) return 0;

        int found = 0;
        if (mibName.equals(node.getMib())) {
            System.out.printf("  %s (%s) - 语法: %s%n", node.getLabel(), node.getOid(), node.getSyntax());
            found = 1;
        }

        if (node.getChildren() != null && found < maxCount) {
            for (MibNode child : node.getChildren()) {
                if (found >= maxCount) break;
                found += findAndPrintMibNodes(child, mibName, maxCount - found);
            }
        }

        return found;
    }
}
