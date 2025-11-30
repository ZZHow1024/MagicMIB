package com.zzhow.magicmibbackend.service.impl;

import com.zzhow.magicmibbackend.pojo.dto.MibDTO;
import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.MibService;
import com.zzhow.magicmibbackend.util.MibParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MIB 文件服务实现类
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
@Service
public class MibServiceImpl implements MibService {
    @Autowired
    private MibParseUtil mibParseUtil;

    /**
     * 加载MIB文件
     *
     * @param mibDTO MIB 文件信息传输模型
     * @return MIB 文件解析结果（JSON）
     */
    @Override
    public Result<List<MibNode>> loadMib(MibDTO mibDTO) {
        return Result.success(mibParseUtil.parseMibFiles(mibDTO.getMibFiles()));
    }
}
