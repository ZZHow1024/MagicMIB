package com.zzhow.magicmibbackend.pojo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * MIB 结点实体类
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MibNode {
    // ID
    private String id;
    // 标签
    private String label;
    // OID
    private String oid;
    // 所属的 MIB 模块名称
    private String mib;
    // 对象的数据类型语法定义
    private String syntax;
    // 对象的访问权限：read-only, read-write, not-accessible等
    private String access;
    // 对象状态：current, deprecated, obsolete等
    private String status;
    // 对象的详细描述信息
    private String description;
    // 子节点列表，用于构建 MIB 树形结构
    private List<MibNode> children = new ArrayList<>();

    public MibNode(String id, String label, String oid, String mib, String syntax, String access, String status, String description) {
        this.id = id;
        this.label = label;
        this.oid = oid;
        this.mib = mib;
        this.syntax = syntax;
        this.access = access;
        this.status = status;
        this.description = description;
    }
}