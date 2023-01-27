package com.intern.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-24
 */
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

    /**
     * 获取商品秒杀订单列表
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    List<SeckillOrder> findSeckillOrder();

}
