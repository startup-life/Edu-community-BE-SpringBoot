package kr.adapterz.edu_community.domain.post.entity;

import jakarta.persistence.*;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.global.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = PROTECTED)
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File attachFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer hits = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;

    // Constructor
    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Post(
            String title,
            String content,
            User author,
            File attachFile
    ) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.attachFile = attachFile;
    }

    // Factory method
    public static Post create(
            String title,
            String content,
            User author,
            File attachFile
    ) {
        return new Post(title, content, author, attachFile);
    }

    public static Post createWithFile(
            String title,
            String content,
            User author,
            File attachFile
    ) {
        return new Post(title, content, author, attachFile);
    }

    // Business methods
    public void update(
            String title,
            String content,
            File attachFile
    ) {
        this.title = title;
        this.content = content;
        this.attachFile = attachFile;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
