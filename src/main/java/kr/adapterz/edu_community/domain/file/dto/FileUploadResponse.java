package kr.adapterz.edu_community.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadResponse {

    private String fileUrl;

    public static FileUploadResponse of(String fileUrl) {
        return new FileUploadResponse(fileUrl);
    }
}
