package com.androidproject.rainmain.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Saurabh on 2015-01-30.
 */
public  class WeatherForecastFragment extends Fragment {
    private static String TAG = WeatherForecastFragment.class.getSimpleName();

    private WeatherListItemAdapter adapter;
    ListView forecastListView;
    public WeatherForecastFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> forecastList = new ArrayList<String>();



        forecastListView = (ListView) rootView.findViewById(R.id.forecast_listview);

        updateForecastData("New York,US");

        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               WeatherData weatherData = adapter.getItem(position);
                Intent intent = new Intent(getActivity(),ForecastDetailActivity.class);
                intent.putExtra("weatherData",weatherData);
                startActivity(intent);
            }
        });
        return rootView;
    }
    public void updateForecastData(String city){

        try {

            if(isOnline()) {
                ArrayList<WeatherData> output = new FetchWeatherDataAsyncTask().execute(city).get();
                if(output!=null){
                    adapter = new WeatherListItemAdapter(output);
                    forecastListView.setAdapter(adapter);
                }



            }
            else
            {
                Toast.makeText(getActivity(),R.string.enable_connection,Toast.LENGTH_LONG).show();

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateForecastData("Syracuse");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }



    public class WeatherListItemAdapter extends BaseAdapter {

        private ArrayList<WeatherData> weatherDataList;


        public WeatherListItemAdapter(ArrayList<WeatherData> weatherDatas )
        {
            this.weatherDataList = weatherDatas;
        }
        @Override
        public int getCount() {
            if(weatherDataList.size()>0)
                return weatherDataList.size();
            return 0;
        }

        @Override
        public WeatherData getItem(int position) {
            return weatherDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if(convertView==null){
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.forecast_list_layout,null,false);

            }
            TextView listDateTV = (TextView) convertView.findViewById(R.id.listDateTV);
            TextView listWeatherTV = (TextView) convertView.findViewById(R.id.listMainWeatherTV);
            TextView listTempTV = (TextView) convertView.findViewById(R.id.listTempTV);
            ImageView listIconIV = (ImageView) convertView.findViewById(R.id.listIconIV);
            int redClr=224, greenClr=224, blueClr=224;
            convertView.setBackgroundColor(Color.rgb(redClr,greenClr,blueClr));
            WeatherData weatherData = weatherDataList.get(position);
            Typeface typeface =  Typeface.createFromAsset(getActivity().getAssets(),"fonts/cabin.ttf");
            listDateTV.setTypeface(typeface);
            listWeatherTV.setTypeface(typeface);
            listTempTV.setTypeface(typeface);
            listDateTV.setText(weatherData.getDate());
            listWeatherTV.setText(weatherData.getMainForecast());
            listTempTV.setText(weatherData.getMaxTemp()+"/"+weatherData.getMinTemp());
            listIconIV.setImageResource(weatherData.getImageId());


            return convertView;
        }


    }

}
