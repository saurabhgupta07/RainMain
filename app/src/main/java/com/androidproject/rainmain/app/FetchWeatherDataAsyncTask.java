package com.androidproject.rainmain.app;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.FrameLayout;

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

/**
 * Created by Saurabh on 2015-02-01.
 */
class FetchWeatherDataAsyncTask extends AsyncTask<String, Void, ArrayList<WeatherData>> {

    private static String TAG = FetchWeatherDataAsyncTask.class.getSimpleName();

    @Override
    protected ArrayList<WeatherData> doInBackground(String... params) {
        if(params.length==0)
            return null;

        String jsonString = getWeatherData(params[0]);
        return parseWeatherData(jsonString,params[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<WeatherData> strings) {
        super.onPostExecute(strings);
       // weatherForecastAdapter.clear();
        /*for(int i=0;i<strings.size();i++){
            weatherForecastAdapter.add(strings.get(i));
        }*/

    }


    public ArrayList<WeatherData> parseWeatherData(String jsonString, String cityName)
    {
        final String list_tag = "list";
        final String temperature_tag = "temp";
        final String max_temp_tag = "max";
        final String min_temp_tag = "min";
        final String date_time_tag = "dt";
        final String weather_tag =  "weather";
        final String main_tag = "main";
        final String id_tag = "id";
        final String pressure_tag = "pressure";
        final String humidity_tag = "humidity";
        final String desc_tag = "description";
        List<String> weatherDataList = new ArrayList<>();
        ArrayList<WeatherData> wdl =  new ArrayList<WeatherData>();
        try {
            JSONObject weatherObject = new JSONObject(jsonString);
            JSONArray dayForecastData = weatherObject.getJSONArray(list_tag);


            for(int i=0;i<dayForecastData.length();i++)
            {
                WeatherData weatherDataObject = new WeatherData();
                JSONObject dayData = dayForecastData.getJSONObject(i);
                JSONObject tempData = dayData.getJSONObject(temperature_tag);

                JSONObject weatherData =  dayData.getJSONArray(weather_tag).getJSONObject(0);
                Long maxTemp = tempData.getLong(max_temp_tag);
                Long minTemp =  tempData.getLong(min_temp_tag);
                long dateTime = dayData.getLong(date_time_tag);
                String mainWeather = weatherData.getString(main_tag);
                long weatherCode = weatherData.getLong(id_tag);

                Date date = new Date(dateTime*1000);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, E");
                String dayDate = dateFormat.format(date).toString();
                weatherDataList.add(dayDate + "  "+maxTemp+"/"+minTemp+"  "+mainWeather);
                weatherDataObject.setDate(dayDate);
                weatherDataObject.setHumidity(dayData.getLong(humidity_tag));
                weatherDataObject.setPressure(dayData.getLong(pressure_tag));
                weatherDataObject.setMaxTemp(maxTemp);
                weatherDataObject.setMinTemp(minTemp);
                weatherDataObject.setMainForecast(mainWeather);
                weatherDataObject.setMainWeatherId(weatherCode);
                weatherDataObject.setDescpForecast(weatherData.getString(desc_tag));
                weatherDataObject.setImageId(setImage(weatherCode));
                weatherDataObject.setCityName(cityName);
                wdl.add(weatherDataObject);


            }


        } catch (JSONException e) {
            Log.e(TAG, "Error @ parseWeatherData", e);
            return null;
        }
        return wdl;
        //return weatherDataList;

    }
    int setImage(Long code)
    {
        if(code>=200 && code<300)
            return R.drawable.thunderstorm;
        if(code>=300 && code<400)
            return R.drawable.drizzle;
        if(code>=500 && code<600)
            return R.drawable.rain;
        if(code>=600 && code<700)
            return R.drawable.snow;
        if(code>=700&& code<800)
            return R.drawable.mist;
        if(code==800)
            return R.drawable.clear_sky;
        if(code==801)
            return R.drawable.few_clouds;
        if(code>801 && code<900 )
            return R.drawable.cloudy;
        return R.drawable.ic_launcher;

    }
    /*function that calls API and fetches JSON data*/
    public static String getWeatherData(String cityName)
    {
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String mode = "json";
        String units = "metric";
        Integer numOfDays = 10;
        final String openWeatherBaseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String locationParameter = "q";
        final String modeParameter = "mode";
        final String unitsParameter = "units";
        final String cntParameter = "cnt";
        Uri finalUri = Uri.parse(openWeatherBaseURL).buildUpon()
                .appendQueryParameter(locationParameter,cityName)
                .appendQueryParameter(modeParameter,mode)
                .appendQueryParameter(unitsParameter,units)
                .appendQueryParameter(cntParameter,Integer.toString(numOfDays)).build();

        //String urlString = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
        String jsonStr;

        try {
            Log.v(TAG,finalUri.toString());
            URL url = new URL(finalUri.toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            if(inputStream == null){
                return null;
            }
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String currentLine;
            while((currentLine = reader.readLine())!=null){
                buffer.append(currentLine+"\n");
            }
            if(buffer.length()==0){
                return null;
            }
            jsonStr = buffer.toString();
            Log.v(TAG,jsonStr);




        } catch (Exception e) {
            Log.e(TAG,"Error @ getWeatherData()",e);
            jsonStr = null;

        }
        finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage()+"@ getWeatherData()");
                }
            }
        }
        return jsonStr;


    }

    }