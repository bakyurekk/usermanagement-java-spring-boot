package com.usermanagement.service;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.*;

@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUMBER_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String, Integer> loadingAttemptCache;

    public LoginAttemptService() {
        super();
        loadingAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loadingAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loadingAttemptCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loadingAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loadingAttemptCache.get(username) >= MAXIMUM_NUMBER_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
