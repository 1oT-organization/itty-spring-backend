package org.iot.itty.login.jwt;

import org.iot.itty.login.service.LoginService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/* 받아온 토큰을 분해 */
@Slf4j
@Component
public class JwtUtil {

	private final Key key;
	private final LoginService loginService;
	private final long accessTokenExpTime = 15 * 60 * 1000L;
	private final long RefreshTokenExpTime = 2 * 24 * 60 * 1000L;

	public JwtUtil(
		@Value("${token.secret}") String secretKey,
		LoginService loginService) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.loginService = loginService;
	}

	/* AccessToken에서 인증 객체(Authentication) 추출 */
	public Authentication getAuthentication(String token) {
		/* User 객체에서 토큰에 담긴 userEmail과 일치하는 회원의 userEmail을 가져와 저장 */
		UserDetails userDetails = loginService.loadUserByUsername(this.getUserEmail(token));

		Claims claims = parseClaims(token);
		log.info("AcessToken에 담긴 Claims 확인: " + claims);

		if (claims.get("auth") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString()
					.replace("[", "").replace("]", "").split(", "))
				.map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList());

		return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
	}

	/* 구문 분석이 완료된 토큰에서 userEmail 추출 */
	private String getUserEmail(String token) {
		return parseClaims(token).getSubject();
	}

	/* 토큰에서 Claims 추출 */
	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	/* 토큰 유효성 검증 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token {}", e);
		} catch (ExpiredJwtException e) {
			log.info("expired JWT Toekn {}", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token {}", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims strig si empty {}", e);
		}

		return false;
	}
}
