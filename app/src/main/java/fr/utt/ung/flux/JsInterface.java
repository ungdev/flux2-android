package fr.utt.ung.flux;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;


/**
 * Will be exposed to js in webview to communicate with flux2-client
 */
public class JsInterface {

    private Context context;

    private String firebaseToken;
    private String androidUid;

    private String JWT;
    private String apiUri;
    private boolean modal = false;

    public JsInterface(Context context) {
        this.context = context;
    }

    public void initTopics(String jwt) {
        Log.d("JWT2", jwt);
        //MainActivity.JWT = jwt;
        //MainActivity.subscribeToTopics(this.context);
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
}
