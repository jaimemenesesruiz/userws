package com.nisum.userws.utils.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class PhoneRequest {
    @NotNull
    @NotBlank
    private String  number;

    @NotNull
    @NotBlank
    private String  citycode;

    @NotNull
    @NotBlank
    private String  contrycode;

}
