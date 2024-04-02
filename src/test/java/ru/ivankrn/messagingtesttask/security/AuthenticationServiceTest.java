package ru.ivankrn.messagingtesttask.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ivankrn.messagingtesttask.controller.error.UserAlreadyExistsException;
import ru.ivankrn.messagingtesttask.database.model.Role;
import ru.ivankrn.messagingtesttask.database.model.User;
import ru.ivankrn.messagingtesttask.database.repository.UserRepository;
import ru.ivankrn.messagingtesttask.dto.AuthenticationRequestDTO;
import ru.ivankrn.messagingtesttask.dto.AuthenticationResponseDTO;
import ru.ivankrn.messagingtesttask.dto.RegistrationRequestDTO;
import ru.ivankrn.messagingtesttask.service.UserApprovalService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private UserRepository userRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private UserApprovalService userApprovalService;
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        this.userRepository = Mockito.mock();
        this.jwtService = Mockito.mock();
        this.authenticationManager = Mockito.mock();
        this.passwordEncoder = Mockito.mock();
        this.userApprovalService = Mockito.mock();
        this.authenticationService = new AuthenticationService(
                userRepository,
                jwtService,
                authenticationManager,
                passwordEncoder,
                userApprovalService);
    }

    @Test
    public void givenUserNotExists_WhenRegister() {
        String username = "test";
        String userEmail = "test@mail.com";
        RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO(
                username,
                "12345",
                userEmail,
                "Краснов Илья Сергеевич"
        );
        verify(userRepository, never()).save(any());
        verify(userApprovalService, never()).submitForApproval(any());

        authenticationService.register(registrationRequest);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals(username) && user.getEmail().equals(userEmail)
        ));
        verify(userApprovalService).submitForApproval(argThat(user ->
                user.getUsername().equals(username) && user.getEmail().equals(userEmail)
        ));
    }

    @Test
    public void givenUserAlreadyExists_WhenRegister_ThenThrowUserAlreadyExistsException() {
        String username = "test";
        String userEmail = "test@mail.com";
        RegistrationRequestDTO sameLoginRegistrationRequest = new RegistrationRequestDTO(
                username,
                "12345",
                "abc@mail.com",
                "Краснов Илья Сергеевич"
        );
        RegistrationRequestDTO sameEmailRegistrationRequest = new RegistrationRequestDTO(
                "test2",
                "12345",
                userEmail,
                "Краснов Илья Сергеевич"
        );
        when(userRepository.existsByUsername(username)).thenReturn(true);
        when(userRepository.existsByEmail(userEmail)).thenReturn(true);
        verify(userRepository, never()).save(any());
        verify(userApprovalService, never()).submitForApproval(any());

        Exception sameLoginException = assertThrows(UserAlreadyExistsException.class,
                () -> authenticationService.register(sameLoginRegistrationRequest));
        assertEquals("User with same login already exists", sameLoginException.getMessage());

        Exception sameEmailException = assertThrows(UserAlreadyExistsException.class,
                () -> authenticationService.register(sameEmailRegistrationRequest));
        assertEquals("User with same email already exists", sameEmailException.getMessage());

        verify(userRepository, never()).save(any());
        verify(userApprovalService, never()).submitForApproval(any());
    }

    @Test
    public void givenCorrectUserCredentials_whenAuthenticate_thenSuccessfullyAuthenticate() {
        String username = "test";
        String password = "12345";
        String userEmail = "test@mail.com";
        User user = new User(
                1L,
                username,
                "12345",
                userEmail,
                "Краснов Илья Сергеевич",
                Role.USER,
                false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO(username, password);

        verify(jwtService, never()).generateToken(any());

        AuthenticationResponseDTO authenticationResponse = authenticationService.authenticate(authenticationRequest);

        assertNotNull(authenticationResponse);
        verify(jwtService).generateToken(any());
    }

    @Test
    public void givenWrongUserCredentials_whenAuthenticate_thenThrowBadCredentialsException() {
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO("test", "12345");

        verify(jwtService, never()).generateToken(any());

        assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticate(authenticationRequest));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    public void givenUserAccountIsNotActivated_whenAuthenticate_thenThrowDisabledException() {
        when(authenticationManager.authenticate(any())).thenThrow(DisabledException.class);
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO("test", "12345");

        verify(jwtService, never()).generateToken(any());

        assertThrows(DisabledException.class,
                () -> authenticationService.authenticate(authenticationRequest));
        verify(jwtService, never()).generateToken(any());
    }

}