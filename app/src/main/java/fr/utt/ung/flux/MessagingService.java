package fr.utt.ung.flux;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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
        JsInterface jsInterface = JsInterface.get();

        // If it's a chat message
        if (payload.get("type").equals("message")) {
            // TODO read jsinterface to ignore, ignored channels

            // Create notification if we are not already on this channel
            if (!MainActivity.isForeground() || jsInterface.getChannel() == null || !jsInterface.getChannel().equals(payload.get("channel"))) {

                // Build notification
                NotificationCompat.Builder builder = this.createNotificationBuilder()
                        .setContentTitle("Nouveau message sur " + payload.get("channel").split(":")[1])
                        .setContentText(payload.get("senderName") + " (" + payload.get("senderTeamName") + ") : " + payload.get("text"));

                // Set action
                Intent resultIntent = new Intent(this, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("route", "chat.channel");
                resultIntent.putExtra("route.param.channel", payload.get("channel"));
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);

                // Dispatch notification
                int mNotificationId = 0;
                String mNotificationTag = payload.get("channel");
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationTag, mNotificationId, builder.build());
            }
        } else if (payload.get("type").equals("alert")) {
            // TODO read jsinterface to know if we trigger notif

            // Create notification if we are not already on the alert route
            if (!MainActivity.isForeground() || jsInterface.getRoute() == null || !jsInterface.getRoute().equals("alert")) {
                int mNotificationId = 1;
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                // If alert done, then cancel
                if(payload.get("severity").equals("done")) {
                    Log.d(TAG, "try to cancel : " + payload.get("id") + ":" + payload.get("severity"));
                    mNotifyMgr.cancel("alert:"+payload.get("id"), mNotificationId);
                }
                else {
                    Log.d(TAG, "new alert: " + payload.get("id") + ":" + payload.get("severity"));
                    // Build notification
                    String location = "";
                    if(payload.get("senderLocation") != null && !payload.get("senderLocation").equals("")) {
                        location += " (" + payload.get("senderLocation") + ")";
                    }
                    String emoji = payload.get("severity").equals("serious") ? "\uD83D\uDEA8" : "\u26A0";
                    NotificationCompat.Builder builder = this.createNotificationBuilder()
                            .setOnlyAlertOnce(false)
                            .setContentTitle(emoji + " Nouvelle alerte : " + payload.get("senderName") + location)
                            .setContentText(payload.get("title"));

                    // Set action
                    Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    resultIntent.putExtra("route", "alert");
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(resultPendingIntent);

                    // Dispatch notification
                    mNotifyMgr.notify("alert:"+payload.get("id"), mNotificationId, builder.build());
                }
            }
        }
    }

    private NotificationCompat.Builder createNotificationBuilder() {

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.notification)
        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
        .setOnlyAlertOnce(true)
        .setVibrate(new long[]{0, 250, 100, 250, 100, 500})
        .setLights(ContextCompat.getColor(this.getApplicationContext(), R.color.colorAccent), 3000, 3000)
        .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification));
        //.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        return builder;
    }
}
