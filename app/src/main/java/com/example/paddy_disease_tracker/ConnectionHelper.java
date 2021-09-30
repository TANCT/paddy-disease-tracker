package com.example.paddy_disease_tracker;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/*Insert your own azure database name, username, and password */
public class ConnectionHelper {
    Connection con;
    String uname, pass,ip,port,database;
    @SuppressLint("NewApi")
    public Connection connectionclass(){
        
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection=null;
        String ConnectionURL=null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            ConnectionURL="jdbc:jtds:sqlserver://ylkami.database.windows.net:1433;DatabaseName= dB name;user=dB user;password=dB password;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;ssl=request";
            connection= DriverManager.getConnection(ConnectionURL,"ylkami","UMHackathon1012");
        }catch(Exception ex){
            Log.e("Error",ex.getMessage());
        }
        return connection;


    }

}
