package com.kosa.moimeasy.gallery.dto;

public class UploadFileInfo {
    private String fileName;     // 파일명
    private String downloadUrl;  // Firebase Storage에서 획득한 다운로드 URL

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
