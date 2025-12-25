package com.zzhow.magicmibbackend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MIB 文件信息传输模型
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MibDTO {
    // MIB 文件名数组
    private String[] mibFiles;
}
