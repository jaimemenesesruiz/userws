package com.nisum.userws.utils.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class SignInRequest  {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
