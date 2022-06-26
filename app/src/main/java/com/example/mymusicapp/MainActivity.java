package com.example.mymusicapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements frag1.SendDataInterface {

    private TabLayout tabLayout;
    private TabItem tab1, tab2, tab3,tab4;
    private ViewPager viewPager;
    private pageAdapter pAdapter;
    private BottomSheetBehavior behavior;
    private LinearLayout lv;
    private ImageView nextBtn1,playBtn1,previousBtn,nextBtn,playBtn;
    private int play_pause_flag=0;
    private ConstraintLayout cl1,cl2;
    static MediaPlayer mediaPlayer;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cl1 = findViewById(R.id.constraint_layout1);
        cl2 = findViewById(R.id.constraint_layout2);
        tabLayout= findViewById(R.id.tabLayout1);
        tab1 = findViewById(R.id.Tab1);
        tab2 = findViewById(R.id.Tab2);
        tab3 = findViewById(R.id.Tab3);
        tab4 = findViewById(R.id.Tab4);
        nextBtn1= findViewById(R.id.NextBtn1);
        playBtn1 = findViewById(R.id.playBtn1);
        nextBtn = findViewById(R.id.NextBtn);
        playBtn = findViewById(R.id.PlayBtn);
        previousBtn=findViewById(R.id.PreviousBtn);
        lv = findViewById(R.id.linearLayout1);
        View bottomsheet = findViewById(R.id.behavior);
        behavior = BottomSheetBehavior.from(bottomsheet);

        behavior.setPeekHeight(160);
//        behavior.setHideable(true);
        viewPager = findViewById(R.id.viewPager);
        pAdapter= new pageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pAdapter);



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
        }else{
            playBtn1.setImageResource(R.drawable._695059_music_play_play_button_player_icon);
            playBtn.setImageResource(R.drawable._695059_music_play_play_button_player_icon);
            play_pause_flag=0;
        }
    }


    @Override
    public void SendData(ArrayList<File> files, int position) {
        if (mediaPlayer!=null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
        Uri uri = Uri.parse(files.get(position).toString());
       mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);

       mediaPlayer.start();
    }
}