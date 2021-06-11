package com.android.calendarapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static int num = 0;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private int mParam2;

    public MonthViewFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static MonthViewFragment newInstance(int year, int month) {
        //newInstance(year,month) 통해 년, 월 정보 받아옴
        MonthViewFragment fragment = new MonthViewFragment();
        //MonthViewFragment 객체 생성
        Bundle args = new Bundle();
        //Bundle 객체 생성
        args.putInt(ARG_PARAM1, year);
        //ARG_PARAM1에 year 값 넣어 args에 저장
        args.putInt(ARG_PARAM2, month);
        //ARG_PARAM2에 month 값 넣어 args에 저장
        fragment.setArguments(args);
        //args를 매개변수로 한 setArguments() 메소드 수행하여 fragment에 저장
        return fragment; //fragment 반환
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) { //getArguments() 메소드가 비어있지 않은 경우
            mParam1 = getArguments().getInt(ARG_PARAM1);
            //ARG_PARAM1에 저장된 정수값 가져와서 mParam1에 저장
            mParam2 = getArguments().getInt(ARG_PARAM2);
            //ARG_PARAM2에 저장된 정수값 가져와서 mParam2에 저장
        }
        else { //getArguments() 메소드가 비어있는 경우
            mParam1 = Calendar.getInstance().get(Calendar.YEAR);
            //mParam1에 현재 년도 정보 저장
            mParam2 = Calendar.getInstance().get(Calendar.MONTH);
            //mParam2에 현재 월 정보 저장 (이때 month 는 0부터 시작)
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_month_view, container, false);
        //inflate() 함수 통해 fragment_month_view 파일로부터 레이아웃 로드하여 rootview에 저장
        ViewPager2 vpPager = rootview.findViewById(R.id.vpPager);
        FragmentStateAdapter adapter = new MonthCalendarAdapter(this,"MonthViewFragment");
        //호출한 fragment를 구분하기 위해 현재 fragment이름을 넘겨줌
        vpPager.setAdapter(adapter);
        //ViewPager2는 FragmentStateAdapter의 객체 adapter 통해 각 페이지에 표시될 정보 제공받음

        vpPager.setCurrentItem(50,false);
        //ViewPager2 객체의 현재 페이지를 50페이지로 설정, 애니메이션 전환 false

        return rootview; //rootview 반환
    }
}