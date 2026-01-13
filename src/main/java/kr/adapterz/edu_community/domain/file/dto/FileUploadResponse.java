package kr.adapterz.edu_community.domain.file.dto;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.global.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadResponse {

    private String fileUrl;

    public static FileUploadResponse of(String fileUrl) {
        return new FileUploadResponse(fileUrl);
    }

    public static FileUploadResponse from(File file) {
        // DB의 상대 경로(/public/...)를 전체 URL(http://...)로 변환
        String fullUrl = FileUtil.toFullUrl(file.getFilePath());

        return of(fullUrl);
    }
}
