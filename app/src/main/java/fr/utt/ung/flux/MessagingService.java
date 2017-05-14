package fr.utt.ung.flux;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseMessagingService.java#L45-L82
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> payload = remoteMessage.getData();

        // If it's a chat message
        if(payload.get("type").equals("message")) {

            JsInterface jsInterface = JsInterface.get();
            // TODO read jsinterface to ignore, ignored channels

            // Create notification if we are not already on this channel
            if(!MainActivity.isForeground() || jsInterface.getChannel() == null || !jsInterface.getChannel().equals(payload.get("channel"))) {

                // Build notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Nouveau message sur " + payload.get("channel").split(":")[1])
                .setContentText(payload.get("senderName") + " (" + payload.get("senderTeamName") + ") : " + payload.get("text"))
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                //.setOnlyAlertOnce(true)
                .setVibrate(new long[] { 0, 250, 100, 250, 100, 500  })
                .setLights(ContextCompat.getColor(this.getApplicationContext(), R.color.colorAccent), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                // Set action
                Intent resultIntent = new Intent(this, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("route", "chat.channel");
                resultIntent.putExtra("route.param.channel", payload.get("channel"));
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                // Dispatch notification
                int mNotificationId = 0;
                String mNotificationTag = payload.get("channel");
                NotificationManager mNotifyMgr =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationTag, mNotificationId, mBuilder.build());
            }
        }
    }

}
