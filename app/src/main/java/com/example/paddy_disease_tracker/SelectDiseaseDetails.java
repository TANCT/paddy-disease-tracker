package com.example.paddy_disease_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SelectDiseaseDetails extends AppCompatActivity {
    RelativeLayout hispa,brownspot,leafblast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_disease_details);
        hispa=findViewById(R.id.Hispaface);
        brownspot=findViewById(R.id.Brownspotface);
        leafblast=findViewById(R.id.Leafblastface);
        hispa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SelectDiseaseDetails.this,Hispa.class);
                startActivity(intent);
            }
        });
        brownspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SelectDiseaseDetails.this,Brownspot.class);
                startActivity(intent);
            }
        });
        leafblast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SelectDiseaseDetails.this,LeafBlast.class);
                startActivity(intent);
            }
        });
    }
}