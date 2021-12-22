package com.fsolsh.mining.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String additionalInfo;
    private Long timestamp;

}
