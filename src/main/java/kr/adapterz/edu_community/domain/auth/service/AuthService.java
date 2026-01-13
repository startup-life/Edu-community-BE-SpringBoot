package kr.adapterz.edu_community.domain.auth.service;

import kr.adapterz.edu_community.domain.auth.dto.internal.LoginResult;
import kr.adapterz.edu_community.domain.auth.dto.internal.TokenResult;
import kr.adapterz.edu_community.domain.auth.dto.request.ChangePasswordRequest;
import kr.adapterz.edu_community.domain.auth.dto.request.LoginRequest;
import kr.adapterz.edu_community.domain.auth.dto.request.SignupRequest;
import kr.adapterz.edu_community.domain.auth.dto.response.AuthStatusResponse;
import kr.adapterz.edu_community.domain.auth.dto.response.LoginResponse;
import kr.adapterz.edu_community.domain.auth.dto.response.SignupResponse;
import kr.adapterz.edu_community.domain.auth.dto.response.TokenInfo;
import kr.adapterz.edu_community.domain.auth.entity.RefreshToken;
import kr.adapterz.edu_community.domain.auth.repository.RefreshTokenRepository;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserQueryRepository;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.common.exception.AuthorizedException;
import kr.adapterz.edu_community.global.common.exception.DuplicateException;
import kr.adapterz.edu_community.global.common.exception.NotFoundException;
import kr.adapterz.edu_community.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FileRepository fileRepository;

    // 회원가입
    public SignupResponse signup(SignupRequest signupRequest) {
        validateDuplicateEmail(signupRequest.getEmail());
        validateDuplicateNickname(signupRequest.getNickname());

        File profileImage = resolveProfileImage(signupRequest.getProfileImageUrl());

        User user = new User(
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getNickname(),
                profileImage
        );

        User savedUser = userRepository.save(user);

        return SignupResponse.of(savedUser.getId());
    }

    // 로그인
    public LoginResult login(LoginRequest loginRequest) {
        User user = userQueryRepository.findActiveByEmailWithProfileImage(loginRequest.getEmail())
                .orElseThrow(() -> new AuthorizedException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        )) {
            throw new AuthorizedException("INVALID_CREDENTIALS");
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

        User user = userRepository.findActiveById(saved.getUserId())
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
                new TokenInfo(newAccessToken, 3600),
                newRefreshToken
        );
    }

    // 로그인 상태 검증
    @Transactional(readOnly = true)
    public AuthStatusResponse checkAuthStatus(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        // 프로필 이미지 (null이면 프론트엔드에서 기본 이미지 사용)
        String profileImageUrl = null;
        if (user.getProfileImage() != null) {
            profileImageUrl = user.getProfileImage().getFilePath();
        }

        return AuthStatusResponse.of(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getNickname(),
                profileImageUrl
        );
    }

    // 비밀번호 변경
    public void changePassword(
            Long userId,
            @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        String newPassword = passwordEncoder.encode(changePasswordRequest.getPassword());
        user.updatePassword(newPassword);
        userRepository.save(user);
    }

    // 중복 이메일 검사
    @Transactional(readOnly = true)
    public void validateDuplicateEmail(String email) {
        if (userRepository.existsActiveByEmail(email)) {
            throw new DuplicateException("EMAIL_ALREADY_EXISTS");
        }
    }

    // 중복 닉네임 검사
    @Transactional(readOnly = true)
    public void validateDuplicateNickname(String nickname) {
        if (userRepository.existsActiveByNickname(nickname)) {
            throw new DuplicateException("NICKNAME_ALREADY_EXISTS");
        }
    }

    // ========== Private Methods ==========
    private File resolveProfileImage(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return null;
        }

        // 1. 전체 URL에서 도메인을 떼고 상대 경로만 추출
        String relativePath = extractPathFromUrl(profileImageUrl);

        // 2. 추출된 상대 경로로 DB 조회
        return fileRepository.findByFilePath(relativePath)
                .orElseThrow(() -> new NotFoundException("PROFILE_IMAGE_NOT_FOUND"));
    }

    // URL에서 도메인을 제외한 경로(Path)만 추출하는 헬퍼 메서드
    private String extractPathFromUrl(String url) {
        try {
            // URI 파싱을 통해 path 부분만 가져옴
            URI uri = new URI(url);
            String path = uri.getPath();

            // 혹시 path가 null이면(잘못된 URL 등) 원본 반환
            return path != null ? path : url;

        } catch (URISyntaxException e) {
            // URL 형식이 아니라면(이미 상대경로이거나 잘못된 문자열) 그냥 원본 반환
            return url;
        }
    }
}