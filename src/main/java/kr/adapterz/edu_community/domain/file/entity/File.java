package kr.adapterz.edu_community.domain.file.entity;

import jakarta.persistence.*;
import kr.adapterz.edu_community.global.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "files")
@NoArgsConstructor(access = PROTECTED)
public class File extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_path", nullable = false)
    private String filePath;

    @Column(name="file_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileCategory fileCategory;

    @Column(name="uploader_id")
    private Long uploaderId;  // 업로더 추적용 (nullable)

    // Constructor
    public File(String filePath, FileCategory fileCategory, Long uploaderId) {
        this.filePath = filePath;
        this.fileCategory = fileCategory;
        this.uploaderId = uploaderId;
    }

    // Business Methods
}
