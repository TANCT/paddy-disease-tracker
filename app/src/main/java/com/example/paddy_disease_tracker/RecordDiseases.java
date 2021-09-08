package com.example.paddy_disease_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class RecordDiseases extends AppCompatActivity {

    Connection connect;
    Toast toast;
    String query;
    Statement st;
    ResultSet rs;
    String status,region,disease,diseaseid,key;
    Float lat,lon;
    Date datetime;
    ListView listview;
    Button submit;
    EditText uid,did,pwd;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_diseases);
        listview=findViewById(R.id.listview);
        submit=findViewById(R.id.csubmit);
        uid=findViewById(R.id.cuid);
        pwd=findViewById(R.id.cpwd);
        did=findViewById(R.id.cdid);
        ArrayList<String> diseases=new ArrayList<String>();
        ArrayList<String> diseaseIDs=new ArrayList<String>();
        ArrayList<Float> latitude=new ArrayList<Float>();
        ArrayList<Float> longitude=new ArrayList<Float>();
        test=findViewById(R.id.record_subdivision);


        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionclass();

            if (connect != null) {
                query = "Select * from Disease";
                st = connect.createStatement();
                rs = st.executeQuery(query);
                while (rs.next()) {
                    status=rs.getString(7);
                    if(status.equals("Found")){
                        disease=rs.getString(3);
                        lat=rs.getFloat(4);
                        lon=rs.getFloat(5);
                        region=rs.getString(6);
                        datetime=rs.getDate(2);
                        diseaseid=rs.getString(1);
                        diseases.add("DateTime: "+datetime.toString()+" Region: "+region+" \nDisease ID: "+diseaseid+" Disease: "+disease);
                        diseaseIDs.add(diseaseid);
                        latitude.add(lat);
                        longitude.add(lon);

                    }

                }

            }else{
                toast= Toast.makeText(getApplicationContext(),"Check Connection",Toast.LENGTH_LONG);
                toast.show();
            }
        }catch(Exception e){
            toast= Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,diseases);

        listview.setAdapter(adapter);
        listview.setClickable(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(RecordDiseases.this,MapsActivity.class);
                i.putExtra("lat",latitude.get(position));
                i.putExtra("lon",longitude.get(position));
                startActivity(i);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid=uid.getText().toString();
                String userpwd=pwd.getText().toString();
                String didClosed=did.getText().toString();
                Boolean found=false;
                Boolean found2=false;
                if(userid.isEmpty()||userid==null){
                    toast=Toast.makeText(getApplicationContext(),"UserID is empty",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(userpwd.isEmpty()||userpwd==null){
                    toast=Toast.makeText(getApplicationContext(),"Password is empty",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(didClosed.isEmpty()||didClosed==null){
                    toast=Toast.makeText(getApplicationContext(),"Disease ID is empty",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                try{

                    if(connect!=null){
                        query="Select * from Users where userid='"+userid+"'";
                        st=connect.createStatement();
                        rs=st.executeQuery(query);

                        while(rs.next())
                        {
                            found=true;
                            key=rs.getString(3);

                        }
                        if(found==false){
                            toast=Toast.makeText(getApplicationContext(),"User ID not found",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                        if(userpwd.equals(key)){
                            query="select * from Disease where diseaseid='"+didClosed+"'";
                            rs=st.executeQuery(query);
                            while(rs.next()){
                                found2=true;
                                test.setText(rs.getString(7));
                                if(rs.getString(7).equals("Found")){
                                    query="Update Disease Set statuss='Closed' Where diseaseid='"+didClosed+"'";
                                    st.executeUpdate(query);
                                    toast=Toast.makeText(getApplicationContext(),"Case is successfully closed",Toast.LENGTH_LONG);
                                    toast.show();
                                    did.setText("");
                                }else{
                                    toast=Toast.makeText(getApplicationContext(),"Case Closed Already",Toast.LENGTH_LONG);
                                    toast.show();
                                    return;
                                }
                            }
                            if(found2==false){
                                toast=Toast.makeText(getApplicationContext(),"Disease ID not found",Toast.LENGTH_LONG);
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
                    String error=ex.getMessage();

                }
            }
        });


    }
}