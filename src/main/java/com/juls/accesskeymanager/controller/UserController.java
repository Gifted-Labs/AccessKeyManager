package com.juls.accesskeymanager.controller;

import com.juls.accesskeymanager.data.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private static final List<User> USERS = Arrays.asList(
            new User(1L,"James Bond"),
            new User(2L, "Anthony Abban"),
            new User(3L, "Richies Infinity")
    );

    @GetMapping(path = "/{userId}")
    public User getUser(@PathVariable("userId") Long userId){
        return USERS.stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User with "+userId+" not found"));
    }
}
