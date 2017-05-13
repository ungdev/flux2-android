package fr.utt.ungdev.flux2_android;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String APP_URI = "https://flux-dev.uttnetgroup.fr/";
    public static final String API_URI = "https://api.flux-dev.uttnetgroup.fr/";
    public static String JWT = "";

    private class CustomJSInterface {

        private Context context;

        private CustomJSInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void initTopics(String jwt) {
            Log.d("JWT2", jwt);
            MainActivity.JWT = jwt;
            MainActivity.subscribeToTopics(this.context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define the app page
        setContentView(R.layout.activity_main);

        // get the WebView element inside the app page
        WebView webView = (WebView) findViewById(R.id.myWebView);

        // SET WEBVIEW SETTINGS
        WebSettings webSettings = webView.getSettings();
        // enable javascript
        webSettings.setJavaScriptEnabled(true);
        // allow chrome debugging (console -> more tools -> remote devices)
        WebView.setWebContentsDebuggingEnabled(true);
        // allow localStorage operations
        webSettings.setDomStorageEnabled(true);

        // get the instance token
        FirebaseInstanceId.getInstance().getToken();
        Log.d("INSTANCE TOKEN", FirebaseInstanceId.getInstance().getToken());

        // use our WebViewClient
        WebViewClientImpl webViewClient = new WebViewClientImpl();
        webView.setWebViewClient(webViewClient);

        // add interface
        webView.addJavascriptInterface(new CustomJSInterface(this), "androidInterface");

        // the webApp to load
        webView.loadUrl(APP_URI + "?firebase=" + FirebaseInstanceId.getInstance().getToken());
    }

    /**
     * Register a listener on each allowed topic for the authenticated user.
     * @param context the Activity context
     */
    public static void subscribeToTopics(Context context) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String getChannelsEndpoint = MainActivity.API_URI + "message/channels";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getChannelsEndpoint,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    Log.d("volley success", "Response is: "+ response);
                    String[] channels = response.replace("[", "").replace("\"", "").split(",");
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

    }

    private class WebViewClientImpl extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
        }
    }
}
