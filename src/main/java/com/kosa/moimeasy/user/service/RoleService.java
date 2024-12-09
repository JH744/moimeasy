package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.user.entity.Role;
import com.kosa.moimeasy.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findById(Integer roleId) {
        return roleRepository.findById(roleId);
    }
}
