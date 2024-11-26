package com.example.demo.services;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AuthEntity;
import com.example.demo.utils.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class JwtService {

    @Value("${spring.jwt.secretKey_access_token}")
    String secretKey_access_token;

    @Value("${spring.jwt.secretKey_refresh_token}")
    String secretKey_refresh_token;

    public String generateToken(TokenType type, AuthEntity user, Integer exInteger) {
        String jwt = Jwts.builder()
                .setIssuer(user.getUsername())
                .setSubject(user.getEmail())
                .claim("user_id", user.getId())
                .claim("scope", user
                        .getRole())
                .setIssuedAt(new Date(System
                        .currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + exInteger))
                .signWith(
                        key(type),
                        SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    private Key key(TokenType type) {
        switch (type) {
            case ACCESS_TOKEN:
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey_access_token));
            case REFRESH_TOKEN:
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey_refresh_token));
            default:
                break;
        }
        return null;
    }

    public Claims ExtraClaims(String token, TokenType type) {
        return Jwts.parserBuilder().setSigningKey(key(type)).build().parseClaimsJws(token).getBody();
    }

    public String ExtraUsername(String token, TokenType type) {
        Claims claims = this.ExtraClaims(token, type);
        return claims.getIssuer();
    }

    public Boolean isValidToken(String token, TokenType type, UserDetails userDetails){
        System.out.println("user Detail:" +  userDetails.getUsername());

        Claims claims= this.ExtraClaims(token, type);

        Date getExpiration= claims.getExpiration();

        String getUser = claims.getIssuer();

        return getExpiration.before(new Date()) && getUser.equals(userDetails.getUsername());
    }
}
