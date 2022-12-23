package com.intern.seckill.controller;

import com.intern.seckill.pojo.User;
import com.intern.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 商品
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;

    /**
     * 跳转商品页面
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/toList")
    public String toList(Model model, User user) {
        // if (StringUtils.isEmpty(ticket)) return "login";
        // // User user = (User) session.getAttribute(ticket);
        // User user = userService.getUserByCookie(ticket, httpServletRequest, httpServletResponse);
        // if (user == null) return "login";
        model.addAttribute("user", user);
        return "goodsList";
    }
}
