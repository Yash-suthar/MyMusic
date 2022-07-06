package com.example.mymusicapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class MainActivity extends AppCompatActivity implements frag1.SendDataInterface {

    private TabLayout tabLayout;
    private TabItem tab1, tab2, tab3,tab4;
    private ViewPager viewPager;
    private pageAdapter pAdapter;
    private BottomSheetBehavior behavior;
    private LinearLayout lv;
    private ImageView nextBtn1,playBtn1,previousBtn,nextBtn,playBtn,bottomImageView,musicIcon,loopList,sync_menu;
    private int play_pause_flag=0,loop_flag=0;
    private ConstraintLayout cl1,cl2,cl;
    static MediaPlayer mediaPlayer;
    public static int songPosition,seekbarPosition;
    private ArrayList<File> SongList;
    private SeekBar seekBar;
    TextView startTime, EndTime,bottomTextView,bottomTextView1;
    Handler handler;
    Runnable runnable;
    MediaMetadataRetriever retriever ;
    frag1.RecyclerViewAdapter recyclerViewAdapter;
    BlurView blurView;
    Set<String> songList;

    @Override
    protected void onPause() {
        SharedPreferences sharedPreferences = getSharedPreferences("demo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("songPosition",songPosition);
        if (mediaPlayer!=null) {
            editor.putInt("seekbarPosition", mediaPlayer.getCurrentPosition());
        }else{
            editor.putInt("seekbarPosition",0);
        }
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blurView = findViewById(R.id.BlurView);
        bottomImageView = findViewById(R.id.bottomImageView);
        bottomTextView = findViewById(R.id.bottomViewText);
        bottomTextView1 = findViewById(R.id.bottomViewText1);
        sync_menu = findViewById(R.id.sync);
        loopList = findViewById(R.id.loopList);
        musicIcon = findViewById(R.id.musicIcon);
        cl = findViewById(R.id.behavior);
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
        blurbackground();

        AccessibilityService applicationContext = null;
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String ipAddress = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        Log.d("wifiip",ipAddress);

        sync_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    TcpConnectionManager tc = new TcpConnectionManager(InetAddress.getByName("192.168.64.227"));
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
                TcpConnectionManager tc = new TcpConnectionManager();
            }
        });
        loopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loop_flag == 0){
                    loopList.setImageResource(R.drawable.ic_baseline_repeat_one_24);
                    loop_flag=1;
                }else if (loop_flag==1){
                    loopList.setImageResource(R.drawable.ic_baseline_shuffle_24);
                    loop_flag=2;
                }else{
                    loopList.setImageResource(R.drawable.ic_baseline_repeat_24);
                    loop_flag=0;
                }
            }
        });

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
        viewPager.setOffscreenPageLimit(4);
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
                if (songPosition==SongList.size()-1){
                    songPosition=0;
                    StartTheSong(songPosition,false);
                }else{
                    StartTheSong(++songPosition,false);
                }
            }
        });
        nextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songPosition==SongList.size()-1){
                    songPosition=0;
                    StartTheSong(songPosition,false);
                }else{
                    StartTheSong(++songPosition,false);
                }

            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songPosition==0){
                    songPosition=SongList.size()-1;
                    StartTheSong(songPosition,false);
                }else{
                    StartTheSong(--songPosition,false);
                }

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

    private void blurbackground() {
        float radius = 20f;

        View decorView = getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);
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
            playBtn1.setImageResource(R.drawable.pause30dp);
            playBtn.setImageResource(R.drawable.pausebutton);
            play_pause_flag=1;
            mediaPlayer.start();
        }else{
            playBtn1.setImageResource(R.drawable.play30dp);
            playBtn.setImageResource(R.drawable.playbutton);
            play_pause_flag=0;
            mediaPlayer.pause();
        }
    }

    public void StartTheSong(int position,boolean flag){
        if (mediaPlayer!=null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
        if (recyclerViewAdapter!=null){
            recyclerViewAdapter.notifyDataSetChanged();
        }

//        recyclerViewAdapter.notifyItemChanged(songPosition);

        Uri uri = Uri.parse(SongList.get(position).toString());
        Log.d("mtagh",uri.toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        playBtn1.setImageResource(R.drawable.pause30dp);
        playBtn.setImageResource(R.drawable.pausebutton);
        play_pause_flag = 1;
        bottomTextView.setText(SongList.get(position).getName().toString().replace(".mp3", ""));
        bottomTextView1.setText(SongList.get(position).getName().toString().replace(".mp3", ""));
        setAlbum();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (flag){
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(seekbarPosition);
                    playBtn.setImageResource(R.drawable.playbutton);
                    playBtn1.setImageResource(R.drawable.play30dp);
                }
                 updateSeekBar();
                 seekBar.setMax(mediaPlayer.getDuration());
                 EndTime.setText(""+TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration())+":"+TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration())%60);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (loop_flag==0){
                    if (songPosition==SongList.size()-1){
                        songPosition=0;
                        StartTheSong(songPosition,false);
                    }else{
                        StartTheSong(++songPosition,false);
                    }

                }else if(loop_flag==1){
                    StartTheSong(songPosition,false);
                }else{
                    Random random = new Random();
                    songPosition = random.nextInt(SongList.size());
                    StartTheSong(songPosition,false);
                }
            }
        });
    }
    @Override
    public void SendData(ArrayList<File> files, int position, boolean flag, frag1.RecyclerViewAdapter recycler) {
        SongList = files;
        recyclerViewAdapter=recycler;
//        Log.d("mtagh",SongList.toString());
        SharedPreferences sharedPreferences = getSharedPreferences("demo",MODE_PRIVATE);
        songPosition =sharedPreferences.getInt("songPosition",0);
        Log.d("mtagh",songPosition+"");
        seekbarPosition=sharedPreferences.getInt("seekbarPosition",0);
        Log.d("mtagh",seekbarPosition+"");
        if (flag){
            songPosition = position;
            StartTheSong(songPosition,false);
        }else{
            StartTheSong(songPosition,true);
        }




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
            Drawable dr = new BitmapDrawable(bitmap);
            cl.setBackground(dr);
        }else {
            bottomImageView.setImageResource(R.drawable._695055_headset_music_player_icon);
            musicIcon.setImageResource(R.drawable._695055_headset_music_player_icon);
        }
    }

    public void updateSeekBar(){
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        startTime.setText(""+TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition())+":"+TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())%60);
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
        moveTaskToBack(true);

    }
    }


}