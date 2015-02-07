package com.androidproject.rainmain.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {


    private static String TAG = MainActivity.class.getSimpleName();
    private boolean searchClicked;
    private String searchQuery;
    private Drawable iconClose, iconSearch;
    private EditText searchQueryET;
    private MenuItem searchBarMenu;
    private MenuItem myLocationMenu;
    private TextView currentTempTv;
    private TextView cityNameTv;
    private TextView dateTv;
    private TextView humidityTv;
    private TextView windTv;
    private TextView pressureTv;
    private TextView degreeTV;
    private TextView unitTV;
    private ImageView weatherIconIV;
    private GoogleApiClient googleApiClient;
    private Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        ActionBar actionBar = getSupportActionBar();
       // actionBar.setLogo(R.drawable.ic_launcher);
        if (savedInstanceState == null) {
            searchClicked = false;
            searchQuery = "";
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherForecastFragment())
                    .commit();



        }
        else{
            searchQuery = savedInstanceState.getString("Search_Query");
            searchClicked = savedInstanceState.getBoolean("Search_Clicked");

        }
        Typeface typeface =  Typeface.createFromAsset(getAssets(),"fonts/cabin.ttf");
        weatherIconIV = (ImageView) findViewById(R.id.weatherIconIV);
        iconClose = getResources().getDrawable(R.drawable.cancel_action);
        iconSearch = getResources().getDrawable(R.drawable.search_action);

        currentTempTv = (TextView) findViewById(R.id.currentTempTV);
        currentTempTv.setTypeface(typeface);

        cityNameTv = (TextView) findViewById(R.id.cityNameTV);
        cityNameTv.setTypeface(typeface);

        humidityTv = (TextView) findViewById(R.id.humidityTV);
        humidityTv.setTypeface(typeface);

        windTv = (TextView) findViewById(R.id.windTV);
        windTv.setTypeface(typeface);

        pressureTv = (TextView) findViewById(R.id.pressureTV);
        pressureTv.setTypeface(typeface);

        dateTv = (TextView) findViewById(R.id.dateTV);
        dateTv.setTypeface(typeface);

        degreeTV = (TextView) findViewById(R.id.degreeTV);
        degreeTV.setTypeface(typeface);

        unitTV = (TextView) findViewById(R.id.unitTV);
        unitTV.setTypeface(typeface);

        if(searchClicked)
        {
           showSearchBar(searchQuery);
        }
        //Default
        if(isOnline()){
            new  FetchCurrentWeatherAysncTask().execute("New York,US");


        }
        else{
           toastEnableConnection();
        }




    }
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

    public void toastEnableConnection()

    {
        Toast.makeText(getApplicationContext(),R.string.enable_connection,Toast.LENGTH_LONG).show();

    }
    public synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }
    public void showSearchBar(String text){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);
        searchQueryET = (EditText) actionBar.getCustomView().findViewById(R.id.search_editText);

        searchQueryET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(hasFocus){
                    imm.showSoftInput(searchQueryET,InputMethodManager.SHOW_IMPLICIT);
                }
                else{

                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        searchQueryET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    searchQuery = searchQueryET.getText().toString();
                    hideSearchBar();
                   WeatherForecastFragment weatherForecast  = (WeatherForecastFragment)getSupportFragmentManager().findFragmentById(R.id.container);
                    if(isOnline()){
                        try {
                            WeatherData weatherData =   new  FetchCurrentWeatherAysncTask().execute(searchQuery).get();
                            if(weatherData!=null)
                                weatherForecast.updateForecastData(searchQuery);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


                    }
                    else{
                       toastEnableConnection();
                    }


                     return true;
                }
                return false;
            }
        });
        searchQueryET.setText(text);
        searchQueryET.requestFocus();
        searchBarMenu.setIcon(iconClose);
        searchClicked = true;
    }
    public void hideSearchBar(){
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        searchQueryET.setText("");
        myLocationMenu.setVisible(false);
        searchQueryET.clearFocus();
        searchBarMenu.setIcon(iconSearch);
        searchClicked = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchBarMenu = menu.findItem(R.id.search_action);
        myLocationMenu = menu.findItem(R.id.my_location_action);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id== R.id.search_action)
        {
         if(!searchClicked){

             showSearchBar(searchQuery);
             myLocationMenu.setVisible(true);
            //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
         }

            else
         {
             hideSearchBar();
             myLocationMenu.setVisible(false);
             //inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
         }

            return true;

        }
        if(id==R.id.my_location_action){
            if(gpsEnabled()){
                if(location!=null){
                    if(isOnline()){
                        String cityName = getAddress(location.getLatitude(),location.getLongitude());
                        searchQueryET.setText(cityName);
                    }
                    else
                    {
                        toastEnableConnection();
                    }

                }
                else
                {
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                }

            }
            else
                Toast.makeText(getApplicationContext(),"Enable GPS to get current location",Toast.LENGTH_LONG).show();




        }

        return super.onOptionsItemSelected(item);
    }
    public Boolean gpsEnabled()
    {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public String getAddress(double latitude, double longitude)
    {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        StringBuffer resultString = new StringBuffer("");
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            if(addressList.size()>0){
                Address address = addressList.get(0);
                String city = address.getLocality();
                String postalCode = address.getPostalCode();
                String countryCode = address.getCountryCode();
                String state = address.getAdminArea();
                Log.v(TAG,countryCode+city);
                resultString.append(city + ",").append(state);
            }

        } catch (IOException e) {
            Log.e(TAG,"Error @ getAddress()",e);
        }
         return resultString.toString();

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("Search_Clicked", searchClicked);
        outState.putString("Search_Query",searchQuery);


    }

    @Override
    public void onConnected(Bundle bundle) {
       location =  LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
       /* WeatherForecastFragment weatherForecast  = (WeatherForecastFragment)getSupportFragmentManager().findFragmentById(R.id.container);

        if(weatherForecast.isOnline()){
            new  FetchCurrentWeatherAysncTask().execute("New York,US");
            weatherForecast.updateForecastData("New York,US");

        }
        else{
            Toast.makeText(getApplicationContext(),"Please Enable Internet Connection",Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    class FetchCurrentWeatherAysncTask extends AsyncTask<String, Void, WeatherData>{

        @Override
        protected WeatherData doInBackground(String... params) {

            WeatherData weatherData = parseWeatherData(getWeatherData(params[0]));
            if(weatherData==null)
                return null;
            weatherData.setCityName(params[0]);
            return weatherData;
        }
        public WeatherData parseWeatherData(String jsonString)
        {

            final String main_tag = "main";
            final String temp_tag = "temp";
            final String humidity_tag = "humidity";
            final String pressure_tag = "pressure";
            final String date_time_tag = "dt";
            final String weather_tag =  "weather";
            final String wind_tag ="wind";
            final String speed_tag = "speed";
            final String weather_id_tag = "id";

            WeatherData weatherData =  new WeatherData();
            List<String> weatherDataList = new ArrayList<>();
            try {
                JSONObject dataObject = new JSONObject(jsonString);
               JSONObject mainObject = dataObject.getJSONObject(main_tag);
                weatherData.setCurrentTemp(mainObject.getLong(temp_tag));
                weatherData.setPressure(mainObject.getLong(pressure_tag));
                weatherData.setHumidity(mainObject.getLong(humidity_tag));
                weatherData.setWindSpeed(dataObject.getJSONObject(wind_tag).getLong(speed_tag));
                Date date = new Date(dataObject.getLong(date_time_tag)*1000);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, E");
                weatherData.setDate(dateFormat.format(date).toString());
                JSONObject weatherDataTag =  dataObject.getJSONArray(weather_tag).getJSONObject(0);
                weatherData.setMainForecast(weatherDataTag.getString(main_tag));
                weatherData.setMainWeatherId(weatherDataTag.getLong(weather_id_tag));

            } catch (JSONException e) {
                Log.e(TAG, "Error @ parseWeatherData", e);
                return null;
            }
            return weatherData;

        }

        public  String getWeatherData(String cityName)
        {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            String units = "metric";

            final String openWeatherBaseURL = "http://api.openweathermap.org/data/2.5/weather?";
            final String locationParameter = "q";
            final String unitsParameter = "units";

            Uri finalUri = Uri.parse(openWeatherBaseURL).buildUpon()
                    .appendQueryParameter(locationParameter, cityName)
                    .appendQueryParameter(unitsParameter,units).build();

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
        @Override
        protected void onPostExecute(WeatherData weatherData) {

    super.onPostExecute(weatherData);
    if(weatherData!=null){
        currentTempTv.setText(weatherData.getCurrentTemp().toString());
        if(weatherData.getCityName().contains(",")){
            String city = weatherData.getCityName().split(",")[0];
            cityNameTv.setText(city);
        }
        else
        cityNameTv.setText(weatherData.getCityName().toUpperCase());
        humidityTv.setText("Humidity\n"+weatherData.getHumidity().toString());
        windTv.setText("Wind\n"+weatherData.getWindSpeed().toString());
        pressureTv.setText("Pressure\n"+weatherData.getPressure().toString());
        dateTv.setText(weatherData.getDate());
        weatherIconIV.setImageResource(setImage(weatherData.getMainWeatherId()));

    }
    else
    {
        Toast.makeText(getApplicationContext(),R.string.city_not_found,Toast.LENGTH_LONG).show();
    }


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

}
}
