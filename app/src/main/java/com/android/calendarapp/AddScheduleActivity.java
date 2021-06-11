package com.android.calendarapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class AddScheduleActivity extends AppCompatActivity {
    private String year, month, day, time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        Intent intent = getIntent();
        year = intent.getExtras().getString("year");
        month = intent.getExtras().getString("month");
        day = intent.getExtras().getString("day");
        time = intent.getExtras().getString("time");

        setContentView(R.layout.activity_add_schedule);
        //monthview 프레그먼트가 main의 주 화면으로 표시하기 위해
        FragmentManager fragmentManager = getSupportFragmentManager();
        //MainActivity가 AppCompatActivity를 확장하였으므로, FragmentManager 인스턴스 얻을 때 getSupportFragmentManager() 사용
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragment 추가, 삭제 또는 교체 등의 작업 수행 중 오류 발생 시, 다시 원래 상태로 되돌릴 수 있도록, FragmentTransaction 클래스 이용
        //FragmentManager의 beginTransaction() 메소드 호출 통해 FragmentTransaction의 인스턴스 얻어옴
        fragmentTransaction.add(R.id.add_schedule_container, new AddScheduleFragment());
        //FragmentTransaction의 add() 메소드 통해 동적으로 fragment 추가
        fragmentTransaction.commit();
        //fragmentTransaction의 변경사항 저장
    }
}