package com.nisum.userws.utils.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class SignUpRequest {
    @NotNull
    @NotBlank
    private String  name;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@”]+(\\.[^<>()\\[\\]\\\\.,;:\\s@”]+)*)|(“.+”))@((\\[[0–9]{1,3}\\.[0–9]{1,3}\\.[0–9]{1,3}\\.[0–9]{1,3}])|(([a-zA-Z\\-0–9]+\\.)+[a-zA-Z]{2,}))$" , message = "El correo deber tener el formato aaaaaaa@dominio.cl")
    private String  email;

    @NotNull
    @NotBlank
    private String  password;

    private List<PhoneRequest> phones;

}
