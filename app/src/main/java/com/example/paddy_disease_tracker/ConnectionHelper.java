package com.example.paddy_disease_tracker;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con;
    String uname, pass,ip,port,database;
    @SuppressLint("NewApi")
    public Connection connectionclass(){
        database="ylkami_db";
        uname="ylkami@ylkami";
        pass="UMHackathon1012";
        port="1433";

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection=null;
        String ConnectionURL=null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            ConnectionURL="jdbc:jtds:sqlserver://ylkami.database.windows.net:1433;DatabaseName=ylkami_db;user=ylkami@ylkami;password=UMHackathon1012;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;ssl=request";
            connection= DriverManager.getConnection(ConnectionURL,"ylkami","UMHackathon1012");
        }catch(Exception ex){
            Log.e("Error",ex.getMessage());
        }
        return connection;


    }

}
