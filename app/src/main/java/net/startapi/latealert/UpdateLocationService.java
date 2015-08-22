package net.startapi.latealert;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.List;

public class UpdateLocationService extends IntentService {

    private static final String TAG = UpdateLocationService.class.getSimpleName();
    private List<Event> mEvents;

    public UpdateLocationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        double latitude = workIntent.getExtras().getDouble(AlertApp.EXTRA_LAT);
        double longitude = workIntent.getExtras().getDouble(AlertApp.EXTRA_LONG);
        Log.d(TAG, "latlog " + latitude + ", " + longitude);
        if (null != mEvents) {
            loadEvents();
        }
        checkDistance();
    }

    private void checkDistance() {
        if(null == mEvents) return;
        for (Event event : mEvents) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }

            String location = event.getLocation();
            if (location != null) {
                location = "<unknown>";
            }
            Log.d(TAG, "Event " + event.getDescription() + " @ " + location + " on " + start.toStringRfc3339());
        }
    }

    private void loadEvents() {
       // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        try {
            Events events = AlertApp.getCalendarService().events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            mEvents = events.getItems();
        } catch (Exception e) {
            Log.e(TAG, "oops", e);
        }

    }

}