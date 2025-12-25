package com.zzhow.magicmibbackend.repository;

import com.zzhow.magicmibbackend.pojo.entity.MibNode;

import java.util.List;

/**
 * MIB 文件存储库
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
public class MibRepository {
    // MIB 文件名数组
    public static String[] mibFiles;
    // MIB 树
    public static List<MibNode> mibTree;
}
