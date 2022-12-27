package com.intern.seckill.controller;


import com.intern.seckill.pojo.User;
import com.intern.seckill.service.IOrderService;
import com.intern.seckill.vo.OrderDetailVo;
import com.intern.seckill.vo.RespBean;
import com.intern.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-24
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 订单详情
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }

}
