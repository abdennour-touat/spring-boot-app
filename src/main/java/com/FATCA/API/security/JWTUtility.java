package com.FATCA.API.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

/*
* This clas is the jwt utility class, we make custom functions to work on the jwt token like cheking validation and creating tokens
* */
@Component
public class JWTUtility implements Serializable {

    /* the secret key to hash the jwt token */
    @Value("${auth.secretKey}")
    private String secretKey;
    /* the validity duration of the token  and for the refersh token*/
    @Value("${auth.token.expiration}")
    public long JWT_TOKEN_VALIDITY;
    @Value("${auth.token.refresh.expiratation}")
    public long JWT_REFRESH_VALIDITY;

    /*seprate the token from the bearer */
    public String getToken(String authorizationHeader){
        return authorizationHeader.substring("Bearer ".length());
    }
    /* generate token for user */
    public Map<String, String> generateTokens(UserDetails userDetails, String requestUrl) {
        List<String> claims = userDetails.getAuthorities().
                stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return doGeterateTokens(claims, userDetails.getUsername(), requestUrl);
    }
    //get the decoded token
    public DecodedJWT getDecodedToken (String token){
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT;
    }
    /* check if the token has expired  */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    /* retrieve expiration date from the jwt token */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    /* retrieve the claims from token */
    public <T> T getClaimFromToken(String token, Function<Claims, T> calimsResolver){
        final Claims claims = getAllClaimsFromToken(token);
        return calimsResolver.apply(claims);
    }
    /* to retrieve informations from token we need the secret key */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /* retrive username from jwt token */
    public String getUsername(String token){
        return getDecodedToken(token).getSubject();
    }
    //generate a token
    public String newAccessToken(String subject, String requestUrl, List<String> claims   ){
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        return JWT.create().
                withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .withIssuer(requestUrl).withClaim("role", claims).sign(algorithm);
    }
    public String newRefreshToken(String subject, String requestUrl   ){
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        return JWT.create().
                withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .withIssuer(requestUrl).sign(algorithm);
    }
    /* create the token (define claims and sign the JWT using a hash algo) */
    public Map<String, String> doGeterateTokens(List<String> claims, String subject, String requestUrl) {
        String access_token =  newAccessToken(subject, requestUrl, claims);
        String refresh_token = newRefreshToken(subject, requestUrl );
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        return  tokens;
    }

    //this method get the necessary data from the token to attempt authentication
    public UsernamePasswordAuthenticationToken setUserInfo(String token){
        DecodedJWT decodedJWT = getDecodedToken(token);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        java.util.Arrays.stream(roles).forEach(role ->{
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return  new UsernamePasswordAuthenticationToken(username,null, authorities);
    }
}