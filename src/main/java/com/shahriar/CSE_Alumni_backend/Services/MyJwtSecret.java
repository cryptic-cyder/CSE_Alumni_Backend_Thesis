package com.shahriar.CSE_Alumni_backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyJwtSecret {

    private final JwtConfig jwtConfig;

    @Autowired
    public MyJwtSecret(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String returnSecret() {
       return jwtConfig.getSecret();
    }
}