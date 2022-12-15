package com.intern.seckill.utils;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码校验类
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
public class PhoneNumberValidator {

    private static final Pattern mobile_pattern = Pattern.compile("1([3-9])[0-9]{9}$");

    public static boolean isPhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) return false;
        Matcher matcher = mobile_pattern.matcher(phoneNumber);
        return matcher.matches();
    }

}
