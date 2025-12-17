package kr.adapterz.edu_community.domain.auth.service;

import jakarta.transaction.Transactional;
import kr.adapterz.edu_community.domain.auth.dto.SignupRequest;
import kr.adapterz.edu_community.domain.auth.dto.SignupResponse;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    public SignupResponse signup(SignupRequest signupRequest) {
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .nickname(signupRequest.getNickname())
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }
}