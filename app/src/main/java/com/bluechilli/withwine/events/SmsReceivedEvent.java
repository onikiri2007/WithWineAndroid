package com.bluechilli.withwine.events;

/**
 * Created by monishi on 28/01/15.
 */
public class SmsReceivedEvent {

    private final String code;

    public SmsReceivedEvent(String code) {

        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
