package com.kosa.moimeasy.security.repository;

import com.kosa.moimeasy.security.entity.RefreshTokenRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenRedis, Long> {
    void deleteById(Long userId);
}
