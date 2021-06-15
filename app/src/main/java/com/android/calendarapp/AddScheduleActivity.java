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
        title.setPrivateImeOptions("defaultInputmode=korean;"); //한글자판 입력
        title.setText(year+"년 "+month+"월 "+day+"일 "+time+"시");

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

        getLastLocation(); //Activity가 실행되자 마자 이 함수가 실행 됨

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //XML레이아웃에 있는 mapFragment객체를 얻어와서 메소드 호출.

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleMap != null) {
                    Geocoder geocoder = new Geocoder(AddScheduleActivity.this, Locale.KOREA);
                    Address addr;

                    try {
                        List<Address> listAddress = geocoder.getFromLocationName(mapinput.getText().toString(), 1);
                        if (listAddress.size() > 0) { // 주소값이 존재 하면
                            addr = listAddress.get(0); // Address형태로

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
        memo.setPrivateImeOptions("defaultInputmode=korean;"); //한글자판 입력

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
                onBackPressed(); //뒤로가기
            }
        });

        Button remove = (Button)findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(AddScheduleActivity.this);
                dlg.setMessage("정말 삭제하시겠습니까?"); //삭제 버튼 눌렀을 때, 메시지 띄우기
                dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AddScheduleActivity.this,"확인을 누르셨습니다.",LENGTH_SHORT).show();
                        deleteRecord(); //Record 삭제
                        onBackPressed(); //뒤로가기
                    }
                });
                dlg.show();
            }
        });
    }

    private void viewAllToTextView() {
        TextView result = (TextView)findViewById(R.id.result);

        Cursor cursor = mDbHelper.getAllUsersBySQL();
        //Cursor 객체 통한 쿼리 결과 접근
        //쿼리 결과는 결과셋 자체가 리턴되지 않으며, 위치를 가리키는 Cursor로 리턴

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            //moveToNext() 통해 다음 레코드 위치로 이동
            //마지막 레코드이면 false 리턴
            buffer.append(cursor.getString(0)+" \t");
            //cursor의 컬럼값을 문자열로 구해서 buffer에 추가

        }
        result.setText(buffer);
        //TextView - result의 text 값으로 buffer의 내용 넣기
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
        // 1. 위치 접근에 필요한 권한 검사 및 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    AddScheduleActivity.this,            // MainActivity 액티비티의 객체 인스턴스를 나타냄
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},        // 요청할 권한 목록을 설정한 String 배열
                    REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION    // 사용자 정의 int 상수. 권한 요청 결과를 받을 때
            );
            return; //return으로 하지 말고 onRequestPermissionsResult으로 하기
        }

        // 2. Task<Location> 객체 반환
        Task task = mFusedLocationClient.getLastLocation(); //위치 정보를 반환받기 위해(마지막으로 알려진 위치!!)

        // 3. Task가 성공적으로 완료 후 호출되는 OnSuccessListener 등록
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() { //위치 정보를 획득하게 되면 불려짐
            @Override
            public void onSuccess(Location location) { //위치정보를 얻으면 onSuccess 메소드가 불려짐

                // 4. 마지막으로 알려진 위치(location 객체)를 얻음.
                if (location != null) {
                    mLastLocation = location;
                    //updateUI(); //이건 필요 없음!!
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
                        title("한성대학교")); //초기값

        // move the camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hansung, 15));
    }

}