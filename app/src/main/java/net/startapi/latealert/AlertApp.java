package net.startapi.latealert;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

/**
 * Created by diego on 8/20/15.
 */
public class AlertApp extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String EXTRA_LONG = "net.startapi.latealert.LONGITUDE";
    public static final String EXTRA_LAT = "net.startapi.latealert.LATITUDE";
    /**
     * A Google Calendar API service object used to access the API.
     * Note: Do not confuse this class with API library's model classes, which
     * represent specific data structures.
     */
    private static com.google.api.services.calendar.Calendar mService;

    private static GoogleAccountCredential credential;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final long MIN_DISTANCE = 1;
    private static final long MIN_TIME = 10000;
    private LocationManager mLocationManager;
    public static final String TAG = AlertApp.class.getSimpleName();

    private double mLastLat;
    private double mLastLong;

    private LocationManager mManager;
    LocationListener mListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, String.format("Location %f %f",
                    location.getLatitude(), location.getLongitude()));
            update(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged " + provider + " " + status);
            update();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled " + provider);
            update();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled " + provider);
            update();
        }

    };

    private void update(double latitude, double longitude) {
        mLastLat = latitude;
        mLastLong = longitude;
        startService(new Intent(this, UpdateLocationService.class)
                .putExtra(EXTRA_LONG, longitude)
                .putExtra(EXTRA_LAT, latitude));
    }

    private void update() {
        update(mLastLat, mLastLong);
    }

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
//        Parse.initialize(this, "imFg5O96lfxzjWsfRhpeprmYEJzggfjKsekYjR04",
//                "o4QqUiGIWA5sEkDH7hdHEF80YjroTYKwd1B2iwzD");
//        ParseInstallation.getCurrentInstallation().saveInBackground();

//        ParsePush.subscribeInBackground("", new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
//                } else {
//                    Log.e("com.parse.push", "failed to subscribe for push", e);
//                }
//            }
//        });

                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mListener);


        int hasService = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS != hasService) {
            GooglePlayServicesUtil.showErrorNotification(hasService, this);
        }

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();

        // Initialize credentials and service object.
        SharedPreferences settings = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Late Alert")
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static Calendar getCalendarService() {
        return mService;
    }

    public static GoogleAccountCredential getCredential() {
        return credential;
    }
}
