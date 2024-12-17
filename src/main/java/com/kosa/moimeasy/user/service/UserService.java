package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.user.dto.UserDTO;
import com.kosa.moimeasy.user.entity.Role;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findByMoeimId(Long moeimId) {
        return userRepository.findByMoeimId(moeimId);
    }

    public User createUser(UserDTO request, Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setProfileImage(request.getProfileImage());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname());
        user.setMoeimId(request.getMoeimId());
        user.setRole(role);
        return userRepository.save(user);
    }

    public User updateUser(Long userId, UserDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Role은 변경하지 않음
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getMoeimId() != null) {
            user.setMoeimId(request.getMoeimId());
        }

        return userRepository.save(user);
    }



    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }


    public List<UserDTO> getUsersByMoeimId(Long moeimId) {
        List<User> users = userRepository.findByMoeimId(moeimId);
        return users.stream()
                .map(user -> UserDTO.builder()
                        .userId(user.getUserId())
                        .userName(user.getUserName()) // userName 추가
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .createAt(user.getCreateAt()) // createAt 추가
                        .roleName(user.getRole() != null ? user.getRole().getRoleName() : null) // roleName 추가
                        .moeimId(user.getMoeimId())
                        .build())
                .collect(Collectors.toList());
    }

}
