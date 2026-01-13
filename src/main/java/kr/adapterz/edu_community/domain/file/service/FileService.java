package kr.adapterz.edu_community.domain.file.service;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.global.exception.BusinessException;
import kr.adapterz.edu_community.global.exception.InvalidFileException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static kr.adapterz.edu_community.domain.file.entity.File.createPostAttachImage;
import static kr.adapterz.edu_community.domain.file.entity.File.createProfileImage;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));  // 실제 저장 경로 (쓰기 가능)

    private static final Path PROFILE_DIR = PROJECT_ROOT.resolve("uploads/profile");  // 프로필 이미지 저장 디렉토리
    private static final Path POST_DIR = PROJECT_ROOT.resolve("uploads/post");      // 게시글 첨부파일 저장 디렉토리

    private static final String PROFILE_URL = "/public/profile/";    // 클라이언트 접근 URL
    private static final String POST_URL = "/public/post/";          // 클라이언트 접근 URL

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif"); // 허용할 확장자 목록

    // 프로필 이미지 업로드
    public File uploadProfileImage(MultipartFile file, Long userId) throws FileUploadException {
        return uploadFile(file, userId, PROFILE_DIR, PROFILE_URL, "profile");
    }

    // 게시글 첨부파일 업로드
    public File uploadPostAttachImage(MultipartFile file, Long userId) throws FileUploadException {
        return uploadFile(file, userId, POST_DIR, POST_URL, "post");
    }

    // 실제 업로드를 수행하는 내부 메서드
    private File uploadFile(MultipartFile file, Long userId, Path dirPath, String urlPrefix, String type) throws FileUploadException {
        String extension = extractAndValidateExtension(file); // 확장자 검증 포함
        String filename = generateFilename(type, extension);  // 파일명 생성 통합
        Path savePath = dirPath.resolve(filename);

        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            file.transferTo(savePath.toFile());
        } catch (IOException exception) {
            throw new BusinessException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String dbFilePath = urlPrefix + filename;

        // Entity 생성 로직 분기 (Factory Method 패턴 사용 중이라 if문 처리)
        if ("profile".equals(type)) {
            return fileRepository.save(createProfileImage(dbFilePath, userId));
        } else {
            return fileRepository.save(createPostAttachImage(dbFilePath, userId));
        }
    }

    // ========== Private Methods ==========

    // 확장자 추출 및 검증
    private String extractAndValidateExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException(file.getName(), "FILE_NAME_REQUIRED");
        }

        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(file.getName(), "INVALID_FILE_EXTENSION"); // 허용되지 않은 확장자
        }

        return extension;
    }

    // 파일명 생성 통합 (prefix만 받아서 처리)
    private String generateFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String uuid = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        // 예: profile-20260113...jpg
        return prefix + "-" + timestamp + "-" + uuid + "." + extension;
    }
}
