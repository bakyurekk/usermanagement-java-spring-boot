package com.usermanagment.resource;


import com.usermanagment.exception.ExceptionHandling;
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
