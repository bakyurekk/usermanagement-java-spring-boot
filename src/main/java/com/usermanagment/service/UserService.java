package com.usermanagment.service;

import com.usermanagment.domain.User;
import com.usermanagment.exception.domain.EmailExistException;
import com.usermanagment.exception.domain.UserNotFoundExcepiton;
import com.usermanagment.exception.domain.UsernameExistException;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User register (String firstName, String lastName, String username, String email) throws EmailExistException, UserNotFoundExcepiton, UsernameExistException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
