package com.kosa.moimeasy.gallery;

import com.kosa.moimeasy.gallery.dto.UploadFileInfo;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final MoeimRepository moeimRepository;

    // Gallery 목록 불러오기
//    public Page<Gallery> getGalleryList(Long moeimId,String page, String size) {
//            return galleryRepository.findByMoeim_MoeimId(moeimId);
//    }
    public Page<Gallery> getGalleryList(Long moeimId, Pageable pageable) {
        return galleryRepository.findByMoeim_MoeimId(moeimId, pageable);
    }

    // 이미지 저장
    public Gallery saveImageUrl(Long moeimId, String imageUrl)  {
        Moeim moeim = moeimRepository.findById(moeimId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + moeimId));

        Gallery gallery = Gallery.builder()
                    .moeim(moeim)
                    .imageUrl(imageUrl)
                    .build();

        return galleryRepository.save(gallery);
    }

    public List<Gallery> saveImageUrls(Long moeimId, List<UploadFileInfo> files) {
        Moeim moeim = moeimRepository.findById(moeimId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다. ID: " + moeimId));

        List<Gallery> resultList = new ArrayList<>();

        for (UploadFileInfo file : files) {
            // Gallery 엔티티 생성
            Gallery gallery = new Gallery();
            gallery.setMoeim(moeim);
            gallery.setImageUrl(file.getDownloadUrl());
            Gallery saved = galleryRepository.save(gallery);
            resultList.add(saved);
        }

        return resultList;
    }




}