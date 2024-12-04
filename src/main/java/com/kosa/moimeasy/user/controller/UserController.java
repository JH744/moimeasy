package com.kosa.moimeasy.user.controller;

import com.kosa.moimeasy.user.dto.UserDTO;
import com.kosa.moimeasy.user.service.UserService;
import com.kosa.moimeasy.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody UserDTO request) {
        return userService.createUser(request);
    }

    @PutMapping("/update/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody UserDTO request) {
        return userService.updateUser(userId, request);
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "사용자가 삭제되었습니다.";
    }
}
