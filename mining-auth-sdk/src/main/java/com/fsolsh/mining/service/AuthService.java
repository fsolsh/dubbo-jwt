package com.fsolsh.mining.service;


import com.fsolsh.mining.dto.AuthDTO;

public interface AuthService {

    /**
     * createJWT
     *
     * @param id
     * @param additionalInfo
     * @return
     */
    String createJWT(String id, String additionalInfo);

    /**
     * isValidJWT
     *
     * @param jwtToken
     * @return
     */
    boolean isValidJWT(String jwtToken);

    /**
     * verifyJWT
     *
     * @param jwtToken
     * @return
     */
    AuthDTO verifyJWT(String jwtToken);

    /**
     * invalidateJWT
     *
     * @param id
     * @return
     */
    boolean invalidateJWT(String id);
}
