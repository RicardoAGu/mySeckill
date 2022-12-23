package com.intern.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intern.seckill.exception.GlobalException;
import com.intern.seckill.mapper.UserMapper;
import com.intern.seckill.pojo.User;
import com.intern.seckill.service.IUserService;
import com.intern.seckill.utils.CookieUtil;
import com.intern.seckill.utils.MD5Util;
import com.intern.seckill.utils.UUIDUtil;
import com.intern.seckill.vo.LoginVo;
import com.intern.seckill.vo.RespBean;
import com.intern.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ricardo.A.Gu
 * @since 2022-12-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // 参数校验
//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        if (!PhoneNumberValidator.isPhoneNumber(mobile)) return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        // 根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (user == null) throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        // 判断密码是否正确
        if (!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())) throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        // 登录成功，生成cookie，把cookie和用户对象存到session中去。
        String ticket = UUIDUtil.uuid();
        // httpServletRequest.getSession().setAttribute(ticket, user);
        // 将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(httpServletRequest, httpServletResponse, "UserTicket", ticket);
        return RespBean.success();
    }

    /**
     * 根据cookie获取用户
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (userTicket == null) return null;
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        // cookie有时效的，以防万一cookie失效，当我们获取到用户时再为它设定一次cookie
        if (user != null) {
            CookieUtil.setCookie(httpServletRequest, httpServletResponse, "UserTicket", userTicket);
        }
        return user;
    }
}
