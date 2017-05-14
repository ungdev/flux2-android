package fr.utt.ung.flux;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //public static final String APP_URI = "https://flux-dev.uttnetgroup.fr/";
    public static final String APP_URI = "http://192.168.1.2:8080/";

    private WebView webview;
    private JsInterface jsInterface;
    private LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define the app page
        setContentView(R.layout.activity_main);

        // get the WebView element inside the app page
        webview = (WebView) findViewById(R.id.myWebView);

        // SET WEBVIEW SETTINGS
        WebSettings webSettings = this.webview.getSettings();
        // enable javascript
        webSettings.setJavaScriptEnabled(true);
        // allow chrome debugging (console -> more tools -> remote devices)
        WebView.setWebContentsDebuggingEnabled(true);
        // allow localStorage operations
        webSettings.setDomStorageEnabled(true);

        // use our WebViewClient
        loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress == 100) {
                    loadingLayout.setVisibility(LinearLayout.GONE);
                }
            }
        });

        // add js interface
        jsInterface = new JsInterface(getApplicationContext());
        jsInterface.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());
        jsInterface.setAndroidUid(Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID));
        webview.addJavascriptInterface(jsInterface, "Android");

        // Load the webapp if necessary
        if (savedInstanceState == null)
        {
            webview.loadUrl(APP_URI);
        }
    }

    /**
     * Register a listener on each allowed topic for the authenticated user.
     * @param context the Activity context
     */
/*    public static void subscribeToTopics(Context context) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String getChannelsEndpoint = MainActivity.API_URI + "message/channels";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getChannelsEndpoint,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("volley success", "Response is: "+ response);
                    String[] channels = response.replace("[", "").replace("\"", "").replace(":", "_").split(",");

                    // subscribe to each channel
                    for (String channel:channels) {
                        byte[] data;
                        try {
                            data = channel.getBytes("UTF-8");
                            String encodedChannel = String.format("%x", new BigInteger(1, data));
                            FirebaseMessaging.getInstance().subscribeToTopic(encodedChannel);
                            Log.d("SUBSCRIBED", channel + " - " + encodedChannel);
                        } catch (Exception e) {
                            Log.e("SUBSCRIBE", "Failed to subscribe to " + channel);
                            Log.e("SUBSCRIBE", e.toString());
                        }
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("volley error", "That didn't work!");
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("JWT", MainActivity.JWT);
                params.put("Authorization", "Bearer " + MainActivity.JWT);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }*/

    /**
     * Don't kill web view on 'back', go back in webview or hide
     */
    @Override
    public void onBackPressed() {
        if(jsInterface.hasModal()) {
            webview.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE));
        }
        else if (webview.canGoBack()) {
            webview.goBack();
        } else {
            moveTaskToBack(true);
        }
    }
}
