package ru.ivankrn.messagingtesttask.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ivankrn.messagingtesttask.dto.AuthenticationRequestDTO;
import ru.ivankrn.messagingtesttask.dto.AuthenticationResponseDTO;
import ru.ivankrn.messagingtesttask.dto.RegistrationRequestDTO;
import ru.ivankrn.messagingtesttask.security.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody @Valid
                                                                  RegistrationRequestDTO registrationRequest) {
        return ResponseEntity.ok().body(authenticationService.register(registrationRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody @Valid
                                                                      AuthenticationRequestDTO authRequest) {
        return ResponseEntity.ok().body(authenticationService.authenticate(authRequest));
    }

}
