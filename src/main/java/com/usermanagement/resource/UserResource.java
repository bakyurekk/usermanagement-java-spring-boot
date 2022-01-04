package com.usermanagement.resource;


import com.usermanagement.domain.HttpResponse;
import com.usermanagement.domain.User;
import com.usermanagement.domain.UserPrincipal;
import com.usermanagement.exception.ExceptionHandling;
import com.usermanagement.exception.domain.EmailExistException;
import com.usermanagement.exception.domain.EmailNotFoundException;
import com.usermanagement.exception.domain.UserNotFoundExcepiton;
import com.usermanagement.exception.domain.UsernameExistException;
import com.usermanagement.service.UserService;
import com.usermanagement.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.websocket.server.PathParam;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.usermanagement.constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {

    public static final String EMAIL_SENT = "An email with new password was sent to:";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
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

    @PostMapping("/update")
    public ResponseEntity<User> addNewUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstname") String firstname,
                                           @RequestParam("lastname") String lastname,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNoneLocked") String isNoneLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws EmailExistException, UserNotFoundExcepiton, IOException, UsernameExistException {

        User updatedUser = userService.updateUser(currentUsername, firstname, lastname, username, email, role, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNoneLocked), profileImage);

        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/getUser/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username){
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }
    @DeleteMapping("/deleted/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id){
        userService.deleteUser(id);
        return response(NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }


    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws EmailExistException, UserNotFoundExcepiton, IOException, UsernameExistException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, OK);
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<HttpResponse>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
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
