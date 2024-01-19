package com.contacts.agenda.auth;

import com.contacts.agenda.auth.entities.*;
import com.contacts.agenda.auth.jwt.JwtService;
import com.contacts.agenda.exceptions.customsExceptions.AlreadyExistException;
import com.contacts.agenda.exceptions.customsExceptions.InvalidJwtException;
import com.contacts.agenda.exceptions.customsExceptions.InvalidValueException;
import com.contacts.agenda.exceptions.customsExceptions.NotFoundException;
import com.contacts.agenda.utils.MailManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MailManager mailManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        // TODO validations, return value or trows exceptions
        // TODO String req_username = validator.validateUsername(request.getUsername());
        // TODO String req_password = validator.validatePassword(request.getPassword());
        // TODO String req_firstName = validator.validateFirstName(request.getFirstName());
        // TODO String req_lastName = validator.validateLastName(request.getLastName());
        // TODO String req_phone = validator.validatePhone(request.getPhone());
        // TODO String req_dni = validator.validateDni(request.getDni());
        // TODO String req_email = validator.validateEmail(request.getEmail());
        // TODO validator.alreadyExistUser(req_username, req_dni , req_email);

        if (! registerRequest.getPassword1().equals(registerRequest.getPassword2())) {
            throw new InvalidValueException("Passwords no coinciden!");
        }


        validateNewUsername(registerRequest.getUsername());
        validateNewDni(registerRequest.getDni());
        validateNewEmail(registerRequest.getEmail());

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

        mailManager.sendEmail(user.getEmail(), "Test servidor backend java", "Hola, GRACIAS POR REGISTRARTE "+user.getUsername()+"!");
        log.info("NUEVO USUARIO => "+user.getUsername());

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


    // TODO CREAR METODO PARA RESTAURAR PASSWORD!!
    // TODO CREAR METODO PARA RESTAURAR PASSWORD!!
    // TODO CREAR METODO PARA RESTAURAR PASSWORD!!

    public String restorePassword(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new NotFoundException("Email no registrado");
        String tokenToRestore = jwtService.createTokenForRestorePassword(user.get().getUsername());
        mailManager.sendEmailToRestorePassword(email , tokenToRestore);
        System.out.println(">>>>>> token enviado restore = >> " +tokenToRestore+" <<");
        return "Se envio un email con mas instrucciones";
    }

    public AuthResponse setNewPassword(RestorePassRequest restorePassRequest){
        if(!restorePassRequest.getPassword1().equals(restorePassRequest.getPassword2())) throw new InvalidValueException("Passwords no coinciden");
        if (jwtService.isTokenExpired(restorePassRequest.getToken())) throw new InvalidJwtException("Token expirado, vuelve a solicitar envio del token");
        String username = jwtService.getUsernameFromToken(restorePassRequest.getToken());
        User user = userRepository.findByUsername(username).get();
        user.setPassword(passwordEncoder.encode(restorePassRequest.getPassword1()));
        userRepository.save(user);
        return login(new LoginRequest(username,restorePassRequest.getPassword1()));
    }

    public void validateNewUsername(String username){
        // TODO VALIDAR TIPOS DE DATOS INPUTS
        if(userRepository.existsByUsername(username)) throw new AlreadyExistException("Username ya en uso!");
    }
    public void validateNewEmail(String email){
        // TODO VALIDAR TIPOS DE DATOS INPUTS
        if(userRepository.existsByEmail(email)) throw new AlreadyExistException("Email ya en uso!");
    }
    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    public void validateNewDni(String dni){
        // TODO VALIDAR TIPOS DE DATOS INPUTS
        if(userRepository.existsByDni(dni)) throw new AlreadyExistException("Dni ya en uso!");
    }

    public Role createRoleByEmail ( String email){
        Role role = Role.USER;
        if (email.contains("super@")) role = Role.SUPER_ADMIN;
        if (email.contains("admin@")) role = Role.ADMIN;
        return role;
    }
}
