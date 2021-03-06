package net.startapi.latealert;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String LOCALHOST = "http://localhost:8000/late-alert/";
    public static final String RESOURCE = "android.resource://%s/raw/";
    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView mWebView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

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
        String url = getString(R.string.url_heroku);
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

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            Log.d(TAG, String.format("Location %f %f",
//                    mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
