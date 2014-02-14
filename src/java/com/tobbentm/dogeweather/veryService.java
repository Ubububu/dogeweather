package com.tobbentm.dogeweather;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Tobias on 28.01.14.
 */
public class veryService extends Service {

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        final AppWidgetManager widgetMgr = AppWidgetManager.getInstance(this.getApplicationContext());
        final int[] allWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

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

        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon=" + lon + "&units=metric";

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
        AsyncHttpClient cli = new AsyncHttpClient();

        cli.get(url, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String json){
                Log.d("DOGE!", json);
                String temp = "0/0";
                Double tempc;
                String icon = "01d";
                String desc = "wow ";
                String loc = "nowhere";

                try {
                    JSONObject jsonObj = new JSONObject(json);
                    icon = jsonObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                    desc += jsonObj.getJSONArray("weather").getJSONObject(0).getString("description");
                    loc = jsonObj.getString("name");

                    tempc = jsonObj.getJSONObject("main").getDouble("temp");
                    temp = String.valueOf(Math.round(tempc));
                    temp += "°C\t";
                    temp += String.valueOf(Math.round(tempc * 9 / 5 + 32));
                    temp += "°F";

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                views.setTextViewText(R.id.wowtext, desc);
                views.setTextViewText(R.id.suchtext, loc+": "+temp);
                try {
                    views.setImageViewResource(R.id.veryimage, R.drawable.class.getField("d"+icon).getInt(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for(int id : allWidgets){
                    widgetMgr.updateAppWidget(id, views);
                }
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
