package com.test.service;

import com.test.dto.user.SignInDto;
import com.test.dto.user.SignInResponseDto;
import com.test.dto.user.SignUpDto;
import com.test.dto.user.UserCreateDto;
import com.test.enums.Role;
import com.test.service.exceptions.AuthenticationFailException;
import com.test.service.exceptions.CustomException;
import com.test.model.AuthenticationToken;
import com.test.model.User;
import com.test.repository.UserRepository;
import com.test.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public SignInResponseDto signUp(SignUpDto signupDto) throws CustomException {
        if (Helper.notNull(userRepository.findByEmail(signupDto.getEmail()))) {
            throw new CustomException("User already exists");
        }
        String encryptedPassword = signupDto.getPassword();
        try {
            encryptedPassword = hashPassword(signupDto.getPassword());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
        }
        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), Role.user, encryptedPassword);
        User createdUser;
        try {
            createdUser = userRepository.save(user);
            final AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
            authenticationService.saveConfirmationToken(authenticationToken);

            return new SignInResponseDto("USER_CREATED");
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    public SignInResponseDto signIn(SignInDto signInDto) throws CustomException {
        User user = userRepository.findByEmail(signInDto.getEmail());
        if (!Helper.notNull(user)) {
            throw new AuthenticationFailException("user not present");
        }
        try {
            if (!user.getPassword().equals(hashPassword(signInDto.getPassword()))) {
                throw new AuthenticationFailException("WRONG_PASSWORD");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        AuthenticationToken token = authenticationService.getToken(user);

        if (!Helper.notNull(token)) {
            throw new CustomException("token not present");
        }
        return new SignInResponseDto(token.getToken());
    }

    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return myHash;
    }

    public SignInResponseDto createUser(String token, UserCreateDto userCreateDto) throws CustomException, AuthenticationFailException {
        User creatingUser = authenticationService.getUser(token);
        if (!canCrudUser(creatingUser.getRole())) {
            throw new AuthenticationFailException("USER_NOT_PERMITTED");
        }
        String encryptedPassword = userCreateDto.getPassword();
        try {
            encryptedPassword = hashPassword(userCreateDto.getPassword());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
        }
        User user = new User(userCreateDto.getFirstName(), userCreateDto.getLastName(), userCreateDto.getEmail(), userCreateDto.getRole(), userCreateDto.getPassword(), encryptedPassword);
        User createdUser;
        try {
            createdUser = userRepository.save(user);
            final AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
            authenticationService.saveConfirmationToken(authenticationToken);
            return new SignInResponseDto(("USER_CREATED"));
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    boolean canCrudUser(Role role) {
        if (role == Role.admin || role == Role.manager) {
            return true;
        }
        return false;
    }

/*    boolean canCrudUser(User userUpdating, Integer userIdBeingUpdated) {
        Role role = userUpdating.getRole();

        if (role == Role.admin || role == Role.manager) {
            return true;
        }
        if (role == Role.user && userUpdating.getId() == userIdBeingUpdated) {
            return true;
        }
        return false;
    }*/
}
