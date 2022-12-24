package com.intern.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intern.seckill.pojo.Goods;
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
public interface IGoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
