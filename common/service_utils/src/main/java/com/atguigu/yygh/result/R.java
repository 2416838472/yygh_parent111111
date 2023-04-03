package com.atguigu.yygh.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R {

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    //构造私有
    private R() { }

    //操作成功
    public static R ok() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(20000);
        r.setMessage("成功");
        return r;
    }

    //操作失败
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(20001);
        r.setMessage("失败");
        return r;
    }

    //设置data数据的方法
    public R data(String key,Object value) {
        this.data.put(key,value);
        return this;
    }

    public R data(Map<String, Object> data) {
        this.setData(data);
        return this;
    }

    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    //链式编程
    public R code(Integer code){
        this.setCode(code); //this指向当前对象,当前对象是R,所以可以调用R的方法
        return this;
    }

}
