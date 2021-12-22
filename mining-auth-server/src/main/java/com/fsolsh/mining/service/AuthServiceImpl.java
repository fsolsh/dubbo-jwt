package com.fsolsh.mining.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fsolsh.mining.constant.JWTAuth;
import com.fsolsh.mining.dto.AuthDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@DubboService
public class AuthServiceImpl implements AuthService {

    private static final String SLAT = "@#$%^&*xm*&^%$#@";
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4);

    @Value("${jwt.secret.key}")
    private String tokenSecretKey;

    @Value("${jwt.expire.time}")
    private long tokenExpireTime;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * createJWT
     * 颁发token，token携带id和指定的额外信息
     *
     * @param id
     * @param additionalInfo
     * @return
     */
    @Override
    public String createJWT(String id, String additionalInfo) {
        log.info("execute createJWT : {}", id);
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        long now = System.currentTimeMillis();
        String token = buildJWT(id, now);
        return saveJWTToken(id, token, additionalInfo) ? token : null;
    }

    /**
     * isValidJWT
     * 验证token，基本验证通过并且Redis中存在即为有效，当前方法不刷新token有效期
     *
     * @param jwtToken
     * @return
     */
    @Override
    public boolean isValidJWT(String jwtToken) {
        log.info("execute isValidJWT : {}", jwtToken.substring(0, 10));
        return getTokenInfo(jwtToken) != null;
    }

    /**
     * verifyJWT
     * 验证token，token有效时返回token携带的附加信息，当前方法刷新token有效期
     *
     * @param jwtToken
     * @return
     */
    @Override
    public AuthDTO verifyJWT(String jwtToken) {
        log.info("execute verifyJWT : {}", jwtToken.substring(0, 10));
        AuthDTO authDTO = getTokenInfo(jwtToken);
        if (authDTO != null) {
            THREAD_POOL.submit(() ->
            {
                stringRedisTemplate.expire(buildJWTRedisKey(authDTO.getId()), tokenExpireTime, TimeUnit.SECONDS);
            });
        }
        return authDTO;
    }

    @Override
    public boolean invalidateJWT(String id) {
        return stringRedisTemplate.delete(buildJWTRedisKey(id));
    }

    /**
     * buildJWT
     * 构建JWTToken对象
     *
     * @param id
     * @param timestamp
     * @return
     */
    private String buildJWT(String id, long timestamp) {
        Algorithm algo = Algorithm.HMAC256(tokenSecretKey);
        return JWT.create()
                .withIssuer(JWTAuth.ISSUER)
                .withClaim(JWTAuth.ID, id)
                .withClaim(JWTAuth.TIMESTAMP, timestamp)
                .withClaim(JWTAuth.PERMIT, buildJWTMD5Permit(id, timestamp))
                .sign(algo);
    }

    /**
     * getValidId
     * 进阶redis验证，比对Redis
     *
     * @param jwtToken
     * @return
     */
    private AuthDTO getTokenInfo(String jwtToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenSecretKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(JWTAuth.ISSUER).build();
            DecodedJWT jwt = verifier.verify(jwtToken);
            String id = jwt.getClaim(JWTAuth.ID).asString();
            String permit = jwt.getClaim(JWTAuth.PERMIT).asString();
            Long timestamp = jwt.getClaim(JWTAuth.TIMESTAMP).asLong();
            String redisTokenKey = buildJWTRedisKey(id);
            if (StringUtils.isEmpty(permit) || !permit.equals(buildJWTMD5Permit(id, timestamp)) || Boolean.FALSE.equals(stringRedisTemplate.hasKey(redisTokenKey))) {
                return null;
            }
            //验签通过并且redis中存在，是有效的token，组装认证信息
            if (jwtToken.equals(stringRedisTemplate.boundHashOps(redisTokenKey).get(JWTAuth.TOKEN))) {
                AuthDTO authDTO = new AuthDTO();
                authDTO.setId(id);
                authDTO.setTimestamp(timestamp);
                authDTO.setAdditionalInfo(Objects.requireNonNull(stringRedisTemplate.boundHashOps(redisTokenKey).get(JWTAuth.ADDITIONAL_INFO)).toString());
                return authDTO;
            }

        } catch (Exception e) {
            log.error("getTokenInfo error : {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * buildJWTRedisKey
     * 构建token在Redis中的key
     *
     * @param userId
     * @return
     */
    private String buildJWTRedisKey(String userId) {
        return JWTAuth.JWT_TOKEN_REDIS_KEY_PREFIX + userId;
    }

    /**
     * buildJWTMD5Permit
     * 对数据加盐加密
     *
     * @param id
     * @param timestamp
     * @return
     */
    private String buildJWTMD5Permit(String id, long timestamp) {
        return DigestUtils.md5DigestAsHex(id.concat(String.valueOf(timestamp)).concat(SLAT).getBytes());
    }

    /**
     * saveJWTToken
     * 保存token到Redis
     *
     * @param token
     * @param additionalInfo
     * @return
     */
    private boolean saveJWTToken(String id, String token, String additionalInfo) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put(JWTAuth.TOKEN, token);
            map.put(JWTAuth.ADDITIONAL_INFO, additionalInfo);
            String redisTokenKey = buildJWTRedisKey(id);
            stringRedisTemplate.boundHashOps(redisTokenKey).putAll(map);
            stringRedisTemplate.expire(redisTokenKey, tokenExpireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("saveJWTToken error : {}", e.getMessage(), e);
        }
        return false;
    }
}
