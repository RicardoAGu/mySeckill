package com.intern.seckill.controller;


import com.intern.seckill.pojo.User;
import com.intern.seckill.rabbitmq.MQSender;
import com.intern.seckill.vo.RespBean;
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
 * @since 2022-12-15
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * 返回用户信息（测试专用）
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

    /**
     * RabbitMQ消息发送功能测试
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.send("Hello");
    }

    /**
     * Fanout交换机模式，消息发送功能测试
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq_fanout() {
        mqSender.send("Hello");
    }

    /**
     * Direct交换机模式，消息发送功能测试
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq_direct01() {
        mqSender.send01("Hello,red");
    }

    /**
     * Direct交换机模式，消息发送功能测试
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq_direct02() {
        mqSender.send02("Hello,green");
    }

    /**
     * Topic交换机模式，消息发送功能测试
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void mq_topic01() {
        mqSender.send03("Hello,topic01");
    }

    /**
     * Topic交换机模式，消息发送功能测试
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void mq_topic02() {
        mqSender.send04("Hello,topic01 and topic02");
    }
}
