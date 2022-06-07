package com.nisum.userws.controllers;

import com.nisum.userws.utils.response.MessageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MessageResponse> allAccess(){
        return ResponseEntity.ok( new MessageResponse("Contenido Público"));
    }

    @GetMapping("/user")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('SUPERVISOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse>  userAccess() {
        return ResponseEntity.ok( new MessageResponse( "Contenido de Usuario"));
    }

    @GetMapping("/supp")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<MessageResponse>  supAccess() {
        return ResponseEntity.ok( new MessageResponse( "Contenido de Supervisor"));
    }

    @GetMapping("/mod")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<MessageResponse>  moderatorAccess() {
        return ResponseEntity.ok( new MessageResponse( "Contenido de Moderador."));
    }

    @GetMapping("/admin")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse>  adminAccess() {
        return ResponseEntity.ok( new MessageResponse( "Contenido de Administrador."));
    }
}
