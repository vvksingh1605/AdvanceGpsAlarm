package com.example.mahesh.alarmlocator;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mahesh.alarmlocator.model.Alarmmodel;

import java.io.IOException;

/**
 * Created by viveks on 2/16/2018.
 */

public class PopUpActivity extends AppCompatActivity {
    TextView alarmAddress;
    TextView optiontext;
    private Alarmmodel alarmmodel;
    private Button close;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_popup);
        alarmAddress=findViewById(R.id.alarm_address);
        close=findViewById(R.id.close);
        optiontext=findViewById(R.id.text);
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.ALARM_MODEl)) {
            alarmmodel=getIntent().getParcelableExtra(Constants.ALARM_MODEl);
            alarmAddress.setText(alarmmodel.getAddress());
            optiontext.setText(alarmmodel.getText());
            playSound(this, getAlarmUri());
            Uttils.deleteAlarm(this,alarmmodel);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();
                finish();

            }
        });
        
    }
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification, 
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}
