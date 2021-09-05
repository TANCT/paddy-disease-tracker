package com.example.paddy_disease_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<MainModel> mainModal;
    MainAdapter mainAdapter;
    MainAdapter.RecyclerViewClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_home,container,false);
        RelativeLayout aroundme=v.findViewById(R.id.aroundMe);
        TextView text = (TextView)v.findViewById(R.id.greeting);
        Calendar calendar = Calendar.getInstance();
        recyclerView=v.findViewById(R.id.recycler_view);
        Integer []diseaseLogo={R.drawable.brownspot,R.drawable.hispa,R.drawable.leafblast};
        String[] diseaseName={"BrownSpot","Hispa","LeafBlast"};

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

        mainModal=new ArrayList<>();
        for(int i=0; i<diseaseLogo.length; i++){
            MainModel model=new MainModel(diseaseLogo[i],diseaseName[i]);
            mainModal.add(model);
        }
        setOnClickListener();
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mainAdapter=new MainAdapter(getActivity(),mainModal,listener);
        recyclerView.setAdapter(mainAdapter);
        return v;
    }

    private void setOnClickListener() {
        listener=new MainAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent=null;
                switch(position){
                    case 0:
                        intent=new Intent(getActivity(),Brownspot.class);
                        break;
                    case 1:
                        intent=new Intent(getActivity(),Hispa.class);
                        break;
                    case 2:
                        intent=new Intent(getActivity(),LeafBlast.class);
                        break;
                }
                startActivity(intent);

            }
        };
    }

}
