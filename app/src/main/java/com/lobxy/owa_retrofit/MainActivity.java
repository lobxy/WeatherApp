package com.lobxy.owa_retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lobxy.owa_retrofit.Model.Model;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //API KEY - 8d50279ebc4408fdf5bce4ead1648643
    //url - https://samples.openweathermap.org/data/2.5/weather?q=London&appid=b6907d289e10d714a6e88b30761fae22

    public static final String API_KEY = "8d50279ebc4408fdf5bce4ead1648643";

    TextView text_temp, text_humid, text_condition, text_wind, text_pressure, text_sunset, text_sunrise,
            text_maxTemp, text_minTemp, text_coord, text_name;

    private String mName = "London";
    private EditText edit_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_temp = findViewById(R.id.main_temp);
        text_humid = findViewById(R.id.main_humid);
        text_condition = findViewById(R.id.main_percep);
        text_wind = findViewById(R.id.main_wind);
        text_pressure = findViewById(R.id.main_pressure);
        text_sunset = findViewById(R.id.main_sunset);
        text_sunrise = findViewById(R.id.main_sunrise);
        text_minTemp = findViewById(R.id.main_minTemp);
        text_maxTemp = findViewById(R.id.main_maxTemp);
        text_coord = findViewById(R.id.main_coord);
        text_name = findViewById(R.id.main_name);

        edit_input = findViewById(R.id.main_input);


        Button search = findViewById(R.id.main_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = edit_input.getText().toString().trim();

                //todo: determine whether input is number or string.

                setData();
            }
        });

        setData();

    }

    private void setData() {
        ApiInterface apiInterface = ApiManager.getClient().create(ApiInterface.class);

        Call<Model> call = apiInterface.getData(mName, API_KEY, "metric");

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                String temp = String.valueOf(response.body().getMain().getTemp());
                String con = String.valueOf(response.body().getWeather().get(0).getMain());
                String humidity = String.valueOf(response.body().getMain().getHumidity());
                String wind = String.valueOf(response.body().getWind().getSpeed());

                String pressure = String.valueOf(response.body().getMain().getPressure());
                String coord = String.valueOf(response.body().getCoord().getLat()) + " :: " + String.valueOf(response.body().getCoord().getLon());
                String name = String.valueOf(response.body().getName());
                String minTemp = String.valueOf(response.body().getMain().getTempMin());
                String maxTemp = String.valueOf(response.body().getMain().getTempMax());

                long set = response.body().getSys().getSunset();

                String sunset = String.format("%02d:%02d",
                        //Hours
                        TimeUnit.MILLISECONDS.toHours(set) -
                                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(set)),
                        //Minutes
                        TimeUnit.MILLISECONDS.toMinutes(set) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(set)));

                long rise = response.body().getSys().getSunrise();

                String sunrise = String.format("%02d:%02d",
                        //Hours
                        TimeUnit.MILLISECONDS.toHours(rise) -
                                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(rise)),
                        //Minutes
                        TimeUnit.MILLISECONDS.toMinutes(rise) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(rise)));

                text_temp.setText(temp + "C");
                text_condition.setText("Condition: " + con);
                text_humid.setText("Humidity: " + humidity);
                text_wind.setText("Wind Speed: " + wind);

                text_pressure.setText("Pressure: " + pressure);
                text_sunrise.setText("Sunrise: " + sunrise);
                text_sunset.setText("Sunset: " + sunset);
                text_minTemp.setText("Min Temp.: " + minTemp);
                text_maxTemp.setText("Max Temp.: " + maxTemp);
                text_coord.setText("Coordinates: " + coord);
                text_name.setText(name);

            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                Log.i("Main", "onFailure: " + t.getLocalizedMessage());
            }
        });

    }

    //EOC
}
