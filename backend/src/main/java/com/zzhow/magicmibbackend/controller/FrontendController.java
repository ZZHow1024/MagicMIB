package com.zzhow.magicmibbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前端控制类
 *
 * @author ZZHow
 * create 2025/12/5
 * update 2025/12/5
 */
@Controller
public class FrontendController {
    @RequestMapping("/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}