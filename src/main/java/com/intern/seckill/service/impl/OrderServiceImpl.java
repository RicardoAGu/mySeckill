package com.intern.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intern.seckill.exception.GlobalException;
import com.intern.seckill.mapper.OrderMapper;
import com.intern.seckill.pojo.Order;
import com.intern.seckill.pojo.SeckillGoods;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.pojo.User;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.service.IOrderService;
import com.intern.seckill.service.ISeckillGoodsService;
import com.intern.seckill.service.ISeckillOrderService;
import com.intern.seckill.vo.GoodsVo;
import com.intern.seckill.vo.OrderDetailVo;
import com.intern.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-24
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀功能的实现
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean seckillResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().
                setSql("stock_count = " + "stock_count - 1").eq("goods_id", goodsVo.getId()).gt("stock_count", 0));
        if (seckillGoods.getStockCount() < 1) valueOperations.set("isEmptyStock:" + goodsVo.getId(), "0");
        if (!seckillResult) {
            return null;
        }
        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goodsVo.getId(), seckillOrder);
        return order;
    }

    /**
     * 订单详情
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }

}
