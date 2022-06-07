package com.nisum.userws.controllers;

import com.nisum.userws.models.ERole;
import com.nisum.userws.models.Phone;
import com.nisum.userws.models.Role;
import com.nisum.userws.models.User;
import com.nisum.userws.repositories.RoleRepository;
import com.nisum.userws.services.UserService;
import com.nisum.userws.services.impl.UserDetailsImpl;
import com.nisum.userws.utils.request.SignInRequest;
import com.nisum.userws.utils.request.SignUpRequest;
import com.nisum.userws.utils.response.MessageResponse;
import com.nisum.userws.utils.response.SignUpResponse;
import com.nisum.userws.utils.security.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final UserService userService;

    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthRestController(AuthenticationManager authenticationManager, UserService userService, PasswordEncoder encoder, RoleRepository roleRepository, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> signup(@Valid @RequestBody(required = false) SignUpRequest userDto, BindingResult result) {
        if (result.hasErrors()) return validator(result);
        if (userDto == null)
            return ResponseEntity.badRequest().body(new MessageResponse("El suario no puede ser vacio"));

        if (userService.existsByEmail(userDto.getEmail()))
            return ResponseEntity.badRequest().body(new MessageResponse("El correo ya registrado"));

        User newUser = new User();
        Date feActual = new Date();
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(encoder.encode(userDto.getPassword()));
        newUser.setIsActive(Boolean.TRUE);
        newUser.setLastLogin(feActual);
        newUser.setUsername(userDto.getEmail());

        Set<Role> roles = new HashSet<>();

        var userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElse(null);
        if (userRole == null) {
            var role = new Role();
            role.setName(ERole.ROLE_USER);
            userRole = roleRepository.save(role);

        }
        roles.add(userRole);

        newUser.setRoles(roles);

        newUser.setPhones(userDto.getPhones().stream().map(p -> {
                    Phone ph = new Phone();
                    ph.setNumber(p.getNumber());
                    ph.setCitycode(p.getCitycode());
                    ph.setCountrycode(p.getContrycode());
                    return ph;
                }
        ).collect(Collectors.toList()));

        userService.save(newUser);

        SignInRequest request=new SignInRequest ();
        request.setUsername(userDto.getEmail());
        request.setPassword(userDto.getPassword());
        return autenticate(request);
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Iniciar Sesión",
            notes = "Servicio que nos valida la información de usuario e inicia sesión")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario inicia sesión correctamente"),
            @ApiResponse(code = 201, message = "Usuario creado correctamente"),
            @ApiResponse(code = 400, message = "Campos Inválidos"),
            @ApiResponse(code = 401, message = "Usuario no autorizado"),
            @ApiResponse(code = 403, message = "Usuario no autorizado"),
            @ApiResponse(code = 404, message = "Recurso no encontrado")
    })

    public ResponseEntity<Object> signin(@Valid @RequestBody SignInRequest request, BindingResult result) {
        if (result.hasErrors()) return validator(result);
        return autenticate(request);

    }

    private ResponseEntity<Object> autenticate(SignInRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),
                        request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        var user = userService.findByEmail(userDetails.getEmail());
        SignUpResponse response = new SignUpResponse();
        response.setId(user.getId());
        response.setCreated("" + user.getCreated());
        response.setModified("" + user.getModified());
        response.setLastLogin("" + user.getLastLogin());
        response.setToken("" + user.getToken());
        response.setActive(user.getIsActive());
        response.setToken(jwtToken);
        user.setToken(jwtToken);
        user.setLastLogin(new Date());
        userService.save(user);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Object> validator(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        String KEY = "key";
        result.getFieldErrors().forEach(fieldError -> {
            String errorString = "";
            if (errors.get(KEY) != null)
                errorString = (String) errors.get(KEY) + ",";
            errors.put(KEY, errorString + "'El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage() + "'");
        });
        return ResponseEntity.badRequest().body(new MessageResponse(errors.get(KEY)));
    }
}
