package com.usermanagment.resource;


import com.usermanagment.domain.User;
import com.usermanagment.exception.domain.EmailExistException;
import com.usermanagment.exception.domain.EmailNotFoundException;
import com.usermanagment.exception.domain.ExceptionHandling;
import com.usermanagment.exception.domain.UserNotFoundExcepiton;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/user")
public class UserResource extends ExceptionHandling {

    @GetMapping("/home")
    public String showUser() throws UserNotFoundExcepiton {
        //return "aplication works";
        throw new UserNotFoundExcepiton("The user was not found");
    }
}
