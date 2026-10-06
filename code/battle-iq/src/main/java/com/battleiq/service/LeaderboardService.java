package com.battleiq.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.LeaderboardEntryDTO;
import com.battleiq.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private static final int MAX_PAGE_SIZE = 50;

    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public Page<LeaderboardEntryDTO> getLeaderboard(int page, int size) {
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("totalScore"), Sort.Order.asc("id")));
        Page<UserProfile> profiles = userProfileRepository.findAll(pageable);

        List<LeaderboardEntryDTO> entries = new ArrayList<>();
        int rank = page * size + 1;
        for (UserProfile profile : profiles.getContent()) {
            entries.add(LeaderboardEntryDTO.builder()
                    .rank(rank++)
                    .userId(profile.getId())
                    .username(profile.getUser().getUsername())
                    .fullName(profile.getFullName())
                    .totalScore(profile.getTotalScore())
                    .level(profile.getLevel())
                    .build());
        }
        return new PageImpl<>(entries, pageable, profiles.getTotalElements());
    }
}