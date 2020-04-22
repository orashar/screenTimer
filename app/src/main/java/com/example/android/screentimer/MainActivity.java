package com.example.android.screentimer;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    List<Integer> availableOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initOptions();

        boolean settingsCanWrite = Settings.System.canWrite(this);

        if(!settingsCanWrite) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        }else {
            int next = getNextOption();
            setTimeout(next);

            int sec = next / 1000; String unit = "seconds";
            if(sec >= 60){
                sec = sec / 60;
                unit = "minutes";
            }
            String displayMessage = "Screen time set to " + sec + " " + unit;

            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.layout_widget);
            views.setTextViewText(R.id.time_tv, sec + unit);
            int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                Log.v("-/-/-/", "got intent extras not null");
                appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
            if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                Log.v("-/-/-/", "got intent id correct");
                AppWidgetManager.getInstance(this).updateAppWidget(appWidgetId, views);
            }
            Toast.makeText(getApplicationContext(), displayMessage, Toast.LENGTH_LONG).show();

        }

        finish();
    }

    private void initOptions() {
        availableOptions.add(15000);
        availableOptions.add(30000);
        availableOptions.add(60000);
        availableOptions.add(120000);
        availableOptions.add(300000);
        availableOptions.add(600000);
        availableOptions.add(1800000);
    }

    private int getNextOption(){
        int nextOption, currentOption = 0;
        try{
            currentOption = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception e){

        }
        if(availableOptions.contains(currentOption)){
            int i = availableOptions.indexOf(currentOption) + 1;
            if(i >= availableOptions.size()) i = 0;
            nextOption = availableOptions.get(i);
        } else{
            nextOption = availableOptions.get(0);
        }

        return nextOption;
    }

    private void setTimeout(int screenOffTimeout) {
        android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screenOffTimeout);
    }
}
