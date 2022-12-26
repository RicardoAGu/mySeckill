package com.intern.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.service.IOrderService;
import com.intern.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.intern.seckill.pojo.User;
import com.intern.seckill.pojo.Order;
import com.intern.seckill.vo.RespBeanEnum;
import com.intern.seckill.vo.GoodsVo;
import org.springframework.ui.Model;

/**
 * 秒杀功能实现
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;

    /**
     * 秒杀功能实现
     * windows优化前QPS：3019.3
     * linux优化前QPS：511.7
     *
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        if (user == null) return "login";
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        // 判断库存是否足够
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // 判断同一用户是否对同一商品多次下单
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user, goodsVo);
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";
    }


}
