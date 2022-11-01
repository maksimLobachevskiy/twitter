package com.twitterdan.service.auth;

import com.twitterdan.domain.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
  private int ACCESS_LEAVE_MINUTES = 5;
  private int REFRESH_LEAVE_DAYS = 30;
  public final String USER_LOGIN_FIELD = "userTag";

  private final SecretKey jwtAccessSecret;
  private final SecretKey jwtRefreshSecret;

  public JwtProvider(
    @Value("${jwt.access.leave.minutes}") int accessLeave,
    @Value("${jwt.refresh.leave.days}") int refreshLeave,
    @Value("${jwt.secret.access}") String jwtAccessSecret,
    @Value("${jwt.secret.refresh}") String jwtRefreshSecret) {

    this.ACCESS_LEAVE_MINUTES = accessLeave;
    this.REFRESH_LEAVE_DAYS = refreshLeave;
    this.jwtAccessSecret = getSecretKey(jwtAccessSecret);
    this.jwtRefreshSecret = getSecretKey(jwtRefreshSecret);
  }


  private SecretKey getSecretKey(String secretKey) {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public String generateAccessToken(@NonNull User user) {
    final LocalDateTime now = LocalDateTime.now();
    final Instant accessExpirationInstant = now.plusMinutes(ACCESS_LEAVE_MINUTES).atZone(ZoneId.systemDefault()).toInstant();
    final Date accessExpiration = Date.from(accessExpirationInstant);
    return Jwts.builder()
      .setSubject(user.getUserTag())
      .setExpiration(accessExpiration)
      .signWith(jwtAccessSecret)
      .claim(USER_LOGIN_FIELD, user.getUserTag())
      .compact();
  }

  public String generateRefreshToken(@NonNull User user) {
    final LocalDateTime now = LocalDateTime.now();
    final Instant refreshExpirationInstant = now.plusDays(REFRESH_LEAVE_DAYS).atZone(ZoneId.systemDefault()).toInstant();
    final Date refreshExpiration = Date.from(refreshExpirationInstant);
    return Jwts.builder()
      .setSubject(user.getUserTag())
      .setExpiration(refreshExpiration)
      .signWith(jwtRefreshSecret)
      .compact();
  }

  public boolean validateAccessToken(@NonNull String accessToken) {
    return validateToken(accessToken, jwtAccessSecret);
  }

  public boolean validateRefreshToken(@NonNull String refreshToken) {
    return validateToken(refreshToken, jwtRefreshSecret);
  }

  private boolean validateToken(@NonNull String token, @NonNull Key secret) {
    try {
      Jwts.parserBuilder()
        .setSigningKey(secret)
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException expEx) {
      log.error("Token expired", expEx);
    } catch (UnsupportedJwtException unsEx) {
      log.error("Unsupported jwt", unsEx);
    } catch (MalformedJwtException mjEx) {
      log.error("Malformed jwt", mjEx);
    } catch (SignatureException sEx) {
      log.error("Invalid signature", sEx);
    } catch (Exception e) {
      log.error("invalid token", e);
    }
    return false;
  }

  public Claims getAccessClaims(@NonNull String token) {
    return getClaims(token, jwtAccessSecret);
  }

  public Claims getRefreshClaims(@NonNull String token) {
    return getClaims(token, jwtRefreshSecret);
  }

  private Claims getClaims(@NonNull String token, @NonNull Key secret) {
    return Jwts.parserBuilder()
      .setSigningKey(secret)
      .build()
      .parseClaimsJws(token)
      .getBody();
  }
}
