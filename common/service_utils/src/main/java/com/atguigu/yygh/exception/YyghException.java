package com.atguigu.yygh.exception;

import com.atguigu.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YyghException extends RuntimeException {

    private Integer code;

    private String msg;

}
