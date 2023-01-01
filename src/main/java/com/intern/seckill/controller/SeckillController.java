package com.intern.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.intern.seckill.exception.GlobalException;
import com.intern.seckill.pojo.SeckillMessage;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.rabbitmq.MQSender;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.service.IOrderService;
import com.intern.seckill.service.ISeckillOrderService;
import com.intern.seckill.utils.JsonUtil;
import com.intern.seckill.vo.RespBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import com.intern.seckill.pojo.User;
import com.intern.seckill.pojo.Order;
import com.intern.seckill.vo.RespBeanEnum;
import com.intern.seckill.vo.GoodsVo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 秒杀功能实现
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Long> redisScript;
//    @Autowired
//    private RedisConnection redisConnection;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();


//    /**
//     * 秒杀功能实现
//     * windows优化前QPS：3019.3
//     *
//     * @author Ricardo.A.Gu
//     * @since 1.0.0
//     */
//    @RequestMapping("/doSeckill")
//    public String doSeckill2(Model model, User user, Long goodsId) {
//        if (user == null) return "login";
//        model.addAttribute("user", user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        // 判断库存是否足够
//        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
//        // 判断同一用户是否对同一商品多次下单
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "secKillFail";
//        }
//        Order order = orderService.seckill(user, goodsVo);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//        return "orderDetail";
//    }

    /**
     * 秒杀功能实现
     * windows优化前QPS：3019.3
     * windows优化后QPS：项目刚启动后的第一次压测一般在9000以下，第二次压测之后开始一般在9000以上
     *
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(Model model, User user, Long goodsId) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        // 内存标记，减少对redis的访问
        if (emptyStockMap.get(goodsId)) return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 判断同一用户是否对同一商品多次下单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        // 预减库存
        // Long stock =  valueOperations.decrement("seckillGoods:" + goodsId);
        List<String> KEYS = new ArrayList<>();
        KEYS.add("seckillGoods:" + goodsId);
        KEYS.add(user.getId() + ":" + goodsId);
        Long stock = (Long) redisTemplate.execute(redisScript, KEYS, Collections.EMPTY_LIST);
        if (stock == -1) {
            emptyStockMap.put(goodsId, true);
            //valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        } else if (stock == -2) {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        /*
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
         */
        return RespBean.success(0);
    }

    /**
     * 获取秒杀结果
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }



    /**
     * 将库存信息初始化加载到redis中去
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) return;
        list.forEach(goodsVo -> {
            emptyStockMap.put(goodsVo.getId(), false);
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(), goodsVo.getStockCount());
        });
    }
}
