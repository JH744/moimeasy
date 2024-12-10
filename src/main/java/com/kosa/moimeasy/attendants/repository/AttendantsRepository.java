package com.kosa.moimeasy.attendants.repository;

import com.kosa.moimeasy.attendants.entity.Attendants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendantsRepository extends JpaRepository<Attendants, Long> {
}
