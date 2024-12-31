package com.kosa.moimeasy.gallery.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GalleryResponseDto {
    private Long id;
    private String imageUrl;
}
