package com.example.paddy_disease_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_home,container,false);
        Button aroundme=v.findViewById(R.id.around_me_btn);
        TextView text = (TextView)v.findViewById(R.id.greeting);
        Calendar calendar = Calendar.getInstance();
        int hrs = calendar.get(Calendar.HOUR_OF_DAY);
        if(hrs>0&&hrs<12){
            text.setText("Good Morning!");
        }else if(hrs>=12&&hrs<17){
            text.setText("Good Afternoon!");
        }else if(hrs>=17&&hrs<21){
            text.setText("Good Evening!");
        }else{
            text.setText("Good Night!");
        }
        aroundme.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MapsActivity.class);
                startActivity(intent);
            }


        });

        return v;
    }

}
