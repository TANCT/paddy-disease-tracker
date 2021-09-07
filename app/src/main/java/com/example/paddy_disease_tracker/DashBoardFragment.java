package com.example.paddy_disease_tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashBoardFragment extends Fragment {
    TextView t_date, t_temp, t_city, t_unit, t_desc, t_hum, t_wind, t_press, t_location;
    ImageView icon;
    String[] perms = {"android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE"
    };
    private FusedLocationProviderClient fusedLocationClient;
    static int lon, lat;
    String location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        t_date = (TextView) v.findViewById(R.id.text_date);
        t_city = (TextView) v.findViewById(R.id.text_city);
        t_temp = (TextView) v.findViewById(R.id.text_temp);
        t_unit = (TextView) v.findViewById(R.id.text_unit);
        t_desc = (TextView) v.findViewById(R.id.text_desc);
        t_hum = (TextView) v.findViewById(R.id.text_humidity);
        t_wind = (TextView) v.findViewById(R.id.text_wind);
        t_press = (TextView) v.findViewById(R.id.text_pressure);
        icon = (ImageView) v.findViewById(R.id.icon_weather);
        t_location = (TextView) v.findViewById(R.id.location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), perms, 200);
            return v;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat = (int)location.getLatitude();
                lon = (int)location.getLongitude();
                t_location.setText("lat="+lat + "&lon=" + lon);
                findWeather();

            }

        });
        //String location= (String) t_location.getText();


       return v;
    }
    public void findWeather()
    {

        String location= String.valueOf(t_location.getText());
        String url="https://api.openweathermap.org/data/2.5/weather?"+location+"&appid=197262e81c582f37f3b17e295ff743d6";
        //String url="https://api.openweathermap.org/data/2.5/weather?q=Kuala%20Lumpur&appid=197262e81c582f37f3b17e295ff743d6";

        JsonObjectRequest jor= new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String iconNo=object.getString("icon");
                    String description = object.getString("description");
                    String city = response.getString("name");
                    String pressure=(String.valueOf(main_object.getInt("pressure")))+"\nhPa";
                    String humidity=(String.valueOf(main_object.getInt("humidity")))+"%";
                    JSONObject wind_object=response.getJSONObject("wind");
                    String wind= (String.valueOf(wind_object.getDouble("speed")))+"m/s\n";
                    wind+=(String.valueOf(wind_object.getDouble("deg")))+"°";

                    t_temp.setText(temp);
                    t_city.setText(city);
                    t_desc.setText(description);
                    t_wind.setText(wind);
                    t_hum.setText(humidity);
                    t_press.setText(pressure);
                    setIcon(iconNo);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String formatted_date = sdf.format(calendar.getTime());

                    t_date.setText(formatted_date);

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int - 278.15) ;
                    centi = Math.round(centi);
                    int i = (int) centi;
                    t_temp.setText(String.valueOf(i));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                }
            });
        RequestQueue queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(jor);

    }

    public void setIcon(String iconNo)
    {
        if(iconNo.equals("01d"))
            icon.setImageResource(R.drawable.w01d);
        else if(iconNo.equals("01n"))
            icon.setImageResource(R.drawable.w01n);
        else if(iconNo.equals("02d"))
            icon.setImageResource(R.drawable.w02d);
        else if(iconNo.equals("02n"))
            icon.setImageResource(R.drawable.w02n);
        else if(iconNo.equals("03d"))
            icon.setImageResource(R.drawable.w03d);
        else if(iconNo.equals("03n"))
            icon.setImageResource(R.drawable.w03n);
        else if(iconNo.equals("04d"))
            icon.setImageResource(R.drawable.w04d);
        else if(iconNo.equals("04n"))
            icon.setImageResource(R.drawable.w04n);
        else if(iconNo.equals("09d"))
            icon.setImageResource(R.drawable.w09d);
        else if(iconNo.equals("09n"))
            icon.setImageResource(R.drawable.w09n);
        else if(iconNo.equals("10d"))
            icon.setImageResource(R.drawable.w10d);
        else if(iconNo.equals("10n"))
            icon.setImageResource(R.drawable.w10n);
        else if(iconNo.equals("11d"))
            icon.setImageResource(R.drawable.w11d);
        else if(iconNo.equals("11n"))
            icon.setImageResource(R.drawable.w11n);
        else if(iconNo.equals("13d"))
            icon.setImageResource(R.drawable.w13d);
        else if(iconNo.equals("13n"))
            icon.setImageResource(R.drawable.w13n);
        else if(iconNo.equals("50d"))
            icon.setImageResource(R.drawable.w50d);
        else if(iconNo.equals("50n"))
            icon.setImageResource(R.drawable.w50n);
    }

}
