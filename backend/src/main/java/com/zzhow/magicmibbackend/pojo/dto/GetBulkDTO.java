package com.zzhow.magicmibbackend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GetBulk 请求信息传输模型
 *
 * @author ZZHow
 * create 2025/12/2
 * update 2025/12/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetBulkDTO {
    // n，非重复变量数
    private String nonRepeaters;

    // m，最大重复次数
    private String maxRepetitions;

    // 对象标识符数组
    private String[] oids;
}
