package fr.utt.ungdev.flux2_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "https://bar.utt.fr/";

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

        // the webApp to load
        webView.loadUrl(URL);
    }

    private class WebViewClientImpl extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
        }
    }
}
