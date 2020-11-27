package com.itextpdf.dito.manager.service.scheduler;

import com.itextpdf.dito.manager.entity.FailedLoginAttemptEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
public class UserUnlockScheduler {
    private final UserRepository userRepository;
    private final FailedLoginRepository failedLoginRepository;

    UserUnlockScheduler(UserRepository userRepository, FailedLoginRepository failedLoginRepository) {
        this.userRepository = userRepository;
        this.failedLoginRepository = failedLoginRepository;
    }

    @Scheduled(
            fixedDelay = 300000,
            initialDelay = 1000
    )
    public void searchAndUnlockUsers() {
        final Timestamp unlockTime = new Timestamp(System.currentTimeMillis() - 1800000);
        final List<UserEntity> lockedUsers = userRepository.findAllByLockedIsTrue();

        lockedUsers.forEach(userEntity -> {
            Optional<FailedLoginAttemptEntity> attempt = failedLoginRepository.findFirstByUserIdOrderByVersionDesc(userEntity.getId());
            attempt.ifPresent(a -> {
                if (attempt.get().getVersion().before(unlockTime)) {
                    userEntity.setLocked(false);
                }
            });
        });
        userRepository.saveAll(lockedUsers);
    }
}
