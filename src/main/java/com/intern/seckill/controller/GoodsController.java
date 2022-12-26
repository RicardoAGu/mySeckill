package com.intern.seckill.controller;

import com.intern.seckill.pojo.User;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.service.IUserService;
import com.intern.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Date;
import java.util.List;

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
    @Autowired
    private IGoodsService goodsService;

    /**
     * 跳转商品页面
     * windows优化前QPS：3110.4
     * linux优化前QPS：402
     *
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
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
        model.addAttribute("goodsList", goodsVo);
        return "goodsList";
    }

    /**
     * 跳转商品详情页面
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(@PathVariable Long goodsId, Model model, User user) {
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态参数，0为未开始，1为进行中，2为已结束
        int secKillStatus = 0;
        // 距离秒杀开始还剩多少时间，0为已开始，-1为已结束
        int remainSeconds = 0;
        if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
        }
        else if (nowDate.after(startDate)) secKillStatus = 1;
        else remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goodsDetail";
    }

}
