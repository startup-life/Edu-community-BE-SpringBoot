package kr.adapterz.edu_community.domain.file.controller;

import kr.adapterz.edu_community.domain.file.dto.FileUploadResponse;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.service.FileService;
import kr.adapterz.edu_community.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 프로필 이미지 업로드
    @PostMapping("/users/me/profile-image")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadProfileImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("profileImage") MultipartFile file
    ) throws FileUploadException {
        File savedFile = fileService.uploadProfileImage(file, userId);

        // 현재 요청의 도메인+포트를 가져와서 풀 URL 생성
        // 예: http://localhost:8080 + /public/profile/image.jpg
        String fileUrl = getFullUrl(savedFile.getFilePath());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "PROFILE_IMAGE_UPLOADED",
                        new FileUploadResponse(fileUrl)
                ));
    }

    // 게시글 첨부파일 업로드
    @PostMapping("/posts/image")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadPostAttachImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("attachImage") MultipartFile file
    ) throws FileUploadException {
            File savedFile = fileService.uploadPostAttachImage(file, userId);
            String fileUrl = getFullUrl(savedFile.getFilePath());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.of(
                            "POST_FILE_UPLOADED",
                            new FileUploadResponse(fileUrl)
                ));
    }

    // 도메인 붙이는 헬퍼 메서드
    private String getFullUrl(String relativePath) {
        // fromCurrentContextPath()는 "http://localhost:8080" 부분까지 가져옵니다.
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + relativePath;
    }
}
