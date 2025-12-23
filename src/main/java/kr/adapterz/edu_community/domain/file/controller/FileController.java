package kr.adapterz.edu_community.domain.file.controller;

import kr.adapterz.edu_community.domain.file.dto.FileUploadResponse;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.service.FileService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 프로필 이미지 업로드
    @PostMapping("/users/me/profile-image")
    public ApiResponse<FileUploadResponse> uploadProfileImage(
            Authentication authentication,
            @RequestPart("profileImage") MultipartFile file
    ) throws FileUploadException {
        Long userId = resolveUserId(authentication);

        File savedFile = fileService.uploadProfileImage(file, userId);

        return ApiResponse.ok(
                "profile_image_upload_success",
                new FileUploadResponse(savedFile.getFilePath())
        );
    }

    // 게시글 첨부파일 업로드
    @PostMapping("/posts/image")
    public ApiResponse<FileUploadResponse> uploadPostAttachImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("attachImage") MultipartFile file
    ) throws FileUploadException {
        File savedFile = fileService.uploadPostAttachImage(file, userId);

        return ApiResponse.ok(
                "post_attach_image_upload_success",
                new FileUploadResponse(savedFile.getFilePath())
        );
    }

    // ========== Private Methods ==========

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        if (!authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return Long.valueOf(authentication.getName());
    }
}
