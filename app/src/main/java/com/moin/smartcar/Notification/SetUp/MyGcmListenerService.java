package com.moin.smartcar.Notification.SetUp;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.moin.smartcar.HomePage;
import com.moin.smartcar.MainActivity;
import com.moin.smartcar.Network.MyApplication;
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
            if (data.getString("message").equalsIgnoreCase("Executive Verified")) {
                NewMessageStr str = new NewMessageStr();
                str.BookingId = data.getString("userId");
                str.Actualmessage = "Executive Verified";
                str.status = 1;
                EventBus.getDefault().post(str);
//                showAlert("Executive Verified");
            }
            if (data.getString("message").equalsIgnoreCase("Car Ready For PickUp")) {
                NewMessageStr str = new NewMessageStr();
                str.BookingId = data.getString("userId");
                str.Actualmessage = "Car Ready For PickUp";
                str.status = 2;
                EventBus.getDefault().post(str);
//                showAlert("Car Ready For PickUp");

            }
            if (data.getString("message").equalsIgnoreCase("Car Successfully Delivered")) {
                NewMessageStr str = new NewMessageStr();
                str.BookingId = data.getString("userId");
                str.Actualmessage = "Car Successfully Delivered";
                str.status = 3;
                EventBus.getDefault().post(str);
//                showAlert("Car Successfully Delivered");

            }
        } catch (Exception e) {
            Log.d("some err", e.toString());
        }

        sendNotification(message);
    }

    private void showAlert(String message){
        new AlertDialog.Builder(MyApplication.getAppContext())
                .setTitle("Failure")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

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

//        Bitmap imageBitmap = BitmapDrawable()

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Smart Car")
                .setContentText(message)
//                .setLargeIcon(getResources().getDrawable(R.drawable.bg))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
