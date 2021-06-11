package com.android.calendarapp;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Calendar;

public class MonthCalendarAdapter extends FragmentStateAdapter{
    private static int NUM_ITEMS = 100;
    private static int preposition = 50;
    private static int year, month, day;
    private static int num1 = 0, num2 = 0, turn =1;
    private static String fragname;
    private int Position;

    public MonthCalendarAdapter(@NonNull Fragment fragment, String fragname) {
        super(fragment);
        this.fragname = fragname; //어떤 프래그먼트가 호출했는지 구분하기 위한 String 변수
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Position = position;
        if(fragname.equals("MonthViewFragment")){ //monthviewfragment가 호출했을 때
            if(num1 == 0){ //날짜를 초기화 시키기 위해 가장 처음에 호출되었을 경우에만 들어올 수 있는 if문
                //현재 날짜로 초기화 시킴
                year = Calendar.getInstance().get(Calendar.YEAR);
                month = Calendar.getInstance().get(Calendar.MONTH);
                day = Calendar.getInstance().get(Calendar.DATE);
                num1++;
                return MonthCalendarFragment.newInstance(year, month); //MonthCalenderFragment의 newInstance에 year, month 전달
            }
            if(turn != 2) { //주간 달력에서 "월" 오버플로우 메뉴를 누른 상황이 아닐 때(그냥 월간 달력에서 swipe했을 때)
                if (preposition > position) { //이전으로 넘겼을 때
                    if (month == 0) { //현재 month값이 0(1월)일 때
                        year--; //작년
                        month = 11; //12월
                    } else{
                        month--; //지난 달로
                    }
                } else if (preposition < position) {  //다음으로 넘겼을 때
                    if (month == 11) { //현재 month값이 11(12월)일 때
                        year++; //내년
                        month = 0; //1월로
                    } else{
                        month++; //다음 달로
                    }
                }
            }
            day = 1; //day를 1로 설정
            preposition = position; //현재 position값을 preposition에 저장.(어떤 방향으로 swipe 했는지 알기 위해)
            turn = 1; //현재 월간 달력임을 저장

            return MonthCalendarFragment.newInstance(year, month); //MonthCalenderFragment의 newInstance에 year, month 전달
        }

        else{ //WeekViewFragment가 호출했을 때
            Calendar today = Calendar.getInstance();
            today.set(year, month, day); //저장된 변수들로 날짜를 설정.
            if(num2 == 0){ //날짜를 초기화 시키기 위해 가장 처음에 호출되었을 경우에만 들어올 수 있는 if문
                while(today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){ //해당 주의 일요일로 만들어주기 위해
                    today.add(Calendar.DATE,-1); //일요일이 될 때 까지 하루 전으로 이동
                }
                //일요일로 이동한 날의 날짜를 구함
                year = today.get(Calendar.YEAR);
                month = today.get(Calendar.MONTH);
                day = today.get(Calendar.DATE);
                num2++;
                turn = 2;
                preposition = position;
                return MonthCalendarFragment.newInstance(year, month, day); //MonthCalenderFragment의 newInstance에 year, month, day 전달
            }

            while (today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) { //해당 주의 일요일로 만들어주기 위해
                today.add(Calendar.DATE, -1); //일요일이 될 때까지 하루 전으로 이동
            }

            if(turn != 1) { //월간 달력에서 "주" 오버플로우 메뉴를 누른 상황이 아닐 때(그냥 주간 달력에서 swipe했을 때)
                if (preposition > position) { //이전으로 넘겼을 때
                    today.add(Calendar.DATE, -7); //일주일 전으로 이동]
                } else if (preposition < position) {  //다음으로 넘겼을 때
                    today.add(Calendar.DATE, 7); //일주일 후로 이동
                }
            }
            //이동한 날의 날짜를 구함
            year = today.get(Calendar.YEAR);
            month = today.get(Calendar.MONTH);
            day = today.get(Calendar.DATE);

            preposition = position; //현재 position값을 preposition에 저장.(어떤 방향으로 swipe 했는지 알기 위해)
            turn = 2; //현재 주간 달력임을 저장
            return MonthCalendarFragment.newInstance(year, month, day); //MonthCalenderFragment의 newInstance에 year, month, day 전달
        }
    }

    @Override
    public int getItemCount() {
        return NUM_ITEMS;
    }
}