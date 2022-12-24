package com.intern.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 公共返回对象枚举
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    // 通用的枚举值
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    // 登录模块专用枚举项，5002xx
    LOGIN_ERROR(500210, "用户名或密码错误"),
    MOBILE_ERROR(500211, "手机号码格式错误"),
    BIND_ERROR(500212, "参数校验异常"),
    DOMAIN_ERROR(500213, "域解析错误"),
    // 秒杀模块专用枚举项，5005xx
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品每人限购一件");

    private final Integer code;
    private final String message;
}
