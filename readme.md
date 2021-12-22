**####这只是一个Demo####**

**1、jwt token auth-权限管理项目模块**  
  --auth-client  -> 权限验证拦截实现SDK（定义权限验证的拦截机制）  
  --auth-demo    -> 项目demo（演示如何使用auth-server颁发token，如何使用auth-client进行接口访问权限控制）   
  --auth-sdk    -> 权限管理接口定义SDK（定义dubbo服务接口）  
  --auth-server   -> 权限管理服务项目（提供dubbo服务实现，负责颁发token和验证token）

**2、服务依赖关系：**  
auth-server依赖auth-sdk  
auth-client依赖auth-sdk  
auth-demo依赖auth-client  
auth-demo通过dubbo rpc调用auth-server

**3、服务部署方式：**  
1、使用了dubbo rpc机制，需要部署注册发现服务（nacos）  
2、jwt-token服务端存储采用的redis，需要部署redis服务  
3、配置并启动auth-server服务  

**4、服务接入方式：**  
4.1：使用场景一：对外提供的http服务接口需要token验证，方式如下：  
4.1.1：引入mining-auth-client依赖  
```
        <dependency>
            <groupId>com.fsolsh.mining</groupId>
            <artifactId>mining-auth-client</artifactId>
            <version>1.0.0</version>
        </dependency>
```
4.1.2：添加相关配置 
```
##dubbo注册中心配置
dubbo:
  registry:
    address: nacos://127.0.0.1:8848
    timeout: 10000
    
##集成mining-auth-client之后，默认拦截所有http请求，请求都需要进行Token验证
##绕开token验证的可选方式一：在Controller的方法上添加@NoTokenRequired注解
##绕开token验证的可选方式二：通过auth.without_verification_url配置无须Token验证的请求地址，多个地址可以用逗号隔开
auth:
  without_verification_url: /demo/userLogin,/demo/isValidToken,/demo/verifyToken
```
4.1.3：开启Dubbo服务
```
@EnableDubbo
@SpringBootApplication
public class MiningAuthDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiningAuthDemoApplication.class, args);
    }
}

/**Controller中不需要token校验的接口方法，添加@NoTokenRequired注解**/
```
4.2：使用场景一：需要使用AuthService提供的服务，方式如下：  
4.2.1：引入mining-auth-sdk依赖
```
        <dependency>
            <groupId>com.fsolsh.mining</groupId>
            <artifactId>mining-auth-sdk</artifactId>
            <version>0.0.1</version>
        </dependency>
```
4.2.2：开启Dubbo服务
```
@EnableDubbo
@SpringBootApplication
public class MiningAuthDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiningAuthDemoApplication.class, args);
    }
}
```
4.2.3：添加相关配置
```
##dubbo注册中心配置
dubbo:
  registry:
    address: nacos://127.0.0.1:8848
    timeout: 10000
```
4.2.4：注入AuthService并使用它
```
    @DubboReference
    private AuthService authService;
```
5：以上两种示例均可参照本auth-demo
