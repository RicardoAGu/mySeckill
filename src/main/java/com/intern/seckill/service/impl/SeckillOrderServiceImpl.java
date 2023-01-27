package com.intern.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intern.seckill.mapper.SeckillOrderMapper;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.pojo.User;
import com.intern.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-24
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取秒杀结果
     * @return:秒杀成功：orderId；秒杀失败：-1；排队中：0
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) return seckillOrder.getOrderId();
        else if (redisTemplate.hasKey("isEmptyKey:" + goodsId)) return -1L;
        else return 0L;
    }

}
