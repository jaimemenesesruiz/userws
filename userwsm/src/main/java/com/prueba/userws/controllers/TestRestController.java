package com.prueba.userws.controllers;

import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/test")
@Api(tags = "Prueba")
public class TestRestController {

    @GetMapping("/all")
    public String allAccess(){
        return "Contenido Público";
    }

    @GetMapping("/user")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('SUPERVISOR') or hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 201, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 400, message = "Campos Inválidos"),
            @ApiResponse(code = 401, message = "Usuario no autorizado"),
            @ApiResponse(code = 403, message = "Recurso no disponible"),
            @ApiResponse(code = 404, message = "Recurso no encontrado")
    })
    public String userAccess() {
        return "Contenido de Usuario";
    }

    @GetMapping("/supp")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('SUPERVISOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 201, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 400, message = "Campos Inválidos"),
            @ApiResponse(code = 401, message = "Usuario no autorizado"),
            @ApiResponse(code = 403, message = "Recurso no disponible"),
            @ApiResponse(code = 404, message = "Recurso no encontrado")
    })
    public String supAccess() {
        return "Contenido de Supervisor";
    }

    @GetMapping("/mod")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('MODERATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 201, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 400, message = "Campos Inválidos"),
            @ApiResponse(code = 401, message = "Usuario no autorizado"),
            @ApiResponse(code = 403, message = "Recurso no disponible"),
            @ApiResponse(code = 404, message = "Recurso no encontrado")
    })
    public String moderatorAccess() {
        return "Contenido de Moderador.";
    }

    @GetMapping("/admin")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 201, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 400, message = "Campos Inválidos"),
            @ApiResponse(code = 401, message = "Usuario no autorizado"),
            @ApiResponse(code = 403, message = "Recurso no disponible"),
            @ApiResponse(code = 404, message = "Recurso no encontrado")
    })
    public String adminAccess() {
        return "Contenido de Administrador.";
    }
}
