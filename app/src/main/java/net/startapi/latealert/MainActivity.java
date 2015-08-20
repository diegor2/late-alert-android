package net.startapi.latealert;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends ActionBarActivity {

    public static final String LOCALHOST = "http://localhost:8000/late-alert/";
    public static final String RESOURCE = "android.resource://%s/raw/";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
        setContentView(mWebView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = getString(R.string.url);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith(LOCALHOST)) {
                    String newUrl = url.replace(LOCALHOST, String.format(RESOURCE, getPackageName()));
                    mWebView.loadUrl(newUrl);
                    return true;
                }
                return false;
            }
        });
        mWebView.loadUrl(url);

    }
}
