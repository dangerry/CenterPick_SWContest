package ddwucom.contest.centerpick.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.location.Address;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import java.util.List;

import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.subway.MainActivity2;
import ddwucom.contest.centerpick.video.MyVideoView;

public class MainActivity extends AppCompatActivity {
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private ViewPager viewPager;
    //    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;

    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // making notification bar transparent
//        changeStatusBarColor();

        setContentView(R.layout.activity_main);

        //비디오 배경 설정
        MyVideoView mVideoView = (MyVideoView) findViewById(R.id.bgVideoView);
//        VideoView mVideoView = (VideoView) findViewById(R.id.bgVideoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sion2);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                Intent intent = new Intent(MainActivity.this, MapActivity.class);

                startActivity(intent);
                break;

            case R.id.btn_start2:
                intent = new Intent(MainActivity.this, MainActivity2.class);

                startActivity(intent);
                }
        }

    }
