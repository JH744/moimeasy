package com.kosa.moimeasy.user.controller;

import com.kosa.moimeasy.user.dto.UserDTO;
import com.kosa.moimeasy.user.entity.Role;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.service.RoleService;
import com.kosa.moimeasy.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kosa.moimeasy.common.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = {"http://localhost:3000"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsersByMoeimId(@RequestParam(required = false) Long moeimId) {
        if (moeimId == null) {
            throw new IllegalArgumentException("moeimId는 필수 파라미터입니다.");
        }

        List<User> users = userService.findByMoeimId(moeimId);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // 데이터가 없는 경우 204 반환
        }

        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).toList();
        return ResponseEntity.ok(userDTOs);
    }
    //로그인, 회원가입돼서 필요없음
    @PostMapping("/create")
    public User createUser(@RequestBody UserDTO request) {
        Role role = roleService.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));
        return userService.createUser(request, role);
    }
    //요청한 정보만 수정가능
    @PutMapping("/update/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody UserDTO request) {
        return userService.updateUser(userId, request);
    }


    //회원 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(userDTO);
    }

}
