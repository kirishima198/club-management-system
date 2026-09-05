package com.example.club.util;

import com.example.club.context.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${club.jwt.secret}")
    private String secret;

    @Value("${club.jwt.expire}")
    private long expire;

    public String createToken(LoginUser user) {
        return Jwts.builder()
                .claim("uid", user.getId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public LoginUser parse(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        LoginUser user = new LoginUser();
        user.setId(Long.parseLong(claims.get("uid").toString()));
        user.setUsername((String) claims.get("username"));
        user.setRole((String) claims.get("role"));
        return user;
    }
}