package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTUtility implements Serializable {


    private static final long serialVersionUID = 1L;

    /* the secret key to hash the jwt token */
    @Value("${auth.secretKey}")
    private String secretKey;

    /* the validity duration of the token */
    @Value("${auth.token.expiration}")
    public long JWT_TOKEN_VALIDITY;
    @Value("${auth.token.refresh.expiratation}")
    public long JWT_REFRESH_VALIDITY;
    /* retrive username from jwt token */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUsername(String token){
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT.getSubject();
    }
    /* retrieve expiration date from the jwt token */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /* retrieve the claims from token */
    public <T> T getClaimFromToken(String token, Function<Claims, T> calimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return calimsResolver.apply(claims);
    }

    /* to retrieve informations from token we need the secret key */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /* check if the token has expired  */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /* generate token for user */
    public Map<String, String> generateTokens(UserDetails userDetails, String requestUrl) {
        List<String> claims = userDetails.getAuthorities().
                stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return doGeterateTokens(claims, userDetails.getUsername(), requestUrl);
    }
    /*seprate the token from the bearer */
    public String getToken(String authorizationHeader){
        return authorizationHeader.substring("Bearer ".length());
    }
    public UsernamePasswordAuthenticationToken setUserInfo(String token){
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        java.util.Arrays.stream(roles).forEach(role ->{
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return  new UsernamePasswordAuthenticationToken(username,null, authorities);
    }
    /* create the token (define claims and sign the JWT using a hash algo) */
    public Map<String, String> doGeterateTokens(List<String> claims, String subject, String requestUrl) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        String access_token = String.valueOf(JWT.create().
                withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .withIssuer(requestUrl).
                withClaim("roles", claims)
                        .sign(algorithm));
        String refresh_token = String.valueOf(JWT.create().
                withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_REFRESH_VALIDITY))
                .withIssuer(requestUrl)
                .sign(algorithm));
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        return  tokens;


    }

    /* check if the token is valid */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}