package com.zzhow.magicmibbackend.pojo.vo;

import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MIB 文件信息视图
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MibVO {
    // MIB 文件名数组
    private String[] mibFiles;
    // MIB 树
    private List<MibNode> mibTree;
}
