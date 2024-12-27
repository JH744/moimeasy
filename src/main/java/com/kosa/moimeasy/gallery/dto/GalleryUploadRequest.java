package com.kosa.moimeasy.gallery.dto;

import java.util.List;

public class GalleryUploadRequest {
    private Long moeimId;
    private List<UploadFileInfo> files;

    public Long getMoeimId() {
        return moeimId;
    }
    public void setMoeimId(Long moeimId) {
        this.moeimId = moeimId;
    }

    public List<UploadFileInfo> getFiles() {
        return files;
    }
    public void setFiles(List<UploadFileInfo> files) {
        this.files = files;
    }
}
