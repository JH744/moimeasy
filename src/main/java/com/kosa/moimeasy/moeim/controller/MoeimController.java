package com.kosa.moimeasy.moeim.controller;

import com.kosa.moimeasy.moeim.dto.MoeimDTO;
import com.kosa.moimeasy.moeim.service.MoeimService;
import com.kosa.moimeasy.moeim.entity.Moeim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/moeim")
public class MoeimController {

    @Autowired
    private MoeimService moeimService;

    @PostMapping("/create")
    public Moeim createMoeim(@RequestBody MoeimDTO request) {
        return moeimService.createMoeim(request);
    }
}
