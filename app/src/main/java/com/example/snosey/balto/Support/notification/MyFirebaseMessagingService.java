package com.example.snosey.balto.Support.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.example.snosey.balto.Application;
import com.example.snosey.balto.R;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.example.snosey.balto.main.ChatRoom;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import static com.example.snosey.balto.Support.webservice.WebService.Notification.Types.bookingRequest;

/**
 * Created by pc on 10/30/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {


   /* @Override
    public void handleIntent(Intent intent) {
        Log.e("messageIntent", intent.getExtras().toString() + " !");
        try {
            handleDataMessage(new JSONObject(intent.getStringExtra("data")));
        } catch (Exception e) {
            e.printStackTrace();
            super.handleIntent(intent);
        }
    }*/

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        try {
            for (Iterator<String> it = remoteMessage.getData().values().iterator(); it.hasNext(); ) {
                String value = it.next();
                JSONObject object = new JSONObject(value);
                handleDataMessage(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if message contains a notification payload.

        Log.e("remoteMessage Id ", remoteMessage.getMessageId());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void handleDataMessage(JSONObject data) {

        try {
            // JSONObject data = json.getJSONObject("data");

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String formattedDate = df.format(c.getTime());

            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = formattedDate;
            final JSONObject payload = data.getJSONObject("payload");

            //String title = data.getString("title");
            //String message = data.getString("message");

            if (payload.getString("kind").equals(WebService.Notification.Types.newMsg)) {
                JSONObject jsonObject = new JSONObject(payload.getString("data"));
                String chatId = jsonObject.getString("id_chat");
                if (((Application) getApplication()).getCurrentActivity() != null) {
                    FragmentActivity activity = ((Application) getApplication()).getCurrentActivity();


                    ChatRoom myFragment = (ChatRoom) activity.getSupportFragmentManager().findFragmentByTag("ChatRoom");
                    if ((myFragment != null && myFragment.isVisible())) {

                        if (myFragment.chatId.equals(chatId)) {
                            myFragment.newMsg(jsonObject);
                            return;
                        }
                        FragmentManager fm = activity.getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", chatId);
                        ChatRoom fragment = new ChatRoom();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.addToBackStack("ChatRoom");
                        ft.replace(R.id.fragment, fragment, "ChatRoom");
                        ft.commit();
                        return;
                    }
                }
            }
            NotificationHeader notificationText = replaceHeader(payload.getString("kind"));
            // String message = data.getJSONObject("notification").getString("body");
            //String title = data.getJSONObject("notification").getString("title");

            String message = notificationText.message;
            String title = notificationText.title;


            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            //  if (!com.example.snosey.balto.Support.notification.NotificationUtils.isAppIsInBackground(getApplicationContext())) {
      /*      if (false) {
                Intent pushNotification = new Intent(com.example.snosey.balto.Support.notification.Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("data", payload.toString());
                pushNotification.putExtra("title", title);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                com.example.snosey.balto.Support.notification.NotificationUtils notificationUtils = new com.example.snosey.balto.Support.notification.NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                //    Intent resultIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                //  resultIntent.putExtra("data", payload.toString());
//                notificationUtils.showDialog(payload.toString(), title, message);
                //showNotificationMessageFireBase(getApplicationContext(), title, message, timestamp, resultIntent);
                Log.e(TAG, "isBackGround: " + "no");
            }
            // app is in foreground, broadcast the push message

            // play notification sound
            //     NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //      notificationUtils.playNotificationSound();
            else*/
            {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                resultIntent.putExtra("data", payload.toString());

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessageFireBase(getApplicationContext(), title, message, timestamp, resultIntent);
                }
                Log.e(TAG, "isBackGround: " + "yea");
            }
            //}
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    private class NotificationHeader {
        String message, title;
    }

    private NotificationHeader replaceHeader(String kind) {

        NotificationHeader text = new NotificationHeader();
        if (kind.equals(bookingRequest)) {
            text.title = getString(R.string.bookingRequestMessage);
            text.message = " ";
        } else if (kind.equals(WebService.Notification.Types.bookingRequestOnline)) {
            text.title = getString(R.string.newReservation);
            text.message = " ";
        } else if (kind.equals(WebService.Notification.Types.newMsg)) {
            text.title = getString(R.string.newMsg);
            text.message = " ";
        } else if (kind.equals(WebService.Notification.Types.video_call)) {
            text.title = getString(R.string.video_room_is_created);
            text.message = " ";
        } else if (kind.equals(WebService.Booking.bookingStateStart)) {
            text.title = getString(R.string.doctorStart);
            text.message = " ";
        } else if (kind.equals(WebService.Booking.bookingStateWorking)) {
            text.title = getString(R.string.doctorArrive);
            text.message = " ";
        } else if (kind.equals(WebService.Booking.bookingStateDone)) {
            text.title = getString(R.string.doctorFinished);
            text.message = " ";
        } else if (kind.equals(WebService.Booking.bookingStateDoctorCancel)) {
            text.title = getString(R.string.reservationCancel);
            text.message = " ";
        } else if (kind.equals(WebService.Booking.bookingStatePatientCancel)) {
            text.title = getString(R.string.reservationPatientCancel);
            text.message = " ";
        }

        return text;
    }


    /**
     * Showing notification with text only
     */

    private void showNotificationMessageFireBase(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new com.example.snosey.balto.Support.notification.NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, null);
    }

    /**
     * Showing notification with text and image
     */


}