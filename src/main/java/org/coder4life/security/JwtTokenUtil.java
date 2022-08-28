package org.coder4life.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.coder4life.domain.User;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {

    private final String jwtSecret = "CctlD5JL16m8wLTgsFNhzqjQP";
    private final String jwtIssuer = "coder4.life";

    public String generateAccessToken(User user) {
        Algorithm algorithm = Algorithm.HMAC512(jwtSecret.getBytes());

        List<String> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(role.getName());
        });

        return JWT.create()
                .withSubject(String.format("%s,%s", user.getId(), user.getUsername()))
                .withIssuer(jwtIssuer)
                .withClaim("roles", authorities)
                .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .sign(algorithm);
    }

    public String generateRefreshToken(User user) {
        Algorithm algorithm = Algorithm.HMAC512(jwtSecret.getBytes());

        return JWT.create()
                .withSubject(String.format("%s,%s", user.getId(), user.getUsername()))
                .withIssuer(jwtIssuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .sign(algorithm);
    }

    public boolean validate(String token) {
        try {
            Long expiresAt = JWT.decode(token).getExpiresAt().getTime();
            Calendar cal = Calendar.getInstance();
            if (expiresAt >= cal.getTime().getTime()) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(String.format("JWT is invalid - {%s}", e.getMessage()));
        }

        return false;
    }

    public String getUserName(String token) {
        String subject = JWT.decode(token).getSubject();

        return subject.split(",")[1];
    }

}
