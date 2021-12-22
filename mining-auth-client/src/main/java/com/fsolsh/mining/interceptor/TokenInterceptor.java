package com.fsolsh.mining.interceptor;

import com.fsolsh.mining.annotation.NoTokenRequired;
import com.fsolsh.mining.constant.JWTAuth;
import com.fsolsh.mining.dto.AuthDTO;
import com.fsolsh.mining.service.AuthService;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TokenInterceptor
 */
public class TokenInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public TokenInterceptor(AuthService authService) {
        this.authService = authService;
    }

    /**
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //如果请求的是方法进行处理
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            if ("error".equals(method.getMethod().getName()) || method.getMethod().getDeclaringClass().isAnnotationPresent(NoTokenRequired.class) || method.hasMethodAnnotation(NoTokenRequired.class)) {
                return true;
            }

            String tokenWithBearer = request.getHeader(JWTAuth.AUTHORIZATION);
            if (StringUtils.isNotEmpty(tokenWithBearer) && tokenWithBearer.startsWith(JWTAuth.BEARER)) {
                AuthDTO authDTO = authService.verifyJWT(tokenWithBearer.substring(JWTAuth.BEARER.length()));
                if (authDTO != null) {
                    request.setAttribute(JWTAuth.AUTH_INFO, authDTO);
                    request.setAttribute(JWTAuth.ID, authDTO.getId());
                    return true;
                }
            }

            if (response.containsHeader(JWTAuth.AUTHORIZATION)) {
                response.setHeader(JWTAuth.AUTHORIZATION, null);
            }

            response.setStatus(401);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
    }

}
