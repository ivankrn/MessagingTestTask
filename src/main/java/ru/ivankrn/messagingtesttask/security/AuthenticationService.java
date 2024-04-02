package ru.ivankrn.messagingtesttask.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ivankrn.messagingtesttask.controller.error.NotFoundException;
import ru.ivankrn.messagingtesttask.controller.error.UserAlreadyExistsException;
import ru.ivankrn.messagingtesttask.database.repository.UserRepository;
import ru.ivankrn.messagingtesttask.database.model.Role;
import ru.ivankrn.messagingtesttask.database.model.User;
import ru.ivankrn.messagingtesttask.dto.AuthenticationRequestDTO;
import ru.ivankrn.messagingtesttask.dto.AuthenticationResponseDTO;
import ru.ivankrn.messagingtesttask.dto.RegistrationRequestDTO;
import ru.ivankrn.messagingtesttask.service.UserApprovalService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserApprovalService userApprovalService;

    public AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequest) {
        validateRegistrationRequest(registrationRequest);
        User user = User
                .builder()
                .username(registrationRequest.login())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .email(registrationRequest.email())
                .fio(registrationRequest.fio())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        userApprovalService.submitForApproval(user);
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponseDTO(jwtToken);
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.login(),
                        authenticationRequest.password()
                )
        );
        UserDetails userDetails = userRepository.findByUsername(authenticationRequest.login())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        String jwtToken = jwtService.generateToken(userDetails);
        return new AuthenticationResponseDTO(jwtToken);
    }

    private void validateRegistrationRequest(RegistrationRequestDTO registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.login())) {
            throw new UserAlreadyExistsException("User with same login already exists");
        }
        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new UserAlreadyExistsException("User with same email already exists");
        }
    }

}
