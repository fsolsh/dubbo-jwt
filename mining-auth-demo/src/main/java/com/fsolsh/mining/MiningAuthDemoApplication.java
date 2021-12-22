package com.fsolsh.mining;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class MiningAuthDemoApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(MiningAuthDemoApplication.class, args);
    }

}
