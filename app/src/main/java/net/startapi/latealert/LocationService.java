package net.startapi.latealert;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocationService extends Service {

    private Binder mBinder = new Binder() {

    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
