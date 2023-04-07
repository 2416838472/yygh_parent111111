package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.result.R;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api("登录管理")
@RestController
@CrossOrigin
@RequestMapping("/admin")
public class UserInfoApiController {


//    @Autowired
//    private UserInfoService userInfoService;
//
//    // 会员登录
//    @ApiOperation(value = "会员登录")
//    @PostMapping("login")
//    public R login(@RequestBody LoginVo loginVo) {
//        Map<String,Object> token = userInfoService.login(loginVo);
//        return R.ok().data("token", token);
//    }
//
//    //获取会员信息
//    @ApiOperation(value = "获取会员信息")
//    @PostMapping("auth/getUserInfo")
//    public R getUserInfo() {
//        return R.ok();
//    }
//
//
//    //退出
//    @ApiOperation(value = "退出登录")
//    @PostMapping("logout")
//    public R logout() {
//        return R.ok();
//    }


    /**
     * 管理员登录
     * @return
     */
    @PostMapping("/user/login")
    public R login(){
        return R.ok().data("token","admin-token");
    }

    /**
     * 登录成功后获取用户信息接口
     * @return
     */
    @GetMapping("/user/info")
    public R info(){
//        {"code":20000,
//                "data":{"roles":["admin"],
        //                  "introduction":"I am a super administrator",
        //                "avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
        //                "name":"Super Admin"}
//        }
        return R.ok()
                .data("roles", Arrays.asList("admin"))
                .data("introduction","我是尚医通后台管理员")
                .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name","尚医通管理员");
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping("/user/logout")
    public R logout(){
        return R.ok();
    }


}
