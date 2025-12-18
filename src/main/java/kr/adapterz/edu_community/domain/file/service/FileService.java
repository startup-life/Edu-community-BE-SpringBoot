package kr.adapterz.edu_community.domain.file.service;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.entity.FileCategory;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.global.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
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
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));  // 실제 저장 경로 (쓰기 가능)
    private static final Path PROFILE_DIR = PROJECT_ROOT.resolve("uploads/profile");  // 프로필 이미지 저장 디렉토리
    private static final String PROFILE_URL = "/public/profile";    // 클라이언트 접근 URL
    private final FileRepository fileRepository;

    public File uploadProfileImage(
            MultipartFile file,
            Long userId
    ) throws FileUploadException {
        String extension = extractExtension(file);
        String filename = generateProfileFilename(extension);
        Path savePath = PROFILE_DIR.resolve(filename);

        try {
            Files.createDirectories(savePath.getParent());
            file.transferTo(savePath.toFile());
        } catch (IOException exception) {
            throw new FileUploadException("profile_image_upload_failed", exception);
        }

        return  fileRepository.save(
                new File(
                        PROFILE_URL + filename,
                        FileCategory.PROFILE_IMAGE,
                        userId
                )
        );
    }

    // 파일 확장자 추출
    private String extractExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new BadRequestException("required_file_name");
        }

        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null) {
            throw new BadRequestException("invalid_file_extension");
        }

        return extension;
    }

    // 프로필 이미지 파일명 생성
    private String generateProfileFilename(String extension) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String uuid = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        return "profileimage-" + timestamp + "-" + uuid + "." + extension;
    }
}
