package com.example.graduationprojectsingle.utils.loginUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.graduationprojectsingle.entity.user.TokenUser;
import com.example.graduationprojectsingle.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {
    //密钥
    private static final String SECRET = "zhangyuye";

    //生成token
    public String createToken(User user) {
        Map<String, Object> header = new HashMap<>();

        //过期时间(24小时)
        Calendar expire = Calendar.getInstance();
        expire.add(Calendar.SECOND, 60 * 60 * 24);

        return JWT.create()
                //header
                .withHeader(header)
                //payload
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("nickname", user.getNickname())
                .withClaim("role", user.getRole())
                .withExpiresAt(expire.getTime())
                //signature
                .sign(Algorithm.HMAC256(SECRET));
    }

    //验证token是否过期
    public static boolean tokenIsValid(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT verify = jwtVerifier.verify(token);
        return verify.getExpiresAt().getTime() > new Date().getTime();
    }

    //解析token
    public TokenUser getUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT verify = jwtVerifier.verify(token);
        TokenUser tokenUser = new TokenUser();
        tokenUser.setId(verify.getClaim("id").asLong());
        tokenUser.setUsername(String.valueOf(verify.getClaim("username")));
        tokenUser.setNickname(String.valueOf(verify.getClaim("nickname")));
        tokenUser.setRole(verify.getClaim("role").asInt());

        return tokenUser;
    }
}
