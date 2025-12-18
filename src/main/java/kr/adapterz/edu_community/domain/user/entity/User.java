package kr.adapterz.edu_community.domain.user.entity;

import jakarta.persistence.*;
import kr.adapterz.edu_community.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(name = "file_id")
    private Long profileImageId;

    // Constructor
    public User(String email, String password, String nickname, Long profileImageId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageId = profileImageId;
    }

    // Business Methods
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    public void updatePassword(String password) { this.password = password; }
    public void updateProfileImageId(Long profileImageId) {
        this.profileImageId = profileImageId;
    }
}
