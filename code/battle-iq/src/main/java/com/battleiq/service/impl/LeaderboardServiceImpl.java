package com.battleiq.service.impl;

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
import com.battleiq.mapper.LeaderboardMapper;
import com.battleiq.repository.UserProfileRepository;
import com.battleiq.service.LeaderboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final int MAX_PAGE_SIZE = 50;

    private final UserProfileRepository userProfileRepository;

    @Override
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
            entries.add(LeaderboardMapper.toEntry(profile, rank++));
        }
        return new PageImpl<>(entries, pageable, profiles.getTotalElements());
    }
}
