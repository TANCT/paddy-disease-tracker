package com.example.paddy_disease_tracker;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    //TextView text = findViewById(R.id.greeting);
    //Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //int hrs = calendar.get(Calendar.HOUR_OF_DAY);
        //setGreeting(hrs);
       BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation_view);


        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();


        }

        private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                switch(item.getItemId()){
                    case R.id.itm_home:
                        selectedFragment=new HomeFragment();
                        break;
                    case R.id.itm_dashboard:
                        selectedFragment=new DashBoardFragment();
                        break;
                    case R.id.itm_detector:
                        selectedFragment=new DetectDiseaseFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                return true;
            }
        };
    /*public void setGreeting(int hrs){
        if(hrs>0&&hrs<12){
            text.setText("Good Morning!");
        }else if(hrs>=12&&hrs<17){
            text.setText("Good Afternoon!");
        }else if(hrs>=17&&hrs<21){
            text.setText("Good Evening!");
        }else{
            text.setText("Good Night!");
        }
    }*/

    }
