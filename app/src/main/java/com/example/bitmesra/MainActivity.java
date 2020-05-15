package com.example.bitmesra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSION = 2;
    Button getLocation;
    Button getSignal;
    Button getSave;
    TextView latitude;
    TextView longitude;
    TextView signal;
    TelephonyManager telephonyManager;
    LocationManager locationManager;
    MyPhoneStateListener mPhoneStateListener;
    AppDatabase myAppDatabase;

    private int level = -1;
    private String lat;
    private String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "bitData").allowMainThreadQueries().build();
        getLocation = findViewById(R.id.btn_getLocation);
        getSignal = findViewById(R.id.btn_getSignal);
        latitude = findViewById(R.id.tv_latitude_label);
        longitude = findViewById(R.id.tv_longitude_label);
        getSave = findViewById(R.id.btn_getSave);
        signal = findViewById(R.id.tv_signal_label);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(MainActivity.this, "Please provide the permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                        PERMISSION);
            }
        }

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationUtil();
                getLocation.setEnabled(false);
            }
        });

        getSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneStateListener = new MyPhoneStateListener();
                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                    telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
                }
            }
        });

        getSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Task user = new Task();
                if (lat == null || lon == null || level == -1) {
                    Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_SHORT).show();
                } else {
                    user.setLatitude(lat);
                    user.setLongitude(lon);
                    user.setSignal(level);

                    myAppDatabase.taskDao().insert(user);

                    Toast.makeText(MainActivity.this, "Data is saved", Toast.LENGTH_LONG).show();

                    latitude.setText("Latitude : Waiting...");
                    longitude.setText("Longitude : Waiting...");
                    signal.setText("Signal : Waiting...");

                    lat = null;
                    lon = null;
                    level = -1;
                }

            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission Not Granted!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onLocationChanged(Location location) {
        lat = String.format("%.2f", location.getLatitude());
        lon = String.format("%.2f", location.getLongitude());
        latitude.setText("Latitude : " + lat);
        longitude.setText("Longitude : " + lon);
        getLocation.setEnabled(true);
        Log.v("log1", "its working");
    }

    private void locationUtil() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            level = signalStrength.getLevel();
            signal.setText("Signal :" + level);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(MainActivity.this, " GPS Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
}
