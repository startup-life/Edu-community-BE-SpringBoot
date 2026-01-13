package kr.adapterz.edu_community.domain.user.service;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.user.dto.request.UpdateUserRequest;
import kr.adapterz.edu_community.domain.user.dto.response.UserInfoResponse;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserQueryRepository;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.exception.NotFoundException;
import kr.adapterz.edu_community.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final FileRepository fileRepository;

    // 유저 정보 가져오기
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userQueryRepository.findActiveByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        return UserInfoResponse.from(user);
    }

    // 인증 상태 확인
    /*@Transactional(readOnly = true)
    public AuthStatusResponse checkAuthStatus(Long userId) {
        User user = userQueryRepository.findActiveByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        // 프로필 이미지 (null이면 프론트엔드에서 기본 이미지 사용)
        String profileImageUrl = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .orElse(null);

        return AuthStatusResponse.of(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getNickname(),
                profileImageUrl
        );
    }*/

    // 유저 정보 수정
    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userQueryRepository.findActiveByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        user.updateNickname(updateUserRequest.getNickname());
        applyProfileImage(user, updateUserRequest.getProfileImageUrl());
    }

    // 회원 탈퇴
    public void withdrawUser(Long userId) {
    User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        user.withdraw();
        userRepository.save(user);
    }

    // ========== Private Methods ==========

    // 프로필 이미지 적용
    private void applyProfileImage(User user, String requestedPath) {
        // 1. null 이면 제거
        if (requestedPath == null) {
            user.updateProfileImage(null);
            return;
        }

        // [핵심] 프론트에서 온 전체 URL(http://...)을 DB용 상대 경로(/public/...)로 변환
        String relativePath = FileUtil.extractPathFromUrl(requestedPath);

        // 2. 현재 경로와 비교 (불필요한 갱신 방지)
        String currentPath = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .orElse(null);

        // 둘 다 상대 경로인 상태에서 비교해야 정확함
        if (relativePath.equals(currentPath)) {
            return;
        }

        // 3. 파일 조회 (상대 경로로 조회)
        File newProfileImage = fileRepository.findByFilePath(relativePath)
                .orElseThrow(() -> new NotFoundException("PROFILE_IMAGE_NOT_FOUND"));

        user.updateProfileImage(newProfileImage);
    }
}
