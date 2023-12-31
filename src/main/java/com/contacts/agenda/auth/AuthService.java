package com.contacts.agenda.auth;

import com.contacts.agenda.auth.entities.*;
import com.contacts.agenda.auth.jwt.JwtService;
import com.contacts.agenda.exceptions.customsExceptions.InvalidValueException;
import com.contacts.agenda.exceptions.customsExceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    public Role createRoleByEmail ( String email){
        Role role = null;
        switch (email){
            case "super@gmail.com":
                role = Role.SUPER_ADMIN;
                break;
            case "admin@gmail.com":
                role = Role.ADMIN;
                break;
            default:
                role = Role.USER;
        }
        return role;
    }
    public AuthResponse register(RegisterRequest registerRequest) {
        // validations, return value or trows exceptions
        // String req_username = validator.validateUsername(request.getUsername());
        // String req_password = validator.validatePassword(request.getPassword());
        // String req_firstName = validator.validateFirstName(request.getFirstName());
        // String req_lastName = validator.validateLastName(request.getLastName());
        // String req_phone = validator.validatePhone(request.getPhone());
        // String req_dni = validator.validateDni(request.getDni());
        // String req_email = validator.validateEmail(request.getEmail());
        // validator.alreadyExistUser(req_username, req_dni , req_email);

        if (! registerRequest.getPassword1().equals(registerRequest.getPassword2())) {
            throw new InvalidValueException("Passwords no coinciden!");
        }


        User user = new User().builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword1()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                .dni(registerRequest.getDni())
                .email(registerRequest.getEmail())
                .role(createRoleByEmail(registerRequest.getEmail()))
                .build();
        userRepository.save(user);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registerRequest.getUsername() , registerRequest.getPassword1()));

        return AuthResponse.builder().token(jwtService.getToken(user)).build();

    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername() , loginRequest.getPassword()));
        UserDetails userDetails = userRepository
                .findByUsername(loginRequest.getUsername())
                .orElseThrow(()->new NotFoundException(("User not found")));
        String token = jwtService.getToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public LoguedUserDetails getLoguedUserDetails(HttpHeaders headers) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String token = jwtService.getTokenFromHeader(headers);
        User loguedUser = (User) securityContext.getAuthentication().getPrincipal();

        return LoguedUserDetails
                .builder()
                .token(token)
                .id(loguedUser.getId())
                .username(loguedUser.getUsername())
                .firstName(loguedUser.getFirstName())
                .lastName(loguedUser.getLastName())
                .phone(loguedUser.getPhone())
                .dni(loguedUser.getDni())
                .email((loguedUser.getEmail()))
                .role(loguedUser.getRole())
                .authorities(loguedUser.getAuthorities())
                .build();


    }
}
