package com.intern.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intern.seckill.pojo.User;
import com.intern.seckill.vo.LoginVo;
import com.intern.seckill.vo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-15
 */
public interface IUserService extends IService<User> {
    /**
     * 登录
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    RespBean doLogin(LoginVo loginVo);
}
