package com.intern.seckill.controller;

import com.intern.seckill.pojo.User;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.service.IUserService;
import com.intern.seckill.vo.DetailVo;
import com.intern.seckill.vo.GoodsVo;
import com.intern.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转商品页面
     * 优化前QPS：3110.4
     * 页面缓存之后QPS：6872.9
     *
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        // 从redis中获取页面，如果不为空，直接返回页面。
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) return html;
        // 如果为空，则手动渲染页面，存入redis中再返回。
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        // 手动渲染页面
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页面
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(@PathVariable Long goodsId, Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        // 从redis中获取页面，如果不为空，直接返回页面。
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (!StringUtils.isEmpty(html)) return html;
        // 如果为空，则手动渲染页面，存入redis中再返回。

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
        // 手动渲染页面
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页面
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(@PathVariable Long goodsId, Model model, User user) {
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

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);
    }

}
