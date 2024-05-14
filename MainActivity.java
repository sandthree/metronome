package com.example.newmetro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SoundPool soundPool;
    private int beat;
    private Timer timer;
    private TimerTask task;
    private int hantei;
    private int bpm;
    private int num;
    TextView textView;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.bpm);
        seekBar = findViewById(R.id.seekBar);
        textView.setText(Integer.toString(seekBar.getProgress()));
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());

        //soundPoolに必要なAudioAttributesの呼び出し
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        //soundPoolの生成
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        //音源の呼び出し
        beat = soundPool.load(this,R.raw.beat, 1);

        //音声ファイルがロードされてないのに再生されることを防ぐために初期設定ではボタンをfalseにしておく
        //再生可能かの判定ができるようにhanteiに0を入れる
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            Button button = findViewById(R.id.startButton);
            button.setEnabled(true);
            hantei = 0;
        });

    }

    public void onClick(View view){

        Button button = findViewById(R.id.startButton);
        int id = view.getId();

        //処理を分岐させるための条件
        // 停止(hantei == 0)してたら再生できる
        // 再生(hantei == 1)してたら停止できる
        switch(id){
            case R.id.startButton:

                if(hantei == 0) {
                    play();
                    button.setText("■");
                    hantei = 1;
                }else if(hantei == 1){
                    timer.cancel();
                    button.setText("▶");
                    hantei =0;
                }
                break;

            case R.id.saveButton:

                Intent intent = new Intent(MainActivity.this, LIstActivity1.class);
                intent.putExtra("bpm",seekBar.getProgress());
                startActivity(intent);

                Toast.makeText(MainActivity.this,seekBar.getProgress() + "を保存しました",Toast.LENGTH_LONG).show();

                break;
        }

    }

    //soundPoolの解放
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        if(timer != null){
        timer.purge();
        }
    }

    public void play(){
        num = seekBar.getProgress();
        bpm = (60 * 1000)/num;


        //timerの生成
        //サウンドプールを再生するタスクの生成
        task = new TimerTask() {
            @Override
            public void run() {
                soundPool.play(beat,1,1,1,0,1);
            }
        };

        //timerの生成
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, (long)bpm);
    }

    //SeekBarのリスナクラス
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            textView.setText(String.valueOf(i));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //再生されていた時のみ
            if(hantei == 1) {
                timer.cancel();
                timer.purge();
                play();
            }
        }
    }

}
