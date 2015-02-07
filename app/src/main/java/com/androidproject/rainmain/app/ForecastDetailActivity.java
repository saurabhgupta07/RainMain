package com.androidproject.rainmain.app;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ForecastDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_detail);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));

        if (savedInstanceState == null) {
           /* getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();*/
        }
        WeatherData weatherData = (WeatherData)getIntent().getSerializableExtra("weatherData");
        Typeface typeface =  Typeface.createFromAsset(getAssets(),"fonts/cabin.ttf");
        TextView highLabel = (TextView) findViewById(R.id.highTextView);
        highLabel.setTypeface(typeface);

        TextView highTemp = (TextView) findViewById(R.id.highTempTextView);
        highTemp.setText(weatherData.getMaxTemp().toString());
        highTemp.setTypeface(typeface);

        TextView highUnits = (TextView) findViewById(R.id.highUnitsTextView);
        highUnits.setTypeface(typeface);

        TextView lowLabel = (TextView) findViewById(R.id.lowTextView);
        lowLabel.setTypeface(typeface);

        TextView lowTemp = (TextView) findViewById(R.id.lowTempTextView);
        lowTemp.setText(weatherData.getMinTemp().toString());
        lowTemp.setTypeface(typeface);

        TextView lowUnits = (TextView) findViewById(R.id.lowUnitsTextView);
        lowUnits.setTypeface(typeface);

        TextView city = (TextView) findViewById(R.id.cityName);
        city.setText(weatherData.getCityName());
        city.setTypeface(typeface);

        TextView date = (TextView) findViewById(R.id.date);
        date.setText(weatherData.getDate());
        date.setTypeface(typeface);

        ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        weatherIcon.setImageResource(weatherData.getImageId());

        TextView pressure = (TextView) findViewById(R.id.pressure);
        pressure.setText("Pressure "+weatherData.getPressure());

        TextView humidity = (TextView) findViewById(R.id.humidity);
        humidity.setText("Humidity "+weatherData.getHumidity());
        pressure.setTypeface(typeface);
        humidity.setTypeface(typeface);

        TextView mainWeather = (TextView) findViewById(R.id.main);
        mainWeather.setText(weatherData.getMainForecast());
        mainWeather.setTypeface(typeface);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(weatherData.getDescpForecast());
        description.setTypeface(typeface);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forecast_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == android.R.id.home) {
            startActivity(new Intent(ForecastDetailActivity.this,MainActivity.class));
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_forecast_detail, container, false);

            return rootView;
        }
    }
}
