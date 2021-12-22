package com.fsolsh.mining.bean;

import com.fsolsh.mining.interceptor.TokenInterceptor;
import com.fsolsh.mining.service.AuthService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @DubboReference
    private AuthService authService;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "HEAD", "POST", "DELETE", "PUT", "OPTIONS")
                .maxAge(3600);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        List<String> list = new ArrayList<>();
        list.add("*");
        corsConfiguration.setAllowedOrigins(list);

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        TokenInterceptor interceptor = new TokenInterceptor(authService);
        InterceptorRegistration oneInterceptor = registry.addInterceptor(interceptor);
        oneInterceptor.addPathPatterns("/**");
        oneInterceptor.excludePathPatterns("/webjars/**", "/swagger-ui.html", "/swagger-resources/**", "/swagger/**", "/v2/api-docs/**", "/configuration/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-resources/**").addResourceLocations("classpath:/META-INF/resources/swagger-resources/");
        registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/META-INF/resources/swagger*");
        registry.addResourceHandler("/v2/api-docs/**").addResourceLocations("classpath:/META-INF/resources/v2/api-docs/");
        registry.addResourceHandler("/configuration/**").addResourceLocations("classpath:/META-INF/resources/swagger-resources/configuration/");
    }
}
