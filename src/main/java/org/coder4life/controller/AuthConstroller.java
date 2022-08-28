package org.coder4life.controller;

import org.coder4life.domain.LoginDto;
import org.coder4life.domain.RegistrationDto;
import org.coder4life.domain.User;
import org.coder4life.security.JwtTokenUtil;
import org.coder4life.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthConstroller {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authentication = authenticationManager.authenticate(token);

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User user = userService.getByUsername(userDetails.getUsername());

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return ResponseEntity.ok().body(tokens);
    }

    @PostMapping("refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String refreshToken = authorizationHeader.substring("Bearer ".length());
            if (jwtTokenUtil.validate(refreshToken)) {
                org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) userService.loadUserByUsername(jwtTokenUtil.getUserName(refreshToken));
                User user = userService.getByUsername(userDetails.getUsername());

                String accessToken = jwtTokenUtil.generateAccessToken(user);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);

                return ResponseEntity.ok().body(tokens);
            }
        }

        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("register")
    public User register(@RequestBody RegistrationDto request) {
        return userService.create(request);
    }

}
