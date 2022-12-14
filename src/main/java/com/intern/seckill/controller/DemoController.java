package com.intern.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 测试
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Controller
@RequestMapping("/demo")
/*
RequestMapping是一个用来处理请求地址映射的注解，可用于类或方法上。
用于类上，表示类中的所有响应请求的方法都是以该地址作为父路径。
(RequestMapping注解的作用是建立请求URL和处理方法之间的对应关系)
 */
public class DemoController {
    /**
     * 功能描述：测试页面跳转
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("name", "顾傲广");
        return "hello";
    }
}
