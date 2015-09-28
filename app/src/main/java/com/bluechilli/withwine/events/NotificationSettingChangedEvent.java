package com.bluechilli.withwine.events;

import com.bluechilli.withwine.models.User;

/**
 * Created by monishi on 23/02/15.
 */
public class NotificationSettingChangedEvent {
    private final User user;

    public NotificationSettingChangedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
