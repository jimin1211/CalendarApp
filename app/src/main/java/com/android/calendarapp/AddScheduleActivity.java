package com.android.calendarapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class AddScheduleActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String year, month, day, time;
    private FusedLocationProviderClient mFusedLocationClient;
    GoogleMap mGoogleMap = null;
    final private String TAG = "LocationService";

    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        Intent intent = getIntent();
        year = intent.getExtras().getString("year");
        month = intent.getExtras().getString("month");
        day = intent.getExtras().getString("day");
        time = intent.getExtras().getString("time");

        mDbHelper = new DBHelper(this);


        EditText title = (EditText)findViewById(R.id.title);
        title.setPrivateImeOptions("defaultInputmode=korean;"); //???????????? ??????
        title.setText(year+"??? "+month+"??? "+day+"??? "+time+"???");

        TimePicker sTimePicker = (TimePicker)findViewById(R.id.start_time_picker);
        sTimePicker.setCurrentHour(Integer.parseInt(time));
        sTimePicker.setCurrentMinute(0);

        TimePicker eTimePicker = (TimePicker)findViewById(R.id.end_time_picker);
        eTimePicker.setCurrentHour(Integer.parseInt(time)+1);
        eTimePicker.setCurrentMinute(0);

        EditText mapinput = (EditText)findViewById(R.id.mapinput);
        mapinput.setPrivateImeOptions("defaultInputmode=korean;");
        Button search = (Button)findViewById(R.id.search);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation(); //Activity??? ???????????? ?????? ??? ????????? ?????? ???

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //XML??????????????? ?????? mapFragment????????? ???????????? ????????? ??????.

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleMap != null) {
                    Geocoder geocoder = new Geocoder(AddScheduleActivity.this, Locale.KOREA);
                    Address addr;

                    try {
                        List<Address> listAddress = geocoder.getFromLocationName(mapinput.getText().toString(), 1);
                        if (listAddress.size() > 0) { // ???????????? ?????? ??????
                            addr = listAddress.get(0); // Address?????????

                            LatLng location = new LatLng(addr.getLatitude(), addr.getLongitude());

                            mGoogleMap.addMarker(
                                    new MarkerOptions().
                                            position(location).
                                            title(mapinput.getText().toString()));

                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        EditText memo = (EditText)findViewById(R.id.memo);
        memo.setPrivateImeOptions("defaultInputmode=korean;"); //???????????? ??????

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertRecord();
                viewAllToTextView();
            }
        });

        Button cancle = (Button)findViewById(R.id.cancel);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); //????????????
            }
        });

        Button remove = (Button)findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(AddScheduleActivity.this);
                dlg.setMessage("?????? ?????????????????????????"); //?????? ?????? ????????? ???, ????????? ?????????
                dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AddScheduleActivity.this,"????????? ??????????????????.",LENGTH_SHORT).show();
                        deleteRecord(); //Record ??????
                        onBackPressed(); //????????????
                    }
                });
                dlg.show();
            }
        });
    }

    private void viewAllToTextView() {
        TextView result = (TextView)findViewById(R.id.result);

        Cursor cursor = mDbHelper.getAllUsersBySQL();
        //Cursor ?????? ?????? ?????? ?????? ??????
        //?????? ????????? ????????? ????????? ???????????? ?????????, ????????? ???????????? Cursor??? ??????

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            //moveToNext() ?????? ?????? ????????? ????????? ??????
            //????????? ??????????????? false ??????
            buffer.append(cursor.getString(0)+" \t");
            //cursor??? ???????????? ???????????? ????????? buffer??? ??????

        }
        result.setText(buffer);
        //TextView - result??? text ????????? buffer??? ?????? ??????
    }

    private void insertRecord() {
        EditText title = (EditText)findViewById(R.id.title);
        mDbHelper.insertUserBySQL(title.getText().toString());
    }

    private void deleteRecord() {
        EditText title = (EditText)findViewById(R.id.title);
        mDbHelper.deleteUserBySQL(title.getText().toString());
    }


    final int REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION = 0;
    Location mLastLocation;

    private void getLastLocation() {
        // 1. ?????? ????????? ????????? ?????? ?????? ??? ??????
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    AddScheduleActivity.this,            // MainActivity ??????????????? ?????? ??????????????? ?????????
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},        // ????????? ?????? ????????? ????????? String ??????
                    REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION    // ????????? ?????? int ??????. ?????? ?????? ????????? ?????? ???
            );
            return; //return?????? ?????? ?????? onRequestPermissionsResult?????? ??????
        }

        // 2. Task<Location> ?????? ??????
        Task task = mFusedLocationClient.getLastLocation(); //?????? ????????? ???????????? ??????(??????????????? ????????? ??????!!)

        // 3. Task??? ??????????????? ?????? ??? ???????????? OnSuccessListener ??????
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() { //?????? ????????? ???????????? ?????? ?????????
            @Override
            public void onSuccess(Location location) { //??????????????? ????????? onSuccess ???????????? ?????????

                // 4. ??????????????? ????????? ??????(location ??????)??? ??????.
                if (location != null) {
                    mLastLocation = location;
                    //updateUI(); //?????? ?????? ??????!!
                } else
                    Toast.makeText(getApplicationContext(),
                            "No location detected",
                            Toast.LENGTH_SHORT)
                            .show();
            }
        });
    }

    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng hansung = new LatLng(37.5817891, 127.009854);
        mGoogleMap.addMarker(
                new MarkerOptions().
                        position(hansung).
                        title("???????????????")); //?????????

        // move the camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hansung, 15));
    }

}