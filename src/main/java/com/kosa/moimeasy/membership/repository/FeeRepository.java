package com.kosa.moimeasy.membership.repository;

import com.kosa.moimeasy.membership.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
}
