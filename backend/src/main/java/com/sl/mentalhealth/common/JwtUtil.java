package com.sl.mentalhealth.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

  private static final String SECRET = "mentalhealth_jwt_secret_key_2026_change_me_please_123456";
  private static final long EXPIRE_MILLIS = 24 * 60 * 60 * 1000L;

  private static final SecretKey KEY =
      Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

  private JwtUtil() {
  }

  public static String generateToken(String username, String role) {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + EXPIRE_MILLIS);
    String jti = UUID.randomUUID().toString();

    return Jwts.builder()
        .setId(jti)
        .claim("username", username)
        .claim("role", role)
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expireDate)
        .signWith(KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  public static Claims parseToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(KEY)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public static String getUsername(String token) {
    Object value = parseToken(token).get("username");
    return value == null ? null : value.toString();
  }

  public static String getRole(String token) {
    Object value = parseToken(token).get("role");
    return value == null ? null : value.toString();
  }

  public static String getJti(String token) {
    return parseToken(token).getId();
  }

  public static boolean isExpired(String token) {
    Date expiration = parseToken(token).getExpiration();
    return expiration.before(new Date());
  }

  public static boolean validateToken(String token) {
    try {
      Claims claims = parseToken(token);
      return claims.getSubject() != null && !isExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  public static long getExpireMillis() {
    return EXPIRE_MILLIS;
  }
}