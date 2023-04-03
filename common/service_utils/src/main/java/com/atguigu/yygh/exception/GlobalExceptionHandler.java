package com.atguigu.yygh.exception;

import com.atguigu.yygh.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R error(Exception e) {
        e.printStackTrace();
        return R.error().message("执行全局异常处理");
    }

    //ArithmeticException
    @ExceptionHandler(ArithmeticException.class)
    public R error(ArithmeticException e) {
        e.printStackTrace();
        return R.error().message("执行特定异常处理");
    }

    @ExceptionHandler(YyghException.class)
    public R error(YyghException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().message(e.getMsg()).code(e.getCode());
    }
}
