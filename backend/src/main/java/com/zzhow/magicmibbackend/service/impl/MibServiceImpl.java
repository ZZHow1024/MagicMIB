package com.zzhow.magicmibbackend.service.impl;

import com.zzhow.magicmibbackend.pojo.dto.MibDTO;
import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import com.zzhow.magicmibbackend.pojo.vo.MibVO;
import com.zzhow.magicmibbackend.repository.MibRepository;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.MibService;
import com.zzhow.magicmibbackend.util.MibParseUtil;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        MibRepository.mibFiles = new String[]{"IF-MIB", "RFC1213-MIB"};
        MibRepository.mibTree = mibParseUtil.parseMibFiles(MibRepository.mibFiles);
    }

    /**
     * 加载MIB文件
     *
     * @param mibDTO MIB 文件信息传输模型
     * @return MIB 文件解析结果（JSON）
     */
    @Override
    public Result<List<MibNode>> loadMib(MibDTO mibDTO) {
        MibRepository.mibTree = mibParseUtil.parseMibFiles(mibDTO.getMibFiles());
        MibRepository.mibFiles = mibDTO.getMibFiles();

        return Result.success(MibRepository.mibTree);
    }

    /**
     * 获取当前加载的 MIB 文件
     *
     * @return MIB 文件信息视图
     */
    @Override
    public Result<MibVO> getMib() {
        return Result.success(MibVO.builder()
                .mibFiles(MibRepository.mibFiles)
                .mibTree(MibRepository.mibTree)
                .build());
    }
}
