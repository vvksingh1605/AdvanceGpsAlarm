package com.example.mahesh.alarmlocator;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.mahesh.alarmlocator.adapter.AlarmListAdapter;
import com.example.mahesh.alarmlocator.model.Alarmmodel;

import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity implements AlarmListAdapter.onItemClick {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView addAlarm;
    private TextView no_alarm;
    private AlarmListAdapter listAdapter;
    private ArrayList<Alarmmodel> alarmlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_list);
        getGPSInfo();
        turnGPSOn();
        recyclerView = findViewById(R.id.alarm_list);
        no_alarm = findViewById(R.id.no_alarm);
        initRecyclerView();
        addAlarm = findViewById(R.id.add_alarm);
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasLocationPermissionlocation = ContextCompat.checkSelfPermission(AlarmListActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasLocationPermissionlocation != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AlarmListActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            300);
                    return;
                }
                startActivity(new Intent(AlarmListActivity.this, AddAlarmActivity.class));
            }
        });

    }
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
    private void getGPSInfo() {
        Criteria criteria = new Criteria();
        String provider;
        Location location;
        LocationManager locationmanager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = locationmanager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationmanager.getLastKnownLocation(provider);

        } else {
            showGPSDisabledAlertToUser();
        }
    }
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlarmListActivity.this);
        alertDialogBuilder
                .setMessage(
                        "GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                            }
                        });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }// ;

    @Override
    protected void onResume() {
        super.onResume();
        updateList();

    }

    private void updateList() {
        if (alarmlist != null) {
            alarmlist.clear();
            if(Uttils.getAddressList(AlarmListActivity.this)!=null&&Uttils.getAddressList(AlarmListActivity.this).size()>0)
            alarmlist.addAll(Uttils.getAddressList(AlarmListActivity.this));
            if (alarmlist.size() > 0) {
                no_alarm.setVisibility(View.GONE);
                startService(new Intent(AlarmListActivity.this, MyService.class));
            } else {
                no_alarm.setVisibility(View.VISIBLE);
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        listAdapter = new AlarmListAdapter(this, alarmlist, this);
        recyclerView.setAdapter(listAdapter);
    }

    @Override
    public void onDisable(Alarmmodel alarmmodel) {
        alarmmodel.setDisable(!alarmmodel.isDisable());
        Uttils.saveAlarmModel(AlarmListActivity.this, alarmmodel);
        updateList();
    }

    @Override
    public void onDelete(Alarmmodel alarmmodel) {
        Uttils.deleteAlarm(AlarmListActivity.this, alarmmodel);
        updateList();
    }

    @Override
    public void onEdit(Alarmmodel alarmmodel) {
        int hasLocationPermissionlocation = ContextCompat.checkSelfPermission(AlarmListActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasLocationPermissionlocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AlarmListActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    300);
            return;
        }
        Intent intent=new Intent(AlarmListActivity.this, AddAlarmActivity.class);
        Bundle bundle =new Bundle();
        bundle.putParcelable(Constants.ALARM_MODEl,alarmmodel);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
