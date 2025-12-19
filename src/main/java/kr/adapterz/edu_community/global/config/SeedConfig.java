package kr.adapterz.edu_community.global.config;

import kr.adapterz.edu_community.domain.post.entity.Post;
import kr.adapterz.edu_community.domain.post.repository.PostRepository;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("development")
@RequiredArgsConstructor
public class SeedConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull  ... args) {
        createDummyUser();
        createDummyPosts();
    }

    private void createDummyUser() {
        if (userRepository.count() > 0) {
            return;
        }

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            users.add(
                    new User(
                            "user" + i + "@test.kr",
                            passwordEncoder.encode("12341234aS!"),
                            "유저" + i,
                            null
                    )
            );
        }

        userRepository.saveAll(users);
    }

    private void createDummyPosts() {
        if (postRepository.count() > 0) return;

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;

        postRepository.saveAll(generatePosts(users));
    }

    private List<Post> generatePosts(List<User> users) {
        List<Post> posts = new ArrayList<>();
        int postIndex = 1;

        for (User user : users) {
            for (int i = 1; i <= 3; i++) {
                posts.add(new Post(
                        postIndex++ + "번째 게시글",
                        user.getNickname() + "의 " + i + "번째 게시글입니다.",
                        user
                ));
            }
        }
        return posts;
    }
}
