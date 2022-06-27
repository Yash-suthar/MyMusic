package com.example.mymusicapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements frag1.SendDataInterface {

    private TabLayout tabLayout;
    private TabItem tab1, tab2, tab3,tab4;
    private ViewPager viewPager;
    private pageAdapter pAdapter;
    private BottomSheetBehavior behavior;
    private LinearLayout lv;
    private ImageView nextBtn1,playBtn1,previousBtn,nextBtn,playBtn,bottomImageView,musicIcon;
    private int play_pause_flag=0;
    private ConstraintLayout cl1,cl2;
    static MediaPlayer mediaPlayer;
    private int songPosition;
    private ArrayList<File> SongList;
    private SeekBar seekBar;
    TextView startTime, EndTime,bottomTextView;
    Handler handler;
    Runnable runnable;
    MediaMetadataRetriever retriever ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomImageView = findViewById(R.id.bottomImageView);
        bottomTextView = findViewById(R.id.bottomViewText);
        musicIcon = findViewById(R.id.musicIcon);
        cl1 = findViewById(R.id.constraint_layout1);
        cl2 = findViewById(R.id.constraint_layout2);
        tabLayout= findViewById(R.id.tabLayout1);
        tab1 = findViewById(R.id.Tab1);
        tab2 = findViewById(R.id.Tab2);
        tab3 = findViewById(R.id.Tab3);
        tab4 = findViewById(R.id.Tab4);
        startTime = findViewById(R.id.StartTime);
        EndTime = findViewById(R.id.EndTime);
        nextBtn1= findViewById(R.id.NextBtn1);
        playBtn1 = findViewById(R.id.playBtn1);
        nextBtn = findViewById(R.id.NextBtn);
        playBtn = findViewById(R.id.PlayBtn);
        previousBtn=findViewById(R.id.PreviousBtn);
        lv = findViewById(R.id.linearLayout1);
        View bottomsheet = findViewById(R.id.behavior);
        behavior = BottomSheetBehavior.from(bottomsheet);
        seekBar = findViewById(R.id.seekBar);
        behavior.setPeekHeight(160);
//        behavior.setHideable(true);
        viewPager = findViewById(R.id.viewPager);
        pAdapter= new pageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pAdapter);
        handler = new Handler();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (behavior.STATE_SETTLING == newState){
                    cl1.setVisibility(View.GONE);
                    cl2.setVisibility(View.VISIBLE);
                }
                if (behavior.STATE_EXPANDED == newState){
                    cl1.setVisibility(View.GONE);
                    cl2.setVisibility(View.VISIBLE);
                }
                if (behavior.STATE_DRAGGING == newState){
                    cl1.setVisibility(View.GONE);
                    cl2.setVisibility(View.VISIBLE);

                }
                if (behavior.STATE_COLLAPSED == newState){
                    cl1.setVisibility(View.VISIBLE);
                    cl2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        playBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               play_button_change();


            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_button_change();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTheSong(++songPosition);
            }
        });
        nextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTheSong(++songPosition);
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTheSong(--songPosition);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

//    public static void playSong(){
//        if (mediaPlayer!=null){
//            mediaPlayer.start();
//            mediaPlayer.release();
//        }
//        Uri uri = Uri.parse(frag1.Song_Files.get(frag1.position).toString());
//        mediaPlayer = MediaPlayer.create(,uri);
//        mediaPlayer.start();
//    }
    public void play_button_change(){
        if (play_pause_flag==0){
            playBtn1.setImageResource(R.drawable._695047_media_player_music_pause_player_icon);
            playBtn.setImageResource(R.drawable._695047_media_player_music_pause_player_icon);
            play_pause_flag=1;
            mediaPlayer.start();
        }else{
            playBtn1.setImageResource(R.drawable._695059_music_play_play_button_player_icon);
            playBtn.setImageResource(R.drawable._695059_music_play_play_button_player_icon);
            play_pause_flag=0;
            mediaPlayer.pause();
        }
    }

    public void StartTheSong(int position){
        if (mediaPlayer!=null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
        Uri uri = Uri.parse(SongList.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        setAlbum();
        bottomTextView.setText(SongList.get(position).getName().toString().replace(".mp3", ""));
        playBtn1.setImageResource(R.drawable._695047_media_player_music_pause_player_icon);
        playBtn.setImageResource(R.drawable._695047_media_player_music_pause_player_icon);
        play_pause_flag=1;
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                 updateSeekBar();
                 seekBar.setMax(mediaPlayer.getDuration());
                 EndTime.setText(""+TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration())+":"+TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration())%60);
            }
        });
    }
    @Override
    public void SendData(ArrayList<File> files, int position) {
        SongList = files;
        songPosition = position;
        StartTheSong(position);

    }

    private void setAlbum() {
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(SongList.get(songPosition).toString());

        byte [] data = retriever.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bottomImageView.setImageBitmap(bitmap); //associated cover art in bitmap
            musicIcon.setImageBitmap(bitmap);

        }else {
            bottomImageView.setImageResource(R.drawable._695055_headset_music_player_icon);
            musicIcon.setImageResource(R.drawable._695055_headset_music_player_icon);
        }
    }

    public void updateSeekBar(){
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        startTime.setText(""+TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition())+":"+TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())%60);
        if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()){
            StartTheSong(++songPosition);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar();
            }
        };
        handler.postDelayed(runnable,1000);
    }

    @Override
    public void onBackPressed() {

    if (behavior.getState() == behavior.STATE_EXPANDED){
        behavior.setState(behavior.STATE_COLLAPSED);
    }else{
        super.onBackPressed();
    }
    }
}