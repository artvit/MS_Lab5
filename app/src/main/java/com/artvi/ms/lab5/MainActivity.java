package com.artvi.ms.lab5;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView startedText;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView altitudeText;
    private TextView elapsedText;
    private Button startButton;
    private Button stopButton;
    private RelativeLayout relativeLayout;
    private static MainActivity act;

    public TextView getLatitudeText() {
        return latitudeText;
    }

    public TextView getLongitudeText() {
        return longitudeText;
    }

    public TextView getAltitudeText() {
        return altitudeText;
    }

    public static MainActivity getAct() {
        return act;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startedText = (TextView) findViewById(R.id.startedText);
        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);
        altitudeText = (TextView) findViewById(R.id.altitudeText);
        elapsedText = (TextView) findViewById(R.id.elapsedText);
        startButton = (Button) findViewById(R.id.buttonStart);
        stopButton = (Button) findViewById(R.id.buttonStop);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction("GPS_ACTION");
        LocationReciever reciever = new LocationReciever();
        registerReceiver(reciever, filter);

        act = this;
        Intent intentp = new Intent("GPS_ACTION");
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intentp, PendingIntent.FLAG_CANCEL_CURRENT);

        final boolean[] timeStop = new boolean[1];

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, pendingIntent);

                    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.removeUpdates(pendingIntent);
                        Snackbar snackbar = Snackbar.make(relativeLayout, "Please turn on GPS", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }

                    Toast toast = Toast.makeText(getApplicationContext(), "Pending...", Toast.LENGTH_LONG);
                    toast.show();
                }
                catch(SecurityException ex) {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Check permissions error", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                final Handler handler = new Handler();

                Thread timeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        timeStop[0] = true;
                        final Calendar calendar = Calendar.getInstance();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startedText.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) +
                                        ":" + calendar.get(Calendar.SECOND));
                            }
                        });
                        long startTime = SystemClock.currentThreadTimeMillis();

                        while(timeStop[0]) {
                            final long time = SystemClock.currentThreadTimeMillis() - startTime;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    elapsedText.setText(String.valueOf(time));
                                }
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                timeThread.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStop[0] = false;
                try {
                    locationManager.removeUpdates(pendingIntent);
                }
                catch(SecurityException ex) {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Check permissions error", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
