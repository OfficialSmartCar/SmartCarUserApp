package com.moin.smartcar.Notification.SetUp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.moin.smartcar.HomePage;
import com.moin.smartcar.MainActivity;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import de.greenrobot.event.EventBus;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
        mySingelton.notificationCount++;

        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        String subject = data.getString("subject");

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        try {
            if (data.getString("gcm.notification.title").equalsIgnoreCase("newmessage")) {
                NewMessageStr str = new NewMessageStr();
                str.senderId = data.getString("senderid");
                str.Actualmessage = data.getString("message");
                EventBus.getDefault().post(str);
            }
        } catch (Exception e) {
            Log.d("some err", e.toString());
        }


        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {

        Intent intent = new Intent();

//        try{
//            switch (URLSingelton.getMy_SingeltonData_Reference().activityName){
//                case "home" : intent = new Intent(this,homePage.class);break;
//                case "LiveFeeds" : intent = new Intent(this,liveFeed.class);break;
//                case "Events" : intent = new Intent(this,EventsList.class);break;
//                case "AdminMessaging" : intent = new Intent(this,Messaging.class);break;
//                case "userMessaging" : intent = new Intent(this,MessageListing.class);break;
//                case "Gallery" : intent = new Intent(this,ActivityGallery.class);break;
//                default: intent = new Intent(this,MainActivity.class);break;
//            }
//        }catch (Exception e){
//            intent = new Intent(this, MainActivity.class);
//        }

        try {
            DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
            //Toast.makeText(this,mySingelton.isLogin+"",Toast.LENGTH_LONG).show();
//            if (message.equalsIgnoreCase("Check new feed")) {
//                intent = new Intent(this, liveFeed.class);
//            } else {
//                if (message.equalsIgnoreCase("Check new images")) {
//                    intent = new Intent(this, ActivityGallery.class);
//                } else {
//                    if (message.equalsIgnoreCase("Check new event")) {
//                        intent = new Intent(this, EventsList.class);
//                    } else {
//                        if (mySingelton.isAdmin == 1) {
//                            new Intent(this, Messaging.class);
//                        } else {
//                            new Intent(this, MessageListing.class);
//                        }
//                    }
//                }
//            }
            new Intent(this, HomePage.class);
        } catch (Exception e) {
            intent = new Intent(this, MainActivity.class);
        }


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Smart Car")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
