package com.example.demo.security;


import io.jsonwebtoken.Claims;
        import io.jsonwebtoken.Jwts;
        import io.jsonwebtoken.SignatureAlgorithm;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.stereotype.Service;

        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtTokenUtil {

    private String SECRET_KEY = "secret";

    private static final long serialVersionUID = 1L;

//    /* the secret key to hash the jwt token */
//    @Value("${auth.secretKey}")
//    private String secretKey;
//
//    /* the validity duration of the token */
//    @Value("${auth.token.expiration}")
//    public long JWT_TOKEN_VALIDITY;

    /* retrive username from jwt token */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
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
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /* check if the token has expired  */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /* generate token for user */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGeterateToken(claims, userDetails.getUsername());
    }

    /* create the token (define claims and sign the JWT using a hash algo) */
    public String doGeterateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+30*60*1000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();

    }

    /* check if the token is valid */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
