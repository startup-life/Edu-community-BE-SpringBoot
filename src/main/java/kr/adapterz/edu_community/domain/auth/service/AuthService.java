package kr.adapterz.edu_community.domain.auth.service;

import jakarta.transaction.Transactional;
import kr.adapterz.edu_community.domain.auth.dto.SignupRequest;
import kr.adapterz.edu_community.domain.auth.dto.SignupResponse;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.common.exception.DuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public SignupResponse signup(SignupRequest signupRequest) {
        validateDuplicateEmail(signupRequest.getEmail());
        validateDuplicateNickname(signupRequest.getNickname());

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .nickname(signupRequest.getNickname())
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }

    // 중복 이메일 검사
    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException("email_already_exists");
        }
    }

    // 중복 닉네임 검사
    public void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateException("nickname_already_exists");
        }
    }
}