package kr.adapterz.edu_community.domain.comment.entity;

import jakarta.persistence.*;
import kr.adapterz.edu_community.domain.post.entity.Post;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.global.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name="comments")
@NoArgsConstructor(access=PROTECTED)
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="author_id", nullable=false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id", nullable=false)
    private Post post;

    // Constructor
    public Comment(String content) {
        this.content = content;
    }

    public Comment(String content, User author, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }

    // Factory Method
    public static Comment create(
            String content,
            User author,
            Post post
    ) {
        return new Comment(content, author, post);
    }

    // Business Methods
    public void update(String content) {
        this.content = content;
    }
    public void delete() {
        this.deletedAt = java.time.LocalDateTime.now();
    }
}
