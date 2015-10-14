package ru.ubububu.dogeweather;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Tobias on 28.01.14.
 */
public class veryService extends Service {
    private static final String TAG = "veryService";

    private static final String LOCATION_PERMISSION = "android.permission.ACCESS_COARSE_LOCATION";

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        final AppWidgetManager widgetMgr = AppWidgetManager.getInstance(this.getApplicationContext());
        final int[] allWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        SharedPreferences sp = getSharedPreferences("so_conf", 0);
        int city = sp.getInt("loc", -1);
        String url;

        String openweatherKey = getString(R.string.openweather_key);
        DogeWeatherApiManager.DogeWeatherService dogeApi = DogeWeatherApiManager.getService();
        Call<DefaultWeather> req = null;
        if(city == -1){
            int res =  getApplicationContext().checkCallingOrSelfPermission(LOCATION_PERMISSION);
            if(res != PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "permission denied");
                return START_NOT_STICKY;
            }
            LocationManager locMgr = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = locMgr.getProviders(true);
            Location l = null;

            Double lat = 59.913869, lon = 10.752245;

            for(int i = providers.size()-1; i>=0; i--){
                l = locMgr.getLastKnownLocation(providers.get(i));
                if(l != null){
                    lat = l.getLatitude();
                    lon = l.getLongitude();
                    break;
                }
            }

            req = dogeApi.weatherByLatLon(lat, lon, openweatherKey);
        }else{
            req = dogeApi.weatherById(city, openweatherKey);
        }

        final RemoteViews views = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widgetlayout);
        try {
            getPackageManager().getPackageInfo("com.versobit.weatherdoge", PackageManager.GET_ACTIVITIES);
            // If we get this far, the package exists
            Intent i = new Intent();
            i.setClassName("com.versobit.weatherdoge", "com.versobit.weatherdoge.MainActivity");
            views.setOnClickPendingIntent(R.id.veryimage, PendingIntent.getActivity(this, 0, i, 0));
        } catch (Exception ex) {
            //
        }

        req.enqueue(new Callback<DefaultWeather>() {
            @Override
            public void onResponse(Response<DefaultWeather> response, Retrofit retrofit) {
                DefaultWeather resp = response.body();
                String temp = "0/0";
                float tempc;
                String icon = "01d";
                String desc = "wow ";
                String loc = "nowhere";

                Weather weather = resp.weather.get(0);
                icon = weather.icon;//jsonObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                desc += weather.description;//jsonObj.getJSONArray("weather").getJSONObject(0).getString("description");
                loc = resp.name;//jsonObj.getString("name");

                tempc = resp.main.temp;//jsonObj.getJSONObject("main").getDouble("temp");
                temp = String.valueOf(Math.round(tempc));
                temp += "°C\t";
                temp += String.valueOf(Math.round(tempc * 9 / 5 + 32));
                temp += "°F";

                views.setTextViewText(R.id.wowtext, desc);
                views.setTextViewText(R.id.suchtext, loc + ": " + temp);
                try {
                    views.setImageViewResource(R.id.veryimage, R.drawable.class.getField("d" + icon).getInt(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int id : allWidgets) {
                    widgetMgr.updateAppWidget(id, views);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

        Log.d("DOGE!", "Service running");

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
