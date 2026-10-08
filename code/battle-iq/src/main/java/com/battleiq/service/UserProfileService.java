package com.battleiq.service;

import com.battleiq.dto.request.ProfileUpdateDTO;

public interface UserProfileService {

    /**
     * อัปเดตคะแนนรวม เลเวล และ win streak หลังเล่นจบ 1 รอบ
     */
    void applyGameResult(Long userId, Integer score);

    void updateProfile(Long userId, ProfileUpdateDTO request);
}
