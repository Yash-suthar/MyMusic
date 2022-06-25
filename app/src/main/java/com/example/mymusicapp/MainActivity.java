package com.example.mymusicapp;

import static android.R.anim.fade_in;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabItem tab1, tab2, tab3,tab4;
    private ViewPager viewPager;
    private pageAdapter pAdapter;
    private BottomSheetBehavior behavior;
    private LinearLayout lv;
    private ImageView nextBtn1,playBtn1,previousBtn,nextBtn,playBtn;
    private int play_pause_flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        behavior.setPeekHeight(0);
        behavior.setHideable(true);
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
        playBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }


}