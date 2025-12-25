package com.zzhow.magicmibbackend.service;

import com.zzhow.magicmibbackend.pojo.dto.MibDTO;
import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import com.zzhow.magicmibbackend.pojo.vo.MibVO;
import com.zzhow.magicmibbackend.result.Result;

import java.util.List;

/**
 * MIB 文件服务类接口
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
public interface MibService {
    /**
     * 加载MIB文件
     *
     * @param mibDTO MIB 文件信息传输模型
     * @return MIB 文件解析结果（JSON）
     */
    Result<List<MibNode>> loadMib(MibDTO mibDTO);

    /**
     * 获取当前加载的 MIB 文件
     *
     * @return MIB 文件信息视图
     */
    Result<MibVO> getMib();
}
