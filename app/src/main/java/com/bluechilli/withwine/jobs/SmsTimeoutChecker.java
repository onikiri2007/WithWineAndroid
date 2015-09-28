package com.bluechilli.withwine.jobs;

import com.bluechilli.withwine.events.SmsTimeoutEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by monishi on 29/01/15.
 */
public class SmsTimeoutChecker implements Runnable {
    @Override
    public void run() {
        EventBus.getDefault().post(new SmsTimeoutEvent());
    }
}
