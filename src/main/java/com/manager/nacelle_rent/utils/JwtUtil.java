package com.manager.nacelle_rent.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.manager.nacelle_rent.entity.User;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    /** token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj */
    public static final String SECRET = "Kayla3loveDHH";

    /** token 过期时间: 10天 */
   // public static final int calendarField = Calendar.DATE;
   // public static final int calendarInterval = 10;

    public static String getToken(User user) {
        //Date iatDate = new Date();
        // expire time
        // Calendar nowTime = Calendar.getInstance();
        //  nowTime.add(calendarField, calendarInterval);
        // Date expiresDate = nowTime.getTime();

        // header Map
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        // build token
        // param backups
        String token = JWT.create().withHeader(map) // header
                .withClaim("iss", "kayla") // payload
                .withClaim("userId",user.getUserId())
                .withClaim("userRole",user.getUserRole())
                .withClaim("userName",user.getUserName())
             //   .withIssuedAt(iatDate) // sign time
             //   .withExpiresAt(expiresDate) // expire time
                .sign(Algorithm.HMAC256(SECRET)); // signature
        return token;
    }
    public static String getAndroidToken(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create().withHeader(map) // header
                .withClaim("iss", "kayla") // payload
                .withClaim("userPhone",user.getUserPhone())
                .withClaim("userRole",user.getUserRole())
                .withClaim("userName",user.getUserName())
                .sign(Algorithm.HMAC256(SECRET)); // signature
        return token;
    }
    //解析Token获得userId
    public static Map<String, Claim> verifyToken(String token) throws Exception{
        DecodedJWT jwt;
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        jwt = verifier.verify(token);
        return jwt.getClaims();
    }
}
