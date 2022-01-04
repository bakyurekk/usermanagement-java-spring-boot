package com.usermanagement.resource;


import com.usermanagement.domain.User;
import com.usermanagement.domain.UserPrincipal;
import com.usermanagement.exception.ExceptionHandling;
import com.usermanagement.exception.domain.EmailExistException;
import com.usermanagement.exception.domain.UserNotFoundExcepiton;
import com.usermanagement.exception.domain.UsernameExistException;
import com.usermanagement.service.UserService;
import com.usermanagement.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.IOException;

import static com.usermanagement.constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundExcepiton, EmailExistException, UsernameExistException, MessagingException {
       User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
       return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstname") String firstname,
                                           @RequestParam("lastname") String lastname,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNoneLocked") String isNoneLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws EmailExistException, UserNotFoundExcepiton, IOException, UsernameExistException {

        User newUser = userService.addNewUser(firstname, lastname, username, email, role, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNoneLocked), profileImage);

        return new ResponseEntity<>(newUser, OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}
