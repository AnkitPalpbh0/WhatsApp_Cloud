package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.AuthResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.LoginRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.UserRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Security.config.CustomUserService;
import com.WhatsAppBusiness.WhatsApp.Business.Security.config.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserService customUserService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody Users user) throws UserException {
        String email = user.getEmail();
        String name = user.getName();
        String password = user.getPassword();
        String phone = user.getPhone();

        Users isUser = this.userRepository.findByEmail(email);
        if (isUser != null) {
            throw new UserException("Email is userd with another account");
        }
        Users createdUser = new Users();
        createdUser.setEmail(email);
        createdUser.setName(name);
        createdUser.setPassword(this.passwordEncoder.encode(password));
        createdUser.setPhone(phone);

        userRepository.save(createdUser);

        Authentication authentication = this.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        String jwt = this.tokenProvider.generateToken(authentication);

        AuthResponse response = new AuthResponse(jwt, true);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        Authentication authentication = this.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = this.tokenProvider.generateToken(authentication);

        AuthResponse response = new AuthResponse(jwt, true);

        return new ResponseEntity<AuthResponse>(response, HttpStatus.ACCEPTED);

    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password or username");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
