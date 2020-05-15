package com.example.bitmesra;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SignalStrength;


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
    MyPhoneStateListener mPhoneStatelistener;
    AppDatabase myAppDatabase;

    private int level=-1;
    private String  lat=null;
    private String lon=null;


    class MyPhoneStateListener extends PhoneStateListener {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
             level=signalStrength.getLevel();
            signal.setText("Signal :"+level);
        }
    }


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

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(MainActivity.this, "Please provide the permission", Toast.LENGTH_SHORT).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                        PERMISSION);


            }
        }

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationUtil();
            }
        });

        getSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoneStatelistener = new MyPhoneStateListener();
                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                telephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_NONE);
            }
        });

        getSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task user = new Task();
                if(lat==null||lon==null||level==-1)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Details",Toast.LENGTH_SHORT).show();
                }
                else {
                    user.setLatitude(lat);
                    user.setLongitude(lon);
                    user.setSignal(level);


                    myAppDatabase.taskDao()
                            .insert(user);
                    Toast.makeText(MainActivity.this, "Data is saved", Toast.LENGTH_SHORT).show();
                    latitude.setText("Latitude");
                    longitude.setText("Longitude");
                    signal.setText("Signal");
                    lat = null;
                    lon = null;
                    level = -1;
                }

            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
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


    @Override
    public void onLocationChanged(Location location) {
        lat = String.format("%.2f", location.getLatitude());
        lon = String.format("%.2f",location.getLongitude());
        latitude.setText("Latitude : " +lat );
        longitude.setText("Longitude : " +lon );
        Log.v("log1","its working");

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
