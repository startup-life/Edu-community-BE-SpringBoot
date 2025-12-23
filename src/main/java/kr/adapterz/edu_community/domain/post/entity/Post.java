package kr.adapterz.edu_community.domain.post.entity;

import jakarta.persistence.*;
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

    @Column(name = "file_id")
    private Long attachFileId;

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

    public Post(String title, String content, Long attachFileId, User author) {
        this.title = title;
        this.content = content;
        this.attachFileId = attachFileId;
        this.author = author;
    }

    // Factory method
    public static Post create(
            String title,
            String content,
            User author
    ) {
        return new Post(title, content, author);
    }

    public static Post createWithFile(
            String title,
            String content,
            Long attachFileId,
            User author
    ) {
        return new Post(title, content, attachFileId, author);
    }

    // Business methods
    public void update(
            String title,
            String content,
            Long attachFileId
    ) {
        this.title = title;
        this.content = content;
        this.attachFileId = attachFileId;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
