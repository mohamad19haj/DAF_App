package com.example.dafapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private AudioProcessor audioProcessor;
    private SeekBar delaySeekBar, speedSeekBar;
    private int delayMs = 100; // التأخير الافتراضي 100 مللي ثانية
    private float speedFactor = 1.0f; // سرعة التشغيل الافتراضية 1.0 (عادي)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        delaySeekBar = findViewById(R.id.delaySeekBar);
        speedSeekBar = findViewById(R.id.speedSeekBar);

        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayMs = progress + 50; // الحد الأدنى 50 مللي ثانية
                if (audioProcessor != null) {
                    audioProcessor.setDelay(delayMs);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedFactor = 0.5f + (progress / 100.0f); // تتراوح بين 0.5x و 1.5x
                if (audioProcessor != null) {
                    audioProcessor.setSpeed(speedFactor);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            startAudioProcessing();
        }
    }

    private void startAudioProcessing() {
        audioProcessor = new AudioProcessor();
        audioProcessor.setDelay(delayMs);
        audioProcessor.setSpeed(speedFactor);
        audioProcessor.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioProcessor != null) {
            audioProcessor.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAudioProcessing();
        } else {
            Toast.makeText(this, "يجب السماح بتسجيل الصوت!", Toast.LENGTH_LONG).show();
        }
    }
}
