package com.tobbentm.dogeweather;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        String loc = "auto";
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("loc", loc);
        editor.commit();
        configDone();
    }

    public void btnManual(View view) {
        String loc = ((EditText)findViewById(R.id.really_input))
                .getText().toString().replaceAll(" ", "%20");
        String url =  "http://api.openweathermap.org/data/2.5/find?q="+loc+"&mode=json";

        findViewById(R.id.plz_button).setEnabled(false);

        AsyncHttpClient cli = new AsyncHttpClient();

        cli.get(url, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String json){
                Log.d("DOGE! - Search\t\t", json);
                JSONArray list = null;
                ArrayList<String> cityList = new ArrayList<String>();
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    list = jsonObj.getJSONArray("list");
                    for(int i = 0; i < list.length(); i++){
                        cityList.add(list.getJSONObject(i).getString("name")
                            +", "+list.getJSONObject(i).getJSONObject("sys").getString("country"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                buildDialog(list, cityList.toArray(new String[cityList.size()]));
            }
        });
    }

    private void buildDialog(final JSONArray list, String[] cityList){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title);
        if(list != null && list.length() > 0){
            if(list.length() == 1){
                SharedPreferences.Editor editor = sp.edit();
                try {
                    editor.putString("loc", list.getJSONObject(0).getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
                configDone();
            }else{
                dialog.setItems(cityList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sp.edit();
                        try {
                            editor.putString("loc", list.getJSONObject(which).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                        configDone();
                    }
                });
            }
        }else{
            dialog.setMessage(R.string.dialog_error);
            findViewById(R.id.plz_button).setEnabled(true);
        }
        dialog.show();
    }

}
