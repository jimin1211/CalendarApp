package com.android.calendarapp;


import android.content.Context;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DayAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private ArrayList<DayItem> mItems = new ArrayList<DayItem>();

    public DayAdapter(Context context, int resource, ArrayList<DayItem> items) {
        mContext = context;
        mItems = items;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //int Pheight = parent.getHeight();
        //int Cheight = convertView.getMeasuredHeight();


        if (convertView == null) { // 해당 항목 뷰가 이전에 생성된 적이 없는 경우
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 항목 뷰를 정의한 xml 리소스(여기서는 mResource 값)으로부터 항목 뷰 객체를 메모리로 로드
            convertView = inflater.inflate(mResource, parent,false);
        }
        ViewGroup.LayoutParams param = convertView.getLayoutParams();
        if(param == null){
            param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        param.height = 160; //53~54dp

        // convertView 변수로 참조되는 항목 뷰 객체내에 포함된 이미지뷰 객체를 id를 통해 얻어옴

        TextView grid_day = (TextView) convertView.findViewById(R.id.item_textview);
        // 어댑터가 관리하는 항목 데이터 중에서 position 위치의 항목의 문자열을 설정 텍스트뷰 객체에 설정
        grid_day.setText(mItems.get(position).day+"");

        return convertView;
    }
}