package com.zzhow.magicmibbackend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SNMP 操作结果信息视图
 * 支持 Get/GetNext（单条数据）和 GetBulk/GetSubtree/Walk（多条数据）
 *
 * @author ZZHow
 * create 2025/12/3
 * update 2025/12/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnmpResultVO {
    // 操作类型
    private String operation;
    // 目标设备地址
    private String address;
    // 目标设备端口
    private Integer port;
    // 结果数据列表
    private java.util.List<SnmpDataVO> data;
    // 操作是否成功
    private Boolean success;
    // 错误信息
    private String errorMessage;
    // 执行耗时
    private Long executionTime;

    /**
     * SNMP 单条数据视图
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SnmpDataVO {
        // 对象名称
        private String name;
        // 对象 OID
        private String oid;
        // 对象值
        private String value;
        // 数据类型
        private String type;
        // 访问权限
        private String access;
        // 描述
        private String description;
    }
}