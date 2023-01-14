package com.intern.seckill.controller;

import com.intern.seckill.config.AccessLimit;
import com.intern.seckill.exception.GlobalException;
import com.intern.seckill.pojo.SeckillMessage;
import com.intern.seckill.pojo.SeckillOrder;
import com.intern.seckill.rabbitmq.MQSender;
import com.intern.seckill.service.IGoodsService;
import com.intern.seckill.service.IOrderService;
import com.intern.seckill.service.ISeckillOrderService;
import com.intern.seckill.utils.JsonUtil;
import com.intern.seckill.vo.RespBean;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.intern.seckill.pojo.User;
import com.intern.seckill.vo.RespBeanEnum;
import com.intern.seckill.vo.GoodsVo;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀功能实现
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Slf4j
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
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 内存标记，减少对redis的访问
        if (emptyStockMap.get(goodsId)) return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        // 判断同一用户是否对同一商品多次下单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        // 预减库存
        // Long stock =  valueOperations.decrement("seckillGoods:" + goodsId);
        List<String> KEYS = new ArrayList<>();
        KEYS.add("seckillGoodsStock:" + goodsId);
        KEYS.add("operationStatus:" + user.getId() + ":" + goodsId);
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
     * 获取真正的秒杀接口路径
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    @AccessLimit(second=5, maxCount=5, needLogin=true)
    public RespBean getPath(User user, Long goodsId, String captcha) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * 生成验证码
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void generateCaptcha(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0) throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        // 设置响应头输出类型为图片
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-control", "No-cache");
        response.setDateHeader("Expires", 0);
        // 生成验证码,结果存入redis中
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败:" + e.getMessage());
        }
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
            redisTemplate.opsForValue().set("seckillGoodsStock:"+goodsVo.getId(), goodsVo.getStockCount());
        });
    }
}
