package com.agit.peerflow.service.Impl;

import com.agit.peerflow.domain.entity.User;
import com.agit.peerflow.repository.LoginAttemptLogRepository;
import com.agit.peerflow.repository.PasswordResetLogRepository;
import com.agit.peerflow.repository.UserRepository;
import com.agit.peerflow.service.AiSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiSecurityServiceImpl implements AiSecurityService {

    private final UserRepository userRepository;
    private final LoginAttemptLogRepository loginAttemptLogRepository;
    private final PasswordResetLogRepository passwordResetLogRepository;
    private final RestTemplate restTemplate;  // Bean 주입
    private final String AI_URL = "http://localhost:5001/detect";

    @Override
    public boolean checkLoginAttempt(User user, int attempts, String ip, String device) {
        return checkAiBlocked(user, "loginAttempts", attempts);
    }

    @Override
    public boolean checkResetAttempt(User user, int attempts, String ip, String device) {
        return checkAiBlocked(user, "resetAttempts", attempts);
    }

    private boolean checkAiBlocked(User user, String key, int attempts) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", user.getEmail());
        payload.put(key, attempts);

        ResponseEntity<Map> response = restTemplate.postForEntity(AI_URL, payload, Map.class);
        boolean blocked = response.getBody() != null && (Integer) response.getBody().get("outlier") == 1;

        if (blocked) {
            if ("loginAttempts".equals(key)) user.markAiLoginBlocked();
            else user.markAiResetBlocked();
            userRepository.save(user);
        }
        return blocked;
    }

    @Override
    public int countTodayLoginAttempts(User user) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        return loginAttemptLogRepository.countTodayByUserEmail(user.getEmail(), start, end);
    }

    @Override
    public int countTodayResetAttempts(User user) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        return passwordResetLogRepository.countTodayByEmail(user.getEmail(), start, end);
    }
}
