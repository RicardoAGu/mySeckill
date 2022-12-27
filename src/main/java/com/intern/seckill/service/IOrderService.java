package com.intern.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intern.seckill.pojo.Order;
import com.intern.seckill.pojo.User;
import com.intern.seckill.vo.GoodsVo;
import com.intern.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-24
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀功能的实现
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    Order seckill(User user, GoodsVo goodsVo);

    /**
     * 订单详情
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    OrderDetailVo detail(Long orderId);
}
