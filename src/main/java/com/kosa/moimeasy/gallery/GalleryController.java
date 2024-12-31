package com.kosa.moimeasy.gallery;

import com.kosa.moimeasy.gallery.dto.GalleryResponseDto;
import com.kosa.moimeasy.gallery.dto.GalleryUploadRequest;
import com.kosa.moimeasy.gallery.dto.UploadFileInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name="모임 갤러리 페이지" , description="Gallery API")
@RestController
@RequestMapping("/api/v1/gallery")
@RequiredArgsConstructor
public class GalleryController {
    private final GalleryService galleryService;


    @Operation(summary = "갤러리 조회" ,description ="모임 갤러리 조회하기" )
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

    @Operation(summary = "갤러리 사진 업로드" ,description ="모임 사진 추가하기" )
    @PostMapping
    public List<Gallery> uploadImagesByUrl(@RequestBody GalleryUploadRequest request) {
        Long moeimId = request.getMoeimId();
        List<UploadFileInfo> files = request.getFiles();
        return galleryService.saveImageUrls(moeimId, files);
    }
}
