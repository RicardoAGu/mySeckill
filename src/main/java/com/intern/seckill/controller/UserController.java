package com.intern.seckill.controller;


import com.intern.seckill.pojo.User;
import com.intern.seckill.vo.RespBean;
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

}
