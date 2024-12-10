package com.kosa.moimeasy.membership.service;

import com.kosa.moimeasy.membership.entity.Fee;
import com.kosa.moimeasy.membership.repository.FeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeService {

    // FeeRepository 생성자 주입
    private final FeeRepository feeRepository;

    public Fee saveFee(Fee fee) { return feeRepository.save(fee); }

    public List<Fee> getFeeList(){ return feeRepository.findAll();}
}
