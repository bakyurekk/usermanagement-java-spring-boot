package com.usermanagment.service.impl;

import com.usermanagment.domain.User;
import com.usermanagment.domain.UserPrincipal;
import com.usermanagment.exception.domain.EmailExistException;
import com.usermanagment.exception.domain.UserNotFoundExcepiton;
import com.usermanagment.exception.domain.UsernameExistException;
import com.usermanagment.repository.UserRepository;
import com.usermanagment.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.usermanagment.constant.UserImplConstant.*;
import static com.usermanagment.enumeration.Role.*;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null){
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }else{
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws EmailExistException, UserNotFoundExcepiton, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        userRepository.save(user);
        LOGGER.info("New user password: " + password);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistException, EmailExistException, UserNotFoundExcepiton {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null){
                throw new UserNotFoundExcepiton(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }

            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())){
                throw  new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }


            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())){
                throw  new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return  currentUser;
        }else{
            if (userByNewUsername != null){
                throw  new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null){
                throw  new EmailExistException(EMAIL_ALREADY_EXISTS);
            }

            return null;
        }
    }
}
