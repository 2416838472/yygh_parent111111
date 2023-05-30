package com.atguigu.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.model.user.UserInfo;
import com.atguigu.yygh.result.Result;
import com.atguigu.yygh.user.helper.JwtHelper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.util.ConstantPropertiesUtil;
import com.atguigu.yygh.user.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {

    @Autowired
    UserInfoService userInfoService;



    @GetMapping("getLoginParam")
    public Result genOrConnect() throws UnsupportedEncodingException {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
            map.put("scope", "snsapi_login");
            String wxOpenRedirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "UTF-8");
            map.put("redirect_uri", "snsapi_login");
            map.put("redirect_uri", wxOpenRedirectUrl);
            map.put("state", System.currentTimeMillis() + "");
            return Result.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }


    @GetMapping("callback")
    public String callback(String code) {
        //1、微信端确认后，回调该接口，获取临时票据 code
        System.out.println("code:" + code);

        try {

            //2、根据临时票据code获取access_token 和 openid
            String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                    "appid=" + ConstantPropertiesUtil.WX_OPEN_APP_ID + "" +
                    "&secret=" + ConstantPropertiesUtil.WX_OPEN_APP_SECRET + "" +
                    "&code=" + code +
                    "&grant_type=authorization_code";

            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);

            //得到 openid 和 access_token
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            //3、根据openid从mysql中查询用户
            UserInfo userInfo = userInfoService.selectWxInfoByOpenId(openid);

            if (userInfo == null) {
                //4、根据 access_token 和 openid 获取微信用户昵称
                String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?" +
                        "access_token=" + access_token + "" +
                        "&openid=" + openid + "" +
                        "&lang=zh_CN";

                String resultInfo = HttpClientUtils.get(userInfoUrl);
                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);

                String nickname = resultUserInfoJson.getString("nickname");

                //5、添加该微信用户
                userInfo = new UserInfo();

                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);

                userInfoService.save(userInfo);
            }

            //------------------------------------------------
            //6、返回name+token+openid（phone为空时返回openid）
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
                if (StringUtils.isEmpty(name)) {
                    name = userInfo.getPhone();
                }
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);

            //重定向callback.vue，并传递三个参数
            return "redirect:http://localhost:8203/weixin/callback?" +
                    "name=" + URLEncoder.encode(name, "utf-8") + "&" +
                    "token=" + token + "&" +
                    "openid=" + (StringUtils.isEmpty(userInfo.getPhone())
                    ? userInfo.getOpenid() : "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


