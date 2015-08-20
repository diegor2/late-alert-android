package net.startapi.latealert;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

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

        // TODO move to Application
        Parse.initialize(this, "imFg5O96lfxzjWsfRhpeprmYEJzggfjKsekYjR04",
                "o4QqUiGIWA5sEkDH7hdHEF80YjroTYKwd1B2iwzD");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        int hasService = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS != hasService) {
            GooglePlayServicesUtil.showErrorNotification(hasService, this);
        }

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });


        Intent intent = new Intent(this, UpdateLocationService.class);
        startService(intent);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, String.format("Location %f %f",
                    mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
