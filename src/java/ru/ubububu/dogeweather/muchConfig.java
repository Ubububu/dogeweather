package ru.ubububu.dogeweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Tobias on 04.03.14.
 */
public class muchConfig extends Activity {

    SharedPreferences sp;
    int wID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configlayout);
        sp = getSharedPreferences("so_conf", 0);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            wID = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wID);
        setResult(RESULT_CANCELED, resultValue);
    }

    private void configDone(){

        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wID);
        setResult(RESULT_OK, result);

        //Ugly hack, please forgive me
        new suchprovider().onUpdate(this, AppWidgetManager.getInstance(this), new int[]{wID});

        finish();
    }

    public void btnAuto(View view) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("loc", -1);
        editor.commit();
        configDone();
    }

    public void btnManual(View view) {
        String loc = ((EditText)findViewById(R.id.really_input))
                .getText().toString().replaceAll(" ", "%20");

        findViewById(R.id.plz_button).setEnabled(false);

        String openweatherKey = getString(R.string.openweather_key);
        DogeWeatherApiManager.DogeWeatherService dogeApi = DogeWeatherApiManager.getService();
        Call<ListWeather> request = dogeApi.findByName(loc, openweatherKey);
        request.enqueue(callback);
    }

    private void buildDialog(final ListWeather list){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title);
        if(list != null && list.count > 0){
            if(list.count == 1){
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("loc", list.list.get(0).id);
                editor.commit();
                configDone();
                return;
            } else {
                String[] cityList = new String[list.count];
                for(int i=0;i<list.count;i++) {
                    DefaultWeather city = list.list.get(0);
                    cityList[i] = city.name+", "+city.sys.country;
                }
                dialog.setItems(cityList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("loc", list.list.get(which).id);
                        editor.commit();
                        configDone();
                    }
                });
            }
        } else{
            dialog.setMessage(R.string.dialog_error);
            findViewById(R.id.plz_button).setEnabled(true);
        }
        dialog.show();
    }

    private Callback<ListWeather> callback = new Callback<ListWeather>() {
        @Override
        public void onResponse(Response<ListWeather> response, Retrofit retrofit) {
            buildDialog(response.body());
        }

        @Override
        public void onFailure(Throwable t) {
            buildDialog(null);
        }
    };
}
