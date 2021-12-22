package com.fsolsh.mining.controller;

import com.alibaba.fastjson.JSON;
import com.fsolsh.mining.annotation.NoTokenRequired;
import com.fsolsh.mining.common.Result;
import com.fsolsh.mining.constant.JWTAuth;
import com.fsolsh.mining.service.AuthDemoService;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/demo")
public class AuthDemoController {

    @Autowired
    private AuthDemoService authDemoService;

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public Result auth() {
        return Result.OK("have no permission");
    }

    @NoTokenRequired
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Result login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //if username and password are verified successfully
        String token = authDemoService.createJWT("123456", "additionalInfo");
        if (StringUtils.isNotEmpty(token)) {
            response.addHeader(JWTAuth.AUTHORIZATION, token);
            return Result.OK(token);
        }
        return Result.error("create jwt token error");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@RequestAttribute(required = false) Long id) {
        if (authDemoService.invalidateJWT(String.valueOf(id))) {
            return JSON.toJSONString(Result.OK());
        }
        return JSON.toJSONString(Result.error());
    }

    @RequestMapping(value = "/isValidToken", method = RequestMethod.GET)
    public String isValidJWT(HttpServletRequest request) {
        return JSON.toJSONString(Result.OK(authDemoService.isValidJWT(request.getHeader(JWTAuth.AUTHORIZATION))));
    }

    @RequestMapping(value = "/verifyToken", method = RequestMethod.GET)
    public String verifyJWT(HttpServletRequest request) {
        return JSON.toJSONString(Result.OK(authDemoService.verifyJWT(request.getHeader(JWTAuth.AUTHORIZATION))));
    }
}
