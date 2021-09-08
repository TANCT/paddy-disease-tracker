package com.example.paddy_disease_tracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportActivity extends AppCompatActivity {
    Connection connect;
    String ConnectionResult="";
    TextView geolocation;
    Button submit;
    EditText uid,pwd;
    AutoCompleteTextView region,disease;
    ImageView region_dd,disease_dd;
    TextView tests;
    double lat,lon;
    private static final String [] regions={"A","B","C","D"};
    private static final String [] diseases={"BrownSpot","Hispa","LeafBlast"};


    String[] perms = {"android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE"
    };
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        uid=(EditText) findViewById(R.id.staffid);
        pwd=(EditText) findViewById(R.id.pwd);
        region=(AutoCompleteTextView)findViewById(R.id.region);
        disease=(AutoCompleteTextView)findViewById(R.id.disease);
        region_dd=(ImageView)findViewById(R.id.region_dropdown);
        disease_dd=(ImageView)findViewById(R.id.disease_dropdown);
        geolocation=findViewById(R.id.geolocation);
        submit=(Button)findViewById(R.id.submit);

        region.setThreshold(1);
        disease.setThreshold(1);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, perms, 200);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat=location.getLatitude();
                lon=location.getLongitude();
                geolocation.setText(lat+", "+lon);

            }
        });
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,regions);
        region.setAdapter(adapter);
        ArrayAdapter<String> adapter1= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,diseases);
        disease.setAdapter(adapter1);
        region_dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                region.showDropDown();
            }
        });
        disease_dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disease.showDropDown();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tests=(TextView) findViewById(R.id.report_subdivision);
                String staff=uid.getText().toString();
                String pass=pwd.getText().toString();
                String reg=region.getText().toString();
                String dis=disease.getText().toString();
                String query,id=null,key=null;
                String status="Found";
                Boolean found=false;
                int disNo=0;
                Statement st;
                ResultSet rs;
                Toast toast;
                if(staff.isEmpty()||staff==null){
                    toast=Toast.makeText(getApplicationContext(),"UserID is empty",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(pass.isEmpty()||pass==null){
                    toast=Toast.makeText(getApplicationContext(),"Password is empty",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(reg.isEmpty()||region==null){
                    toast=Toast.makeText(getApplicationContext(),"Region is empty",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(dis.isEmpty()||dis==null){
                    toast=Toast.makeText(getApplicationContext(),"Disease is empty",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                try{
                    ConnectionHelper connectionHelper=new ConnectionHelper();
                    connect=connectionHelper.connectionclass();

                    if(connect!=null){
                        query="Select * from Users where userid='"+staff+"'";
                        st=connect.createStatement();
                        rs=st.executeQuery(query);

                        while(rs.next())
                        {
                            found=true;
                            key=rs.getString(3);

                        }
                        if(found==false){
                            toast=Toast.makeText(getApplicationContext(),"Staff ID not found",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                        if(pass.equals(key)){
                            query="select count(*) from Disease";
                            rs=st.executeQuery(query);
                            while(rs.next()){

                                disNo=Integer.parseInt(rs.getString(1));
                            }

                                if(disNo!=0){
                                    query="select top(1) * from Disease order by diseaseid desc";
                                    rs=st.executeQuery(query);
                                    while(rs.next()){
                                        id=rs.getString(1);
                                    }
                                    int no=Integer.parseInt(id.substring(1))+1;
                                    id="D"+String.format("%05d",no);
                                    query="insert into disease (diseaseid,disease,lat,lon,region,statuss,staffid)values('"+id+"','"+dis+"','"+lat+"','"+lon+"','"+reg+"','"+status+"','"+staff+"')";
                                    st.executeUpdate(query);

                                    uid.setText("");
                                    pwd.setText("");
                                    region.clearListSelection();
                                    disease.clearListSelection();
                                    toast=Toast.makeText(getApplicationContext(),"Your report is successfully submitted",Toast.LENGTH_LONG);
                                    toast.show();

                               }else{
                                    id="D00001";
                                    query="insert into disease (diseaseid,disease,lat,lon,region,statuss,staffid)values('"+id+"','"+dis+"','"+lat+"','"+lon+"','"+reg+"','"+status+"','"+staff+"')";
                                    st.executeUpdate(query);

                                    uid.setText("");
                                    pwd.setText("");
                                    region.clearListSelection();
                                    disease.clearListSelection();
                                    toast=Toast.makeText(getApplicationContext(),"Your report is successfully submitted",Toast.LENGTH_LONG);
                                    toast.show();
                                }

                        }
                        else{
                            toast=Toast.makeText(getApplicationContext(),"Password incorrect",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                    }else{
                        toast=Toast.makeText(getApplicationContext(),"Check Connection",Toast.LENGTH_LONG);
                        toast.show();

                    }
                }catch(Exception ex){
                    toast=Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG);
                    toast.show();

                }



            }
        });


    }



}
