package com.lobxy.owa_retrofit;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lobxy.owa_retrofit.Model.Model;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //API KEY - 8d50279ebc4408fdf5bce4ead1648643

    public static final String API_KEY = "8d50279ebc4408fdf5bce4ead1648643";
    private static final String TAG = "Main";

    TextView text_temp, text_humid, text_condition, text_wind, text_pressure,
            text_maxTemp, text_minTemp, text_coord, text_name;

    private String mName = "london";
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
                if (mName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter city", Toast.LENGTH_SHORT).show();
                } else {
                    edit_input.setText("");

                    //hide keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    getData();
                }
            }
        });

    }

    private void getData() {
        ApiInterface apiInterface = ApiManager.getClient().create(ApiInterface.class);

        Call<Model> call = apiInterface.getData(mName, API_KEY, "metric");

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Its loading....");
        progressDialog.show();

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                int responseCode = response.code();
                progressDialog.dismiss();
                if (responseCode != 200) {
                    if (responseCode == 404) {
                        Log.i(TAG, "onResponse: res: " + response.errorBody().toString());
                        Toast.makeText(MainActivity.this, "Wrong City Name", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 400) {
                        Toast.makeText(MainActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 401) {
                        Toast.makeText(MainActivity.this, "Invalid API Key", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 500) {
                        Toast.makeText(MainActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 429) {
                        Toast.makeText(MainActivity.this, "API Key Blocked", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Internal Error", Toast.LENGTH_SHORT).show();
                    }
                } else setData(response);

            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Main", "onFailure: " + t.getLocalizedMessage());
            }
        });

    }

    private void setData(Response<Model> response) {
        String temp = String.valueOf(response.body().getMain().getTemp());
        String con = String.valueOf(response.body().getWeather().get(0).getMain());
        String humidity = String.valueOf(response.body().getMain().getHumidity());
        String wind = String.valueOf(response.body().getWind().getSpeed());
        String pressure = String.valueOf(response.body().getMain().getPressure());
        String coord = String.valueOf(response.body().getCoord().getLon()) + "° " + String.valueOf(response.body().getCoord().getLat() + "°");
        String name = String.valueOf(response.body().getName());
        String minTemp = String.valueOf(response.body().getMain().getTempMin());
        String maxTemp = String.valueOf(response.body().getMain().getTempMax());

        text_temp.setText(temp + "°C");
        text_condition.setText("Condition: " + con);
        text_humid.setText("Humidity: " + humidity + "%");
        text_wind.setText("Wind Speed: " + wind + "m/sec");
        text_pressure.setText("Pressure: " + pressure + "hpa");
        text_minTemp.setText("Min Temp.: " + minTemp + "°C");
        text_maxTemp.setText("Max Temp.: " + maxTemp + "°C");
        text_coord.setText("Coordinates: " + coord);
        text_name.setText(name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }
    //EOC
}
