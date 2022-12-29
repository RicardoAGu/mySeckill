package com.intern.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.service.IOrderService;
import com.intern.seckill.service.ISeckillOrderService;
import com.intern.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.intern.seckill.pojo.User;
import com.intern.seckill.pojo.Order;
import com.intern.seckill.vo.RespBeanEnum;
import com.intern.seckill.vo.GoodsVo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 秒杀功能实现
     * windows优化前QPS：3019.3
     *
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/doSeckill")
    public String doSeckill2(Model model, User user, Long goodsId) {
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

    /**
     * 秒杀功能实现
     * windows优化前QPS：3019.3
     *
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(Model model, User user, Long goodsId) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        // 判断库存是否足够
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 判断同一用户是否对同一商品多次下单
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        Order order = orderService.seckill(user, goodsVo);
        return RespBean.success(order);
    }


}
