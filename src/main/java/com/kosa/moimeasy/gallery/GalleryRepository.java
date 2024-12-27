package com.kosa.moimeasy.gallery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery,Long> {
    Page<Gallery> findByMoeim_MoeimId(Long moeimId, Pageable pageable);
}
