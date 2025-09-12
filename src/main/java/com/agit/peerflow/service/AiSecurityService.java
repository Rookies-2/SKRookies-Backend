package com.agit.peerflow.service;

import com.agit.peerflow.domain.entity.User;
import java.util.Map;

public interface AiSecurityService {
    boolean checkLoginAttempt(User user, int attempts, String ip, String device);
    boolean checkResetAttempt(User user, int attempts, String ip, String device);
    int countTodayLoginAttempts(User user);
    int countTodayResetAttempts(User user);
}