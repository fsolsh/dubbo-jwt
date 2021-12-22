package com.fsolsh.mining.service;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class AuthDemoService {

    @DubboReference
    private AuthService authService;

    public String createJWT(String id, String additionalInfo) {
        return authService.createJWT(id, additionalInfo);
    }

    public String isValidJWT(String token) {
        return JSON.toJSONString(authService.isValidJWT(token));
    }

    public String verifyJWT(String token) {
        return JSON.toJSONString(authService.verifyJWT(token));
    }

    public boolean invalidateJWT(String id) {
        return authService.invalidateJWT(id);
    }
}
