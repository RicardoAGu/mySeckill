package com.intern.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intern.seckill.exception.GlobalException;
import com.intern.seckill.mapper.UserMapper;
import com.intern.seckill.pojo.User;
import com.intern.seckill.service.IUserService;
import com.intern.seckill.utils.MD5Util;
import com.intern.seckill.utils.PhoneNumberValidator;
import com.intern.seckill.vo.LoginVo;
import com.intern.seckill.vo.RespBean;
import com.intern.seckill.vo.RespBeanEnum;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;

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

    /**
     * 登录
     * @author Ricardo.A.Gu
     * @since 1.0.0
     */
    @Override
    public RespBean doLogin(LoginVo loginVo) {
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
        return RespBean.success();
    }
}
