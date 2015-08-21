package net.startapi.latealert;

import android.app.Application;
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
    }

}
