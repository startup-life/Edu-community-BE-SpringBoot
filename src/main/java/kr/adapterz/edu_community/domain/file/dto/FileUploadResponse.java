package kr.adapterz.edu_community.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadResponse {

    private String filePath;

    public static FileUploadResponse of(String filePath) {
        return new FileUploadResponse(filePath);
    }
}
