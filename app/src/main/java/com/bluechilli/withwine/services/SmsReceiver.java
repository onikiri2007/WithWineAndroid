package com.bluechilli.withwine.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.bluechilli.withwine.events.SmsReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by monishi on 28/01/15.
 */
public class SmsReceiver extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    final static String ORIGINATION_ADDRESS = "WithWine";
    final static String SMS_BUNDLE_DATA_KEY = "pdus";
    final static Pattern pattern = Pattern.compile("[0-9]*");

    @Override
    public void onReceive(Context context, Intent intent) {
       final Bundle bundle = intent.getExtras();
       try {

           if(bundle != null) {

               final Object[] objects = (Object[]) bundle.get(SMS_BUNDLE_DATA_KEY);

               for(int i = 0; i < objects.length; i++) {
                   SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) objects[i]);
                   String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                   String message = currentMessage.getDisplayMessageBody();

                   if((phoneNumber != null
                           && !TextUtils.isEmpty(phoneNumber)
                           && phoneNumber == ORIGINATION_ADDRESS) || (message != null && !TextUtils.isEmpty(message) && message.trim().startsWith(ORIGINATION_ADDRESS))) {

                       EventBus.getDefault().post(new SmsReceivedEvent( extractCode(message)));
                   }

               }
           }

       }
       catch(Exception e) {
         Log.d("error", e.getLocalizedMessage());
       }
    }

    private String extractCode(String message) {

      String code = "";
      Matcher matcher = pattern.matcher(message);

        while(matcher.find()) {
            code = matcher.group();

            if(code != null && !TextUtils.isEmpty(code)) {
                break;
            }
       }

       return code;
    }
}
