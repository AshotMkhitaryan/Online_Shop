package com.test.service;

import com.test.service.exceptions.AuthenticationFailException;
import com.test.model.AuthenticationToken;
import com.test.model.User;
import com.test.repository.TokenRepository;

import com.test.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final TokenRepository repository;

    @Autowired
    public AuthenticationService(TokenRepository repository) {
        this.repository = repository;
    }

    public void saveConfirmationToken(AuthenticationToken authenticationToken) {
        repository.save(authenticationToken);
    }

    public AuthenticationToken getToken(User user) {
        return repository.findTokenByUser(user);
    }

    public void authenticate(String token) throws AuthenticationFailException {
        if (!Helper.notNull(token)) {
            throw new AuthenticationFailException("AUTH_TOKEN_NOT_PRESENT");
        }
        if (!Helper.notNull(getUser(token))) {
            throw new AuthenticationFailException("AUTH_TOKEN_NOT_VALID");
        }
    }

    public User getUser(String token) {
        AuthenticationToken authenticationToken = repository.findTokenByToken(token);
        if (Helper.notNull(authenticationToken)) {
            if (Helper.notNull(authenticationToken.getUser())) {
                return authenticationToken.getUser();
            }
        }
        return null;
    }

}
