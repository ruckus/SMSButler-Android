package com.codycaughlan.smsbutler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {
    private static final String TAG = "IncomingSms";
    
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(Constants.PREFS_TITLE, Context.MODE_PRIVATE);
        
        boolean enabled = preferences.getBoolean(Constants.PREFS_ENABLED, false);
        if(!enabled) {
            Log.i(TAG, "SMS Butler: not enabled.");
            return;
        } else {
            Log.i(TAG, "SMS Butler: Enabled. At your bidding!");
        }

        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i(TAG, "senderNum: " + senderNum + "; message: " + message);

                    String autoReplyMessage = preferences.getString(Constants.PREFS_AUTO_REPLY_KEY, "");
                    if(autoReplyMessage != null && autoReplyMessage.trim().length() > 0) {
                        Log.i(TAG, "sending back: " + autoReplyMessage);
                        sms.sendTextMessage(phoneNumber, null, autoReplyMessage, null, null);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception smsReceiver: " + e.toString());
        }
        
    }
}