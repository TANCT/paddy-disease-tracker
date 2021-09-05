package com.example.paddy_disease_tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashBoardFragment extends Fragment {
    TextView t_date, t_temp, t_city, t_unit, t_desc;
    ImageView icon;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v =inflater.inflate(R.layout.fragment_dashboard,container,false);

        t_date=(TextView)v.findViewById(R.id.text_date);
        t_city=(TextView)v.findViewById(R.id.text_city);
        t_temp=(TextView)v.findViewById(R.id.text_temp);
        t_unit=(TextView)v.findViewById(R.id.text_unit);
        t_desc=(TextView)v.findViewById(R.id.text_desc);
       icon=(ImageView)v.findViewById(R.id.icon_weather);

       findWeather();
       return v;
    }
    public void findWeather()
    {
        String url="https://api.openweathermap.org/data/2.5/weather?q=Kuala%20Lumpur&appid=197262e81c582f37f3b17e295ff743d6";

        JsonObjectRequest jor= new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");

                    t_temp.setText(temp);
                    t_city.setText(city);
                    t_desc.setText(description);

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
}
