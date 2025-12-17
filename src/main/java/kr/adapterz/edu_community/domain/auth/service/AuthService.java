package kr.adapterz.edu_community.domain.auth.service;

import jakarta.transaction.Transactional;
import kr.adapterz.edu_community.domain.auth.dto.LoginRequest;
import kr.adapterz.edu_community.domain.auth.dto.LoginResponse;
import kr.adapterz.edu_community.domain.auth.dto.SignupRequest;
import kr.adapterz.edu_community.domain.auth.dto.SignupResponse;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.common.exception.AuthorizedException;
import kr.adapterz.edu_community.global.common.exception.DuplicateException;
import kr.adapterz.edu_community.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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

    // 로그인
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthorizedException("invalid_credentials")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new AuthorizedException("invalid_credentials");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        return LoginResponse.of(
                user,
                accessToken,
                jwtProvider.getAccessTokenValidityInMilliseconds()
        );
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