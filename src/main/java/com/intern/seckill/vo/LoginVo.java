package com.intern.seckill.vo;

import com.intern.seckill.validator.IsPhoneNumber;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录参数
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Data
public class LoginVo {
    @NotNull
    @IsPhoneNumber
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
