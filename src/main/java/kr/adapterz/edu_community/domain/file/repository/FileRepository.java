package kr.adapterz.edu_community.domain.file.repository;

import kr.adapterz.edu_community.domain.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByFilePath(String filePath);
}
