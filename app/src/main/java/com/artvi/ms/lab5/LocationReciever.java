package com.artvi.ms.lab5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity act = MainActivity.getAct();
        Bundle bundle = intent.getExtras();
        Location location = (Location) bundle.get(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            act.getLatitudeText().setText(String.valueOf(location.getLatitude()));
            act.getLongitudeText().setText(String.valueOf(location.getLongitude()));
            act.getAltitudeText().setText(String.valueOf(location.getAltitude()));
        }
    }
}
