package com.nisum.userws.utils.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SignUpResponse implements Serializable {
    private String id;

    private String created;

    private String modified;

    private String lastLogin;

    private String token;

    private boolean isActive;
}
