package com.android.calendarapp;

import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthCalendarFragment extends Fragment {
//여기서 달력 구현 (JAVA 파일로 구현

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    static Calendar sDay = Calendar.getInstance(); //시작일;
    static int START_DAY_OF_WEEK; //시작일의 요일(1일의 요일)을 알아냄
    static int END_DAY; //한 달의 마지막 날짜 알아냄
    static int Year;
    static int Month;
    static int Day;
    static int Time;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private int mParam2;
    private int mParam3;


    public MonthCalendarFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    /**파라미터가 2개 -> 월간달력**/
    public static MonthCalendarFragment newInstance(int year, int month) {
        MonthCalendarFragment fragment = new MonthCalendarFragment();
        //MonthCalendarFragment 객체 생성
        Bundle args = new Bundle();
        //Bundle 객체 생성
        args.putInt(ARG_PARAM1, year);
        //ARG_PARAM1에 year 값 넣어 args에 저장
        args.putInt(ARG_PARAM2, month);
        //ARG_PARAM2에 month 값 넣어 args에 저장
        args.putInt(ARG_PARAM3, -1);
        //mParam1에 해당하는 값을 -1로 만듦(월간 달력과 주간 달력을 구분하기 위해 전달되지 않은 day 값을 -1로 설정)
        fragment.setArguments(args);
        //args를 매개변수로 한 setArguments() 메소드 수행하여 fragment에 저장
        return fragment; //fragment 반환
    }

    /**파라미터 3개 -> 주간달력**/
    public static MonthCalendarFragment newInstance(int year, int month, int day) {
        MonthCalendarFragment fragment = new MonthCalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, year);
        //ARG_PARAM1에 year 값 넣어 args에 저장
        args.putInt(ARG_PARAM2, month);
        //ARG_PARAM2에 month 값 넣어 args에 저장
        args.putInt(ARG_PARAM3, day);
        //ARG_PARAM3에 day 값 넣어 args에 저장
        fragment.setArguments(args);
        return fragment;
    }

    /**주간 달력 배열 생성**/
    //public String[] getDay(int year, int month, int day) {
    public ArrayList<DayItem> getDay(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        today.set(year, month, day); //파라미터로 받아온 year, month, day값 이용하여 날짜 설정
        int x = 0, y = 1;
        END_DAY = today.getActualMaximum(Calendar.DATE); //해당 월의 마지막 날짜를 알아냄

        ArrayList<DayItem> date = new ArrayList<DayItem>(); //ArrayList로

        for (int i = day; i <= END_DAY; i++) { //파라미터로 받은 day부터 마지막 날짜까지 도는 반복문 생성
            if(x<7){
                x++; //ArrayList에 7개 이하까지만 저장되어야 함.
                date.add(new DayItem(year+"", month+"", i+"")); //date 배열에 날짜(i) 저장
            }
            else
                break;
        }
        if (x < 7) { //나머지 배열에 저장
            month++; //month먼저 증가
            for (; x < 7; x++) {
                if(month == 12){ //증가된 month가 13월 일 경우
                    year++; month = 0; //다음 년도로 바꾸고 month를 1월로
                    date.add(new DayItem(year+"", month+"", y+""));
                    y++;
                }
                date.add(new DayItem(year+"", month+"", y+""));
                y++;
            }
        }
        return date;
    }

    /**월간 달력 배열 생성**/
    public static /*String[]*/ArrayList<DayItem> getItem(int year, int month){
        //String[] date = new String[6*7]; //6*7사이즈의 String형 배열을 선언
        ArrayList<DayItem> date = new ArrayList<DayItem>(); //ArrayList로

        sDay.set(year,month,1); //sDay를 현재 년도,월의 1일로 설정
        START_DAY_OF_WEEK = sDay.get(Calendar.DAY_OF_WEEK); //시작일의 요일(1일의 요일)을 알아냄
        END_DAY = sDay.getActualMaximum(Calendar.DATE); //현재 월의 마지막 날짜를 알아냄

        //배열에 요일 입력
        for(int i=0, n=1; i<42; i++){
            if(i < START_DAY_OF_WEEK-1)  //date배열에 첫번째 요일 전까지 공백으로 채움
                //date[i] = "";            //DAY_OF_WEEK는 일요일:1 ~ 토요일:7로 결과가 나옴 -> START_DAT_OF_WEEK에서 -1을 해준 값 전까지가 공백
                date.add(new DayItem(year+"", month+"", ""));

            else if((i >= START_DAY_OF_WEEK-1) && (n <= END_DAY)) {//START_DAY_OF_WEEK에서 -1해준 값부터 배열 시작
                //date[i] = n+"";                                    //n값은 배열에 날짜를 입력하기위한 변수이고, 1일부터 END_DAY 즉, 마지막 날짜까지 배열에 입력
                date.add(new DayItem(year+"", month+"", n+""));
                n++;
            }

            else                    //이 전까지의 배열은 (START_DAY_OF_WEEK-1)+(END_DAY-1)까지 채워져있으니까
                //date[i] = "";       //START_DAY_OF_WEEK+END_DAY-1부터 배열의 마지막까지 공백으로 채워줌.
                date.add(new DayItem(year+"", month+"", ""));
        }
        return date;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { //getArguments() 메소드가 비어있지 않은 경우
            mParam1 = getArguments().getInt(ARG_PARAM1);
            //ARG_PARAM1에 저장된 정수값 가져와서 mParam1에 저장
            mParam2 = getArguments().getInt(ARG_PARAM2);
            //ARG_PARAM2에 저장된 정수값 가져와서 mParam2에 저장
            mParam3 = getArguments().getInt(ARG_PARAM3);
            //ARG_PARAM3에 저장된 정수값 가져와서 mParam3에 저장
        }
        else{ //getArguments() 메소드가 비어있는 경우
            mParam1 = Calendar.getInstance().get(Calendar.YEAR);
            //mParam1에 현재 년도 정보 저장
            mParam2 = Calendar.getInstance().get(Calendar.MONTH);
            //mParam1에 현재 년도 정보 저장 (이때 month 는 0부터 시작)
            mParam3 = -1;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_month_calendar, container, false);
        //inflate() 함수 통해 fragment_month_calendar 파일로부터 레이아웃 로드하여 rootview에 저장


        if(mParam3 == -1){ //mParam3이 -1이면 월간달력 생성
            //String[] date = getItem(mParam1, mParam2); //mParam1년 mParam2월의 월간달력 배열 얻어옴
            ArrayList<DayItem> date = new ArrayList<DayItem>();
            date = getItem(mParam1, mParam2);
            DayAdapter mGridViewAdapter = new DayAdapter(getActivity(), R.layout.fragment_week_calendar, date);

            GridView gridView = (GridView) rootview.findViewById(R.id.gridview);
            gridView.setAdapter(mGridViewAdapter);

            /*//girdview id를 가진 화면 레이아웃에 정의된 GridVies 객체 로딩
            GridView gridView = (GridView) rootview.findViewById(R.id.gridview);
            //어댑터 준비 (date 배열 객체 이용, simple_list_item_1 리소스 사용)
            ArrayAdapter<String> mGridViewAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    date);
            //어댑터를 GridView 객체에 연결
            gridView.setAdapter(mGridViewAdapter);*/


            //선택된 날짜 정보를 토스트 메세지로 표시
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //if(!((((DayItem)parent.getAdapter().getItem(position)).month).equals(""))) { //date[position]값이 공백이 아닐 경우만 toast 메세지 출력
                    if(!((((DayItem)parent.getAdapter().getItem(position)).day).equals(""))) { //date[position]값이 공백이 아닐 경우만 toast 메세지 출력
                        Toast.makeText((AppCompatActivity) getActivity(),
                                /*mParam1 + "년" + (mParam2 + 1) + "월" + ((GridView)parent).getItemAtPosition(position) + "일"*/
                                ((DayItem)mGridViewAdapter.getItem(position)).year + "년" + (Integer.parseInt(((DayItem)mGridViewAdapter.getItem(position)).month) + 1) + "월" + ((DayItem)mGridViewAdapter.getItem(position)).day + "일"
                                , Toast.LENGTH_SHORT).show();
                        //일의 정보는 position정보를 통해 text를 가져옴
                        ((GridView)parent).setSelector(new PaintDrawable(Color.CYAN)); //배경색을 CYAN으로 변경
                        Year = Integer.parseInt(((DayItem)mGridViewAdapter.getItem(position)).year);
                        Month = Integer.parseInt(((DayItem)mGridViewAdapter.getItem(position)).month) + 1;
                        Day =  Integer.parseInt(((DayItem)mGridViewAdapter.getItem(position)).day);
                        Time = 13;
                        /***아래로 고치기**/
                        //Time = Calendar.getInstance().HOUR_OF_DAY;
                    }
                }
            });
            //getActivity().startActivity(intent);

        }

        else{ //주간 달력 생성
            ArrayList<DayItem> day = new ArrayList<DayItem>();
            day = getDay(mParam1, mParam2, mParam3);
            DayAdapter wGridViewAdapter = new DayAdapter(getActivity(), R.layout.fragment_week_calendar , day);

            GridView wGridview = (GridView) rootview.findViewById(R.id.gridview);
            wGridview.setAdapter(wGridViewAdapter);

            GridView gridview_week = (GridView) rootview.findViewById(R.id.gridview_week);
            String blink[] = new String[24*7];
            for(int i=0; i<24*7; i++){
                blink[i] = i+"";
            }
            ArrayAdapter<String> GridViewAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    blink);
            //어댑터를 GridView 객체에 연결
            gridview_week.setAdapter(GridViewAdapter);


            gridview_week.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    /**Toast.makeText((AppCompatActivity) getActivity(),
                     ((DayItem)wGridViewAdapter.getItem(position%7)).year + "년" + (((DayItem)wGridViewAdapter.getItem(position%7)).month + 1) + "월" + ((DayItem)wGridViewAdapter.getItem(position%7)).day + "일",
                     Toast.LENGTH_SHORT).show();**/
                    Toast.makeText((AppCompatActivity) getActivity(),
                            "Position = "+(position%7),
                            Toast.LENGTH_SHORT).show();
                    //일의 정보는 position정보를 통해 text를 가져옴
                    for(int i=0; i<7; i++){  //background color 초기화
                        wGridview.getChildAt(i).setBackgroundColor(Color.WHITE);
                    }
                    ((GridView)parent).setSelector(new PaintDrawable(Color.CYAN)); //배경색을 CYAN으로 변경
                    //((GridView)parent).getChildAt(position).setBackgroundColor(Color.CYAN); //getChildAt때문에 오류남

                    wGridview.getChildAt(position%7).setBackgroundColor(Color.CYAN); //날짜 background 색상 변경
                    Year = Integer.parseInt(((DayItem)wGridViewAdapter.getItem(position%7)).year);
                    Month = Integer.parseInt(((DayItem)wGridViewAdapter.getItem(position%7)).month) + 1;
                    Day = Integer.parseInt(((DayItem)wGridViewAdapter.getItem(position%7)).day);
                    Time = (position+7)/7-1;
                }
            });

            /*wGridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if(!(parent.getAdapter().getItem(position).equals(""))) { //date[position]값이 공백이 아닐 경우만 toast 메세지 출력
                        Toast.makeText((AppCompatActivity) getActivity(),
                                ((DayItem)wGridViewAdapter.getItem(position)).year + "년" + (((DayItem)wGridViewAdapter.getItem(position)).month + 1) + "월" + ((DayItem)wGridViewAdapter.getItem(position)).day + "일",
                                Toast.LENGTH_SHORT).show();
                        //일의 정보는 position정보를 통해 text를 가져옴
                        ((GridView)parent).setSelector(new PaintDrawable(Color.CYAN)); //배경색을 CYAN으로 변경
                    }
                }
            });*/

            String time[] = new String[24];
            for(int i = 0; i <= 23; i++) {
                time[i] = i+"";
            }
            /*//girdview id를 가진 화면 레이아웃에 정의된 GridVies 객체 로딩
            GridView tmgrid = (GridView) rootview.findViewById(R.id.timegrid);
            //어댑터 준비 (date 배열 객체 이용, simple_list_item_1 리소스 사용)
            ArrayAdapter<String> tmgridAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    time);
            //어댑터를 GridView 객체에 연결
            tmgrid.setAdapter(tmgridAdapter);*/


            LinearLayout layout = (LinearLayout)rootview.findViewById(R.id.LinearLayout);

            // layout param 생성
            for(int i = 0; i <= 23; i++) {
                TextView tv = new TextView(getActivity());  // 새로 추가할 textView 생성
                //ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.VerticalScrollableTextView);
                //tv = new TextView(ctw);
                tv.setText("\n"+i+"\n");  // textView에 내용 추가

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT ,1);

                tv.setLayoutParams(layoutParams);  // textView layout 설정

                layout.addView(tv); // 기존 linearLayout에 textView 추가
            }
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mParam1+"년"+(mParam2+1)+"월");
        //앱바에 현재 표시된 달력의 연월을 표시


        return rootview; //rootview 반환
    }
}