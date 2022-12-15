package com.intern.seckill.vo;

import com.intern.seckill.utils.PhoneNumberValidator;
import com.intern.seckill.validator.IsPhoneNumber;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号码校验规则
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
public class IsMobileValidator implements ConstraintValidator<IsPhoneNumber, String> {
    private boolean required = false;
    @Override
    public void initialize(IsPhoneNumber constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required) {
            return PhoneNumberValidator.isPhoneNumber(s);
        } else {
            if (StringUtils.isEmpty(s)) return true;
            else return PhoneNumberValidator.isPhoneNumber(s);
        }
    }
}
