package kr.adapterz.edu_community.domain.file.repository;

import kr.adapterz.edu_community.domain.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
