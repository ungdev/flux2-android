package fr.utt.ung.flux;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;

import static fr.utt.ung.flux.R.id.myWebView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String APP_URI = BuildConfig.DEBUG ? "https://flux-dev.uttnetgroup.fr/" : "https://bar.utt.fr/";
    //public static final String APP_URI = "http://192.168.1.2:8080/";

    private WebView webview;
    private LinearLayout loadingLayout;
    private static boolean foreground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define the app page
        setContentView(R.layout.activity_main);

        // get the WebView element inside the app page
        webview = (WebView) findViewById(myWebView);

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
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        loadingLayout.setVisibility(LinearLayout.VISIBLE);
        webview.setVisibility(WebView.GONE);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress == 100 && loadingLayout.getVisibility() != LinearLayout.GONE) {
                    loadingLayout.setVisibility(LinearLayout.GONE);
                    webview.setVisibility(WebView.VISIBLE);
                    handleRouteIntent(getIntent());
                }
            }
        });

        // add js interface
        JsInterface jsInterface = JsInterface.get(getApplicationContext());
        jsInterface.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());
        jsInterface.setAndroidUid(Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID));
        webview.addJavascriptInterface(jsInterface, "Android");

        // Log firebase token
        if(FirebaseInstanceId.getInstance().getToken() != null) {
            Log.d(TAG, "Firebase token: " + FirebaseInstanceId.getInstance().getToken());
        }
        else {
            Log.d(TAG, "Not Firebase token yet");
        }
        Log.d(TAG, "Android UID: " + Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID));


        // Load the webapp if necessary
        if (savedInstanceState == null)
        {
            webview.loadUrl(APP_URI);
        }
    }

    /**
     * If the intent contains route information, the webview will be redirect
     */
    private void handleRouteIntent(Intent intent) {
        if(intent.hasExtra("route")) {
            // Generate route params
            String param = "{";
            if(intent.hasExtra("route.param.channel")) {
                param += "channel: '" + intent.getStringExtra("route.param.channel") + "',";
            }
            param += "}";

            // Navigate to new route
            webview.loadUrl("javascript:Android.navigate('" + intent.getStringExtra("route") + "', " + param + ")");
            Log.i(TAG, "Route intent received: Android.navigate('" + intent.getStringExtra("route") + "', " + param + ")");
        }
    }

    /**
     * Called when app is started from a notification or a launcher
     */
    @Override
    public void onNewIntent(Intent intent) {
        this.handleRouteIntent(intent);
        super.onNewIntent(intent);
    }


    /**
     * Transmit back to webview
     */
    @Override
    public void onBackPressed() {
        JsInterface jsInterface = JsInterface.get();
        if(jsInterface.hasModal()) {
            webview.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ESCAPE));
            jsInterface.setModal(false);
        }
        else if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        MainActivity.foreground = true;
    }

    @Override
    public void onPause(){
        super.onPause();
        MainActivity.foreground = false;
    }

    public static boolean isForeground() {
        return foreground;
    }
}
