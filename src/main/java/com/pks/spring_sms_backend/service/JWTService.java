package com.pks.spring_sms_backend.service;


import com.pks.spring_sms_backend.model.LoginModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${spring.security-6.key}")
    private  String secretKey ;
    public String generateToken(LoginModel loginModel) {
        Map<String ,Object> claims = new HashMap<>();
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(loginModel.getEmail())
                .issuer("PGS")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+10*60*1000))
                .and()
                .signWith(generateKey())
                .compact();
    }

    public String getSecretKey(){
        return secretKey ;
    }

    public SecretKey generateKey(){
        final byte[] decode =
                Decoders.BASE64.decode(getSecretKey());
        return Keys.hmacShaKeyFor(decode);
    }

    public String extractUserName(String jwtToken) {

        return extractClaims(jwtToken,Claims::getSubject);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
      String username = extractUserName(jwtToken);
      return (username.equals(userDetails.getUsername())
      && !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken,Claims::getExpiration);
    }

    private <T>T extractClaims(String jwtToken, Function<Claims,T> claimsResolver){
   Claims claims = extractClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String jwtToken) {
      return  Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }
}
