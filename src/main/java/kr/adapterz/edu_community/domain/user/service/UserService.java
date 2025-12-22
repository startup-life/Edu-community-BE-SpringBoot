package kr.adapterz.edu_community.domain.user.service;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.user.dto.request.UpdateUserRequest;
import kr.adapterz.edu_community.domain.user.dto.response.UserInfoResponse;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    // 유저 정보 가져오기
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        String profileImagePath = "/public/images/profile/default.jpg";

        if (user.getProfileImageId() != null) {
            profileImagePath = fileRepository.findById(user.getProfileImageId())
                    .map(File::getFilePath)
                    .orElse(profileImagePath);
        }

        return UserInfoResponse.of(
                user,
                profileImagePath
        );
    }

    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        user.updateNickname(updateUserRequest.getNickname());

        if (updateUserRequest.getProfileImagePath() != null) {
            File newProfileImage = fileRepository.findByFilePath(updateUserRequest.getProfileImagePath())
                    .orElseThrow(() -> new NotFoundException("file_not_found"));
            user.updateProfileImageId(newProfileImage.getId());
        }

        userRepository.save(user);
    }

    public void withdrawUser(Long userId) {
    User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        user.withdraw();
        userRepository.save(user);
    }
}
