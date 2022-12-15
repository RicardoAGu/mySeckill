package com.intern.seckill.exception;

import com.intern.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 全局异常
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {
    private RespBeanEnum respBeanEnum;
}
