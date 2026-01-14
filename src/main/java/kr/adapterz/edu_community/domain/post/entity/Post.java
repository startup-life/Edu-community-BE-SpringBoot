package kr.adapterz.edu_community.domain.post.entity;

import jakarta.persistence.*;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.global.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = PROTECTED)
@SQLRestriction("deleted_at is null")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Post(String title, String content, User author, File attachFile) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.attachFile = attachFile;
    }

    public static Post create(String title, String content, User author, File attachFile) {
        return new Post(title, content, author, attachFile);
    }

    public static Post createWithFile(String title, String content, User author, File attachFile) {
        return new Post(title, content, author, attachFile);
    }

    public void update(String title, String content, File attachFile) {
        this.title = title;
        this.content = content;
        this.attachFile = attachFile;
    }

    public void withdraw() {
        this.deletedAt = LocalDateTime.now();
    }

    public void increaseCommentCount() {
        this.commentCount += 1;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount -= 1;
        }
    }

    public void increaseHits() {
        this.hits += 1;
    }

    public void increaseLikes() {
        this.likeCount += 1;
    }

    public void decreaseLikes() {
        if (this.likeCount > 0) {
            this.likeCount -= 1;
        }
    }
}
