package com.example.mahesh.alarmlocator;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahesh.alarmlocator.model.Alarmmodel;
import com.example.mahesh.alarmlocator.model.MapAdressModel;
import com.google.gson.Gson;

public class AddAlarmActivity extends AppCompatActivity {
    TextView addadress;
    TextView addAlarm;
    TextView choosedate;
    MapAdressModel mapAdressModel;
    private String date;
    private EditText remindertext;
    private Alarmmodel Editalarmmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alarm_activity);
        addadress = findViewById(R.id.chooseaddress);
        choosedate = findViewById(R.id.date_pick);
        addAlarm = findViewById(R.id.save);
        remindertext = findViewById(R.id.reminder_text);
        Intent intent = getIntent();
//        if (intent.hasExtra(Constants.ALARM_MODEl)) {
//            Editalarmmodel=getIntent().getParcelableExtra(Constants.ALARM_MODEl);
//            if(Editalarmmodel!=null){
//                addadress.setText(Editalarmmodel.getText());
//
//            }
//        }
        choosedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AddAlarmActivity.this, DateChooseActivity.class), Constants.DATE_CHOOSE_REQUESTCODE);
            }
        });
        addadress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AddAlarmActivity.this, MapsActivity.class), 100);
            }
        });
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapAdressModel != null && date != null && date.length() > 0) {
                    Gson gson = new Gson();
                    Alarmmodel alarmmodel = new Alarmmodel("");
                    alarmmodel.setAddress(mapAdressModel.getAddress());
                    alarmmodel.setDisable(false);
                    alarmmodel.setLat(mapAdressModel.getLatitude());
                    alarmmodel.setLng(mapAdressModel.getLongitude());
                    alarmmodel.setText(remindertext.getText().toString());
                    Uttils.saveAlarmModel(AddAlarmActivity.this, alarmmodel);
                    startService(new Intent(AddAlarmActivity.this, MyService.class));
                    finish();
                } else {
                    if (mapAdressModel == null) {
                        Toast.makeText(AddAlarmActivity.this, "Please choose an address", Toast.LENGTH_LONG).show();
                    } else if (date != null && date.length() > 0) {
                        Toast.makeText(AddAlarmActivity.this, "Please add expiry date", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            mapAdressModel = data.getParcelableExtra("address_model");
            addadress.setText(mapAdressModel.getAddress());
        } else if (requestCode == Constants.DATE_CHOOSE_REQUESTCODE) {
            date = data.getStringExtra(Constants.DATE_CHOOSE);
            choosedate.setText(date);
        }
    }
}
