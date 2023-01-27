package com.intern.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.pojo.User;
import com.intern.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-24
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @return:orderId:成功，-1；秒杀失败，0；排队中，1
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    Long getResult(User user, Long goodsId);

}
