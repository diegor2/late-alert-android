package net.startapi.latealert;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by diego on 8/20/15.
 */
public class AlertApp extends Application {

    private static final long MIN_DISTANCE = 1;
    private static final long MIN_TIME = 10000;
    private LocationManager mLocationManager;

    private static final String TAG = AlertApp.class.getSimpleName();
    private LocationManager mManager;

    LocationListener mListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, String.format("Location %f %f",
                    location.getLatitude(), location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged " + provider + " " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled " + provider);
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "imFg5O96lfxzjWsfRhpeprmYEJzggfjKsekYjR04",
                "o4QqUiGIWA5sEkDH7hdHEF80YjroTYKwd1B2iwzD");
        ParseInstallation.getCurrentInstallation().saveInBackground();

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

        Intent i = new Intent(this, UpdateLocationService.class);
        PendingIntent service = PendingIntent.getService(this, 0, i, 0);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mListener);
    }

}
