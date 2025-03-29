package com.pks.spring_sms_backend.service;

import com.pks.spring_sms_backend.entity.Role;
import com.pks.spring_sms_backend.entity.User;
import com.pks.spring_sms_backend.model.LoginModel;
import com.pks.spring_sms_backend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTService {

    private final UserRepository userRepository;

    @Value("${spring.security-6.key}")
    private String secretKey;

    private SecretKey signingKey;

    public JWTService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Generates a JWT token for the authenticated user.
     */
    public String generateToken(LoginModel loginModel) {
        User user = userRepository.findByEmail(loginModel.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or user not found");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(claims)
                .subject(loginModel.getEmail())
                .issuer("PGS")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (100 * 60 * 1000))) // 100 mins expiration
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts username (email) from a given JWT token.
     */
    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Validates JWT token against the provided user details.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Extracts authentication details (username & roles) from JWT.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = extractClaims(token);
        String username = claims.getSubject();
        List<?> rawRoles = claims.get("roles", List.class);

        // Ensure type safety when mapping roles
        List<GrantedAuthority> authorities = rawRoles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    /**
     * Extracts expiration date from token.
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Extracts specific claims from JWT token.
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractClaims(token));
    }

    /**
     * Extracts all claims from a JWT token.
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Retrieves the signing key for JWT.
     */
    private SecretKey getSigningKey() {
        if (signingKey == null) {
            signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        }
        return signingKey;
    }
}
