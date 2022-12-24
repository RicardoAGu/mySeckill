package com.intern.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intern.seckill.mapper.GoodsMapper;
import com.intern.seckill.pojo.Goods;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired GoodsMapper goodsMapper;

    /**
     * 获取商品列表
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 获取商品详情
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
