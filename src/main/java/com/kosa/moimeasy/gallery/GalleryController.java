package com.kosa.moimeasy.gallery;

import com.kosa.moimeasy.gallery.dto.GalleryUploadRequest;
import com.kosa.moimeasy.gallery.dto.UploadFileInfo;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gallery")
@RequiredArgsConstructor
public class GalleryController {
    private final GalleryService galleryService;

    // 갤러리 목록 조회
//    @GetMapping
//    public Page<Gallery> getGalleries(Long moeimId,Pageable pageable) {
//        return galleryService.getGalleryList(moeimId, pageable);
//    }

    @GetMapping
    public ResponseEntity<Page<GalleryResponseDto>> getGalleryList(
            @RequestParam Long moeimId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Gallery> galleryPage = galleryService.getGalleryList(moeimId, pageable);

        Page<GalleryResponseDto> response = galleryPage.map(gallery ->
                GalleryResponseDto.builder()
                        .id(gallery.getId())
                        .imageUrl(gallery.getImageUrl())
                        .build()
        );

        return ResponseEntity.ok(response);
    }

    // 갤러리 이미지 저장
    @PostMapping
    public List<Gallery> uploadImagesByUrl(@RequestBody GalleryUploadRequest request) {
        Long moeimId = request.getMoeimId();
        List<UploadFileInfo> files = request.getFiles();
        return galleryService.saveImageUrls(moeimId, files);
    }
}
@Data
@Builder
class GalleryResponseDto {
    private Long id;
    private String imageUrl;
}