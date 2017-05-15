package fr.utt.ung.flux;

import android.app.NotificationManager;
import android.content.Context;
import android.webkit.JavascriptInterface;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Will be exposed to js in webview to communicate with flux2-client
 */
public class JsInterface {
    private static JsInterface INSTANCE = new JsInterface();
    private Context context = null;

    private String firebaseToken;
    private String androidUid;

    private String JWT = "";
    private String apiUri = "";
    private String route = "";
    private String channel = "";
    private boolean modal = false;

    private JsInterface() {}

    public static JsInterface get()
    {
        return INSTANCE;
    }

    public static JsInterface get(Context context)
    {
        INSTANCE.context = context;
        return INSTANCE;
    }

    @JavascriptInterface
    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        if(firebaseToken != null) {
            this.firebaseToken = firebaseToken;
        }
    }

    @JavascriptInterface
    public String getAndroidUid() {
        return androidUid;
    }

    public void setAndroidUid(String androidUid) {
        this.androidUid = androidUid;
    }

    public String getJWT() {
        return JWT;
    }

    @JavascriptInterface
    public void setJWT(String JWT) {
        this.JWT = JWT;
    }

    public String getApiUri() {
        return apiUri;
    }

    @JavascriptInterface
    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public boolean hasModal() {
        return modal;
    }

    @JavascriptInterface
    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public String getRoute() {
        return route;
    }

    @JavascriptInterface
    public void setRoute(String route) {
        this.route = route;
    }

    public String getChannel() {
        return channel;
    }

    @JavascriptInterface
    public void setChannel(String channel) {
        this.channel = channel;

        // Remove notification for this channel
        if(this.context != null) {
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(channel, 0);
        }
    }
}
