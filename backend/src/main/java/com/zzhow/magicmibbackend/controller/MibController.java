package com.zzhow.magicmibbackend.controller;

import com.zzhow.magicmibbackend.pojo.dto.MibDTO;
import com.zzhow.magicmibbackend.pojo.entity.MibNode;
import com.zzhow.magicmibbackend.pojo.vo.MibVO;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.MibService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MIB 文件控制类
 *
 * @author ZZHow
 * create 2025/11/30
 * update 2025/11/30
 */
@Slf4j
@RestController
@RequestMapping("/api/mib")
public class MibController {
    @Autowired
    private MibService mibService;

    /**
     * 加载 MIB 文件
     *
     * @param mibDTO MIB 文件信息传输模型
     * @return MIB 文件解析结果（JSON）
     */
    @PostMapping("/load")
    public Result<List<MibNode>> loadMib(@RequestBody MibDTO mibDTO) {
        log.info("加载 MIB 文件：mibDTO = {}", mibDTO);

        return mibService.loadMib(mibDTO);
    }

    /**
     * 获取当前加载的 MIB 文件
     *
     * @return MIB 文件解析结果（JSON）
     */
    @GetMapping("/get")
    public Result<MibVO> getMib() {
        log.info("获取当前加载的 MIB 文件");

        return mibService.getMib();
    }
}
