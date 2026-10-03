package com.battleiq.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.battleiq.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameCompletionEventListener {

    private final UserProfileService userProfileService;

    @EventListener
    public void onGameCompleted(GameCompletedEvent event) {
        userProfileService.applyGameResult(event.getUserId(), event.getTotalScore());
    }
}
