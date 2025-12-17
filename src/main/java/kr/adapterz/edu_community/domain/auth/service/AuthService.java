package kr.adapterz.edu_community.domain.auth.service;

import kr.adapterz.edu_community.domain.auth.dto.*;
import kr.adapterz.edu_community.domain.auth.entity.RefreshToken;
import kr.adapterz.edu_community.domain.auth.repository.RefreshTokenRepository;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.common.exception.AuthorizedException;
import kr.adapterz.edu_community.global.common.exception.DuplicateException;
import kr.adapterz.edu_community.global.common.exception.NotFoundException;
import kr.adapterz.edu_community.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FileRepository fileRepository;

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
    public LoginResult login(LoginRequest request) {

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

        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(
                new RefreshToken(
                        refreshToken,
                        user.getId(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new LoginResult(
                LoginResponse.of(user, accessToken, jwtProvider.getAccessTokenValidityInMilliseconds()),
                refreshToken
        );
    }

    // 액세스 토큰 재발급
    public TokenResult refreshAccessToken(String refreshToken) {

        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthorizedException("invalid_refresh_token"));

        if (saved.isExpired()) {
            refreshTokenRepository.delete(saved);
            throw new AuthorizedException("refresh_token_expired");
        }

        User user = userRepository.findById(saved.getUserId())
                .orElseThrow(() -> new AuthorizedException("invalid_refresh_token"));

        String newAccessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        // Refresh Token 회전 (Rotation)
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.delete(saved);
        refreshTokenRepository.save(
            new RefreshToken(
                newRefreshToken,
                user.getId(),
                LocalDateTime.now().plusDays(14)
            )
        );

        return new TokenResult(
                new TokenResponse(newAccessToken, 3600),
                newRefreshToken
        );
    }

    // 로그인 상태 검증
    public AuthStatusResponse checkAuthStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        String profileImagePath = "/public/images/profile/default.jpg";

        if (user.getProfileImageId() != null) {
            profileImagePath = fileRepository.findById(user.getProfileImageId())
                    .map(File::getFilePath)
                    .orElse(profileImagePath);
        }

        return AuthStatusResponse.of(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getNickname(),
                profileImagePath
        );
    }

    // 중복 이메일 검사
    @Transactional(readOnly = true)
    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException("email_already_exists");
        }
    }

    // 중복 닉네임 검사
    @Transactional(readOnly = true)
    public void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateException("nickname_already_exists");
        }
    }
}