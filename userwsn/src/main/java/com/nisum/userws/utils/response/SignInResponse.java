package com.nisum.userws.utils.response;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class SignInResponse{
    private static final String TOKEN_TYPE = "Bearer";
    private String token;
}
