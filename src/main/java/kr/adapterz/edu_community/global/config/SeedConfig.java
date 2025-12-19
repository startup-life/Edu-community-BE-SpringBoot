package kr.adapterz.edu_community.global.config;

import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@Profile("development")
@RequiredArgsConstructor
public class SeedConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull  ... args) {

        if (userRepository.count() > 0) {
            return;
        }

        List<User> users = List.of(
                new User("user1@test.kr", passwordEncoder.encode("12341234aS!"), "유저1", null),
                new User("user2@test.kr", passwordEncoder.encode("12341234aS!"), "유저2", null),
                new User("user3@test.kr", passwordEncoder.encode("12341234aS!"), "유저3", null),
                new User("user4@test.kr", passwordEncoder.encode("12341234aS!"), "유저4", null),
                new User("user5@test.kr", passwordEncoder.encode("12341234aS!"), "유저5", null)
        );

        userRepository.saveAll(users);
    }
}
