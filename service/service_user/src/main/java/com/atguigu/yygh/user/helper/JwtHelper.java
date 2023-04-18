package com.atguigu.yygh.user.helper;


import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {
    // token 过期时间为 24 小时
    private static long tokenExpiration = 24*60*60*1000;
    // token 签名密钥
    private static final String tokenSignKey = "123456";

    /**
     * 创建 token
     * @param userId 用户 ID
     * @param userName 用户名
     * @return token 字符串
     */
    public static String createToken(Long userId, String userName) {
        // 使用 Jwts.builder() 创建 token
        String token = Jwts.builder()
                // 设置 token 主题
                .setSubject("YYGH-USER")
                // 设置 token 过期时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                // 设置 token 中的用户 ID 和用户名
                .claim("userId", userId)
                .claim("userName", userName)
                // 使用 token 签名密钥进行签名
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                // 使用 GZIP 压缩
                .compressWith(CompressionCodecs.GZIP)
                // 生成 token 字符串
                .compact();
        return token;
    }

    /**
     * 获取 token 中的用户 ID
     * @param token token 字符串
     * @return 用户 ID
     */
    public static Long getUserId(String token) {
        // 如果 token 为空，则返回 null
        if(StringUtils.isEmpty(token)) return null;
        // 使用 Jwts.parser() 解析 token
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        // 获取 token 中的用户 ID
        Integer userId = (Integer)claims.get("userId");
        return userId.longValue();
    }

    /**
     * 获取 token 中的用户名
     * @param token token 字符串
     * @return 用户名
     */
    public static String getUserName(String token) {
        // 如果 token 为空，则返回空字符串
        if(StringUtils.isEmpty(token)) return "";
        // 使用 Jwts.parser() 解析 token
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        // 获取 token 中的用户名
        return (String)claims.get("userName");
    }
}
