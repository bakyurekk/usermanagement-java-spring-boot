package com.usermanagement.service;

import com.usermanagement.domain.User;
import com.usermanagement.exception.domain.EmailExistException;
import com.usermanagement.exception.domain.EmailNotFoundException;
import com.usermanagement.exception.domain.UserNotFoundExcepiton;
import com.usermanagement.exception.domain.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register (String firstName, String lastName, String username, String email) throws EmailExistException, UserNotFoundExcepiton, UsernameExistException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistException, UserNotFoundExcepiton, UsernameExistException, IOException;

    User updateUser(String currentUsername,String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistException, UserNotFoundExcepiton, UsernameExistException, IOException;

    void deleteUser(long id);

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UserNotFoundExcepiton, UsernameExistException, IOException;
}
