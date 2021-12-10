package com.usermanagment.resource;


import com.usermanagment.domain.User;
import com.usermanagment.exception.ExceptionHandling;
import com.usermanagment.exception.domain.EmailExistException;
import com.usermanagment.exception.domain.UserNotFoundExcepiton;
import com.usermanagment.exception.domain.UsernameExistException;
import com.usermanagment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundExcepiton, EmailExistException, UsernameExistException {
       User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
       return new ResponseEntity<>(newUser, OK);
    }
}
