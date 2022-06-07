package com.prueba.userws.controllers;

import com.prueba.userws.exception.TokenRefreshException;
import com.prueba.userws.models.ERole;
import com.prueba.userws.models.RefreshToken;
import com.prueba.userws.models.Role;
import com.prueba.userws.models.User;
import com.prueba.userws.repositories.RoleRepository;
import com.prueba.userws.repositories.UserRepository;
import com.prueba.userws.utils.security.JwtUtils;
import com.prueba.userws.services.RefreshTokenService;
import com.prueba.userws.services.UserDetailsImpl;
import com.prueba.userws.utils.response.SignInResponse;
import com.prueba.userws.utils.response.MessageResponse;
import com.prueba.userws.utils.response.TokenRefreshResponse;
import com.prueba.userws.utils.request.LogOutRequest;
import com.prueba.userws.utils.request.SignInRequest;
import com.prueba.userws.utils.request.SignUpRequest;
import com.prueba.userws.utils.request.TokenRefreshRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Api(tags = "Autentificación")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthRestController(AuthenticationManager authenticationManager, UserRepository userRepository,
                              RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                              RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    // Validator de campos
    public ResponseEntity<Object> validar(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(),
                "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @PostMapping("/signin")
    @ApiOperation(
            value = "Iniciar Sesión SSS",
            notes = "Servicio que nos valida la información de usuario e inicia sesión")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 201, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 400, message = "Campos Inválidos"),
            @ApiResponse(code = 401, message = "Usuario no autorizado"),
            @ApiResponse(code = 403, message = "Recurso no disponible"),
            @ApiResponse(code = 404, message = "Recurso no encontrado")
    })
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody SignInRequest loginRequest, BindingResult result) {

        if (result.hasErrors()) return this.validar(result);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new SignInResponse(
                jwtToken,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    @PostMapping("/signup")
    @ApiOperation(
            value = "Registrar Usuario",
            notes = "Servicio que crea los usuario para el aplicativo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario creado correctamente"),
            @ApiResponse(code = 201, message = "Usuario creado correctamente"),
            @ApiResponse(code = 400, message = "Campos Inválidos(Duplicados o incorrectos)"),
            @ApiResponse(code = 401, message = "Usuario no autorizado"),
            @ApiResponse(code = 403, message = "Recurso no disponible"),
            @ApiResponse(code = 404, message = "Recurso no encontrado")
    })
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult result){
        if (result.hasErrors()) return this.validar(result);
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Este nombre de usuario ya existe!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Este email ya se encuentra en uso!"));
        }

        var user = new User(
                signUpRequest.getName(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> srtRoles = signUpRequest.getRoles();

        Set<Role> roles = new HashSet<>();

        if (srtRoles == null || srtRoles.isEmpty()) {
            var userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElse(null);
            if(userRole==null){
                var role=new Role();
                role.setName(ERole.ROLE_USER);
                userRole=roleRepository.save(role);

            }
            roles.add(userRole);
        }else {
            srtRoles.forEach(rol -> {
                switch (rol) {
                    case "admin":
                        var adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(adminRole);
                        break;
                    case "sup":
                        var suppRole = roleRepository.findByName(ERole.ROLE_SUPERVISOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(suppRole);
                        break;
                    case "mod":
                        var modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(modRole);
                        break;
                    default:
                        var userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(userRole);
                        break;
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Usuario creado Correctamente"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token no esta en la Base de datos!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        refreshTokenService.deleteByUserId(logOutRequest.getUserId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

}
