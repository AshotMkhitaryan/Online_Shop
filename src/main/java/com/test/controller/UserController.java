package com.test.controller;

import com.test.dto.user.*;
import com.test.service.exceptions.AuthenticationFailException;
import com.test.service.exceptions.CustomException;
import com.test.model.User;
import com.test.repository.UserRepository;
import com.test.service.AuthenticationService;
import com.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {
    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    private final UserService userService;
    @Autowired
    public UserController(UserRepository userRepository, AuthenticationService authenticationService, UserService userService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }


    @GetMapping("/all")
    public List<User> findAllUser(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        return userRepository.findAll();
    }

    @PostMapping("/signUp")
    public SignInResponseDto SignUp(@RequestBody SignUpDto signUpDto) throws CustomException {
        return userService.signUp(signUpDto);
    }

    @PostMapping("/signIn")
    public SignInResponseDto SignIp(@RequestBody SignInDto signInDto) throws CustomException {
        return userService.signIn(signInDto);
    }


    @PostMapping("/createUser")
    public SignInResponseDto createUser(@RequestParam(required = false, name = "token") String token, @RequestBody UserCreateDto userCreateDto)
            throws CustomException, AuthenticationFailException {
        authenticationService.authenticate(token);
        return userService.createUser(token, userCreateDto);
    }
}
