package com.usermanagment.service.impl;

import com.usermanagment.domain.User;
import com.usermanagment.domain.UserPrincipal;
import com.usermanagment.exception.domain.EmailExistException;
import com.usermanagment.exception.domain.UserNotFoundExcepiton;
import com.usermanagment.exception.domain.UsernameExistException;
import com.usermanagment.repository.UserRepository;
import com.usermanagment.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {


    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null){
            LOGGER.error("User not found by username: " + username);
            throw new UsernameNotFoundException("User not found by username: " + username);
        }else{
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found user by username:" + username);
            return userPrincipal;
        }
    }

    @Override
    public UserService register(String firstName, String lastName, String username, String email) throws EmailExistException, UserNotFoundExcepiton, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        return null;
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistException, EmailExistException, UserNotFoundExcepiton {
        if (StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null){
                throw new UserNotFoundExcepiton("No user found by username " + currentUsername);
            }
            User userByUsername = findUserByUsername(newUsername);
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())){
                throw  new UsernameExistException("Username already exists");
            }

            User userByEmail = findUserByEmail(newEmail);
            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())){
                throw  new EmailExistException("Email already exists");
            }
            return  currentUser;
        }else{
            User userByUsername = findUserByUsername(newUsername);
            if (userByUsername != null){
                throw  new UsernameExistException("Username already exists");
            }

            User userByEmail = findUserByEmail(newEmail);
            if (userByEmail != null){
                throw  new EmailExistException("Email already exists");
            }

            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }
}
