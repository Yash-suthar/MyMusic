package com.example.mymusicapp;


import static android.content.ContentValues.TAG;
import static android.view.View.GONE;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class MainActivity extends AppCompatActivity implements frag1.SendDataInterface {
    private TabLayout tabLayout;
    private TabItem tab1, tab2, tab3, tab4;
    private ViewPager viewPager;
    private pageAdapter pAdapter;
    private BottomSheetBehavior behavior;
    private LinearLayout lv;
    private ImageView nextBtn1, playBtn1, previousBtn, nextBtn, playBtn, bottomImageView, musicIcon, loopList, sync, menu_bar;
    private int play_pause_flag = 0, loop_flag = 0, sync_flag = 0, port1 = 8000,port2 =8100,port3=8200,port4=8300;
    private ConstraintLayout cl1, cl2, cl;
    static MediaPlayer mediaPlayer;
    public static int songPosition, seekbarPosition;
    private ArrayList<File> SongList;
    private SeekBar seekBar;
    TextView startTime, EndTime, bottomTextView, bottomTextView1, connected_device_name;
    Handler handler;
    Runnable runnable;
    MediaMetadataRetriever retriever;
    frag1.RecyclerViewAdapter recyclerViewAdapter;
    BlurView blurView;
    Toolbar toolbar;
    WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WIFIBroadCastReceiver mbroadcast;
    IntentFilter intentFilter;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    private AutoCompleteTextView autoCompleteTextView;
    private CoordinatorLayout devicesListLayout;
    Socket hostSong,clientSong,hostObject,clientObject;
    boolean device_connected_flag, sendMode;
    ArrayList<File> tempSong = new ArrayList<File>();

//    InputStream JSONinput_stream;
//    OutputStream JSONoutput_stream;
//    ObjectInputStream JSON_Object_input_stream;
//    ObjectOutputStream JSON_Object_output_stream;
//    receive_status receiveStatus;
    @Override
    protected void onPause() {
        SharedPreferences sharedPreferences = getSharedPreferences("demo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("songPosition", songPosition);
        if (mediaPlayer != null) {
            editor.putInt("seekbarPosition", mediaPlayer.getCurrentPosition());
        } else {
            editor.putInt("seekbarPosition", 0);
        }
        editor.apply();
        unregisterReceiver(mbroadcast);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connected_device_name = findViewById(R.id.device_name);
        devicesListLayout = findViewById(R.id.wifilistlayout);
        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        blurView = findViewById(R.id.BlurView);
        toolbar = findViewById(R.id.toolbar);
        bottomImageView = findViewById(R.id.bottomImageView);
        bottomTextView = findViewById(R.id.bottomViewText);
        bottomTextView1 = findViewById(R.id.bottomViewText1);
        sync = findViewById(R.id.sync);
        menu_bar = findViewById(R.id.menu_bar);
        loopList = findViewById(R.id.loopList);
        musicIcon = findViewById(R.id.musicIcon);
        cl = findViewById(R.id.behavior);
        cl1 = findViewById(R.id.constraint_layout1);
        cl2 = findViewById(R.id.constraint_layout2);
        tabLayout = findViewById(R.id.tabLayout1);
        tab1 = findViewById(R.id.Tab1);
        tab2 = findViewById(R.id.Tab2);
        tab3 = findViewById(R.id.Tab3);
        tab4 = findViewById(R.id.Tab4);
        startTime = findViewById(R.id.StartTime);
        EndTime = findViewById(R.id.EndTime);
        nextBtn1 = findViewById(R.id.NextBtn1);
        playBtn1 = findViewById(R.id.playBtn1);
        nextBtn = findViewById(R.id.NextBtn);
        playBtn = findViewById(R.id.PlayBtn);
        previousBtn = findViewById(R.id.PreviousBtn);
        lv = findViewById(R.id.linearLayout1);
        View bottomsheet = findViewById(R.id.behavior);
        behavior = BottomSheetBehavior.from(bottomsheet);
        seekBar = findViewById(R.id.seekBar);
        behavior.setPeekHeight(160);
        viewPager = findViewById(R.id.viewPager);
        pAdapter = new pageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pAdapter);
        handler = new Handler();
        blurbackground();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(getApplicationContext(), getMainLooper(), null);
        mbroadcast = new WIFIBroadCastReceiver(wifiP2pManager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sync_flag == 0) {
                    if (wifiManager.isWifiEnabled()) {
                        wifiP2pManager.removeGroup(channel, null);
                        Log.d("wifip2p", "hellow wifi on");
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "giver location permission", Toast.LENGTH_SHORT).show();
                        }
                        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d("wifip2p", "starting disovering");
                                Toast.makeText(MainActivity.this, "starting descovering", Toast.LENGTH_SHORT).show();
                                sync.setVisibility(GONE);
                                devicesListLayout.setVisibility(View.VISIBLE);
                                sync.setImageResource(R.drawable.ic_baseline_sync_disabled_24);
                                sync_flag = 1;
                            }

                            @Override
                            public void onFailure(int i) {
                                Toast.makeText(MainActivity.this, "descovering failed", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {

                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                        if (!wifiManager.isWifiEnabled()) {
                            Intent intent1 = new Intent();
                            intent1.setClassName("com.android.settings", "com.android.settings.TetherSettings");
                            startActivity(intent1);
                        }
                    }


                } else {
                    if (wifiP2pManager != null && channel != null) {
                        wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                            @Override
                            public void onGroupInfoAvailable(WifiP2pGroup group) {
                                if (group != null && wifiP2pManager != null && channel != null) {
                                    wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "removeGroup onSuccess -");
//                                            sync.setImageResource(R.drawable.ic_baseline_sync_24);
//                                            connected_device_name.setVisibility(GONE);
//                                            sync_flag=0;
                                        }

                                        @Override
                                        public void onFailure(int reason) {
                                            Log.d(TAG, "removeGroup onFailure -" + reason);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
        loopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loop_flag == 0) {
                    loopList.setImageResource(R.drawable.ic_baseline_repeat_one_24);
                    loop_flag = 1;
                } else if (loop_flag == 1) {
                    loopList.setImageResource(R.drawable.ic_baseline_shuffle_24);
                    loop_flag = 2;
                } else {
                    loopList.setImageResource(R.drawable.ic_baseline_repeat_24);
                    loop_flag = 0;
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
                if (behavior.STATE_SETTLING == newState) {
                    cl1.setVisibility(GONE);
                    cl2.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (behavior.STATE_EXPANDED == newState) {
                    cl1.setVisibility(GONE);
                    cl2.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(GONE);
                }
                if (behavior.STATE_DRAGGING == newState) {
                    cl1.setVisibility(GONE);
                    cl2.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);

                }
                if (behavior.STATE_COLLAPSED == newState) {
                    cl1.setVisibility(View.VISIBLE);
                    cl2.setVisibility(GONE);
                    toolbar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        playBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (device_connected_flag ){
                          Log.d("server","non sender button clicked");
                          if (sendMode){
                              JSONsender(hostObject);
                          }else{
                              JSONsender(clientObject);
                          }
                }
                else{
                    play_button_change();
                }


            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (device_connected_flag ){
                    if (sendMode){
                        JSONsender(hostObject);
                    }else{
                        JSONsender(clientObject);
                    }
                }
                else{
                    play_button_change();
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songPosition == SongList.size() - 1) {
                    songPosition = 0;
                    StartTheSong(SongList.get(songPosition), false);
                } else {
                    StartTheSong(SongList.get(++songPosition), false);
                }
            }
        });
        nextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songPosition == SongList.size() - 1) {
                    songPosition = 0;
                    StartTheSong(SongList.get(songPosition), false);
                } else {
                    StartTheSong(SongList.get(++songPosition), false);
                }

            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songPosition == 0) {
                    songPosition = SongList.size() - 1;
                    StartTheSong(SongList.get(songPosition), false);
                } else {
                    StartTheSong(SongList.get(--songPosition), false);
                }

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
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
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                devicesListLayout.setVisibility(GONE);
                wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
//                        device_connected_flag = true;
                        connected_device_name.setText(device.deviceName);
                        connected_device_name.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(MainActivity.this, "connection failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
                if (peers.size() == 0) {
                    Toast.makeText(MainActivity.this, "device not found", Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> adapterItem = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                autoCompleteTextView.setAdapter(adapterItem);

            }

        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            devicesListLayout.setVisibility(GONE);
            sync.setVisibility(View.VISIBLE);
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
//            Log.d("server", groupOwnerAddress.getHostAddress());
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                sendMode = true;
                Host1 host1 = new Host1();
                host1.execute();
                Host2 host2 = new Host2();
                host2.execute();
                Host3 host3 = new Host3();
                host3.execute();
                Host4 host4 = new Host4();
                host4.execute();
                Toast.makeText(MainActivity.this, "you are host", Toast.LENGTH_SHORT).show();
            } else if (wifiP2pInfo.groupFormed) {
                sendMode = false;
                Client1 clientSocket1 = new Client1();
                clientSocket1.execute(groupOwnerAddress);
                Client2 clientSocket2 = new Client2();
                clientSocket2.execute(groupOwnerAddress);
                Client3 clientSocket3 = new Client3();
                clientSocket3.execute(groupOwnerAddress);
                Client4 clientSocket4 = new Client4();
                clientSocket4.execute(groupOwnerAddress);

                Toast.makeText(MainActivity.this, "you are client", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void ondisconnect() {
        sync.setImageResource(R.drawable.ic_baseline_sync_24);
        connected_device_name.setVisibility(GONE);
        sync_flag = 0;
        device_connected_flag = false;
    }

    private void blurbackground() {
        float radius = 20f;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);
    }


    public void play_button_change() {
        if (play_pause_flag == 0) {
            playBtn1.setImageResource(R.drawable.pause30dp);
            playBtn.setImageResource(R.drawable.pausebutton);
            play_pause_flag = 1;
            mediaPlayer.start();
        } else {
            playBtn1.setImageResource(R.drawable.play30dp);
            playBtn.setImageResource(R.drawable.playbutton);
            play_pause_flag = 0;
            mediaPlayer.pause();
        }
    }

//    public void StartTheSong(int position, boolean flag) {
//        if (mediaPlayer != null) {
//            mediaPlayer.start();
//            mediaPlayer.release();
//        }
//        if (recyclerViewAdapter != null) {
//            recyclerViewAdapter.notifyDataSetChanged();
//        }
////        if (sendMode){
////            SendSong sendSong = new SendSong();
////            sendSong.execute(position);
////        }
//        Uri uri;
////        recyclerViewAdapter.notifyItemChanged(songPosition);
//        if (device_connected_flag && !sendMode) {
//            uri = Uri.parse(tempSong.get(position).toString());
//            Log.d("server", "" + uri);
//        } else {
//
//            uri = Uri.parse(SongList.get(position).toString());
//            Log.d("server", "" + uri);
//        }
//        Log.d("mtagh", uri.toString());
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//        mediaPlayer.start();
//        playBtn1.setImageResource(R.drawable.pause30dp);
//        playBtn.setImageResource(R.drawable.pausebutton);
//        play_pause_flag = 1;
//        if (device_connected_flag && !sendMode) {
//            bottomTextView.setText(tempSong.get(position).getName().toString().replace(".mp3", ""));
//            bottomTextView1.setText(tempSong.get(position).getName().toString().replace(".mp3", ""));
//        } else {
//            bottomTextView.setText(SongList.get(position).getName().toString().replace(".mp3", ""));
//            bottomTextView1.setText(SongList.get(position).getName().toString().replace(".mp3", ""));
//        }
//        setAlbum();
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                if (flag || device_connected_flag) {
//                    mediaPlayer.pause();
//                    if (flag) {
//                        mediaPlayer.seekTo(seekbarPosition);
//                    }
//                    playBtn.setImageResource(R.drawable.playbutton);
//                    playBtn1.setImageResource(R.drawable.play30dp);
//                    play_pause_flag=0;
//                    if (!sendMode && device_connected_flag) {
//
//                            JSONsender(clientObject);
//
//                    }
//                }
//
//                updateSeekBar();
//                seekBar.setMax(mediaPlayer.getDuration());
//                EndTime.setText("" + TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()) + ":" + TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) % 60);
//            }
//
//
//        });
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                if (loop_flag == 0) {
//                    if (songPosition == SongList.size() - 1) {
//                        songPosition = 0;
//                        StartTheSong(songPosition, false);
//                    } else {
//                        StartTheSong(++songPosition, false);
//                    }
//
//                } else if (loop_flag == 1) {
//                    StartTheSong(songPosition, false);
//                } else {
//                    Random random = new Random();
//                    songPosition = random.nextInt(SongList.size());
//                    StartTheSong(songPosition, false);
//                }
//            }
//        });
//    }
    public void StartTheSong(File file, boolean flag) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.release();
        }
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        }
//        if (sendMode){
//            SendSong sendSong = new SendSong();
//            sendSong.execute(position);
//        }

//        recyclerViewAdapter.notifyItemChanged(songPosition);
        Uri uri = Uri.parse(file.toString());
        Log.d("server", "" + uri);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        playBtn1.setImageResource(R.drawable.pause30dp);
        playBtn.setImageResource(R.drawable.pausebutton);
        play_pause_flag = 1;
        bottomTextView.setText(file.getName().toString().replace(".mp3", ""));
        bottomTextView1.setText(file.getName().toString().replace(".mp3", ""));
        setAlbum(file);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (flag || device_connected_flag) {
                    mediaPlayer.pause();
                    if (flag) {
                        mediaPlayer.seekTo(seekbarPosition);
                    }
                    playBtn.setImageResource(R.drawable.playbutton);
                    playBtn1.setImageResource(R.drawable.play30dp);
                    play_pause_flag=0;
                    if (!sendMode && device_connected_flag) {

                        JSONsender(clientObject);

                    }
                }

                updateSeekBar();
                seekBar.setMax(mediaPlayer.getDuration());
                EndTime.setText("" + TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()) + ":" + TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) % 60);
            }


        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (loop_flag == 0) {
                    if (songPosition == SongList.size() - 1) {
                        songPosition = 0;
                        StartTheSong(SongList.get(songPosition), false);
                    } else {
                        StartTheSong(SongList.get(++songPosition), false);
                    }

                } else if (loop_flag == 1) {
                    StartTheSong(SongList.get(songPosition), false);
                } else {
                    Random random = new Random();
                    songPosition = random.nextInt(SongList.size());
                    StartTheSong(SongList.get(songPosition), false);
                }
            }
        });
    }

    private void JSONsender(Socket socket){
        SerializableObject ob = new SerializableObject();
        ob.songName="hello this is my mp3";
        ob.SeekbarPosition = mediaPlayer.getCurrentPosition();
        Log.d("server",mediaPlayer.getCurrentPosition()+"");
        ob.play_pause_flag = play_pause_flag;
        send_status sendStatus = new send_status(socket);
        sendStatus.execute(ob);
        Log.d("server","object sending");
    }

    private void JSONreceiver(SerializableObject ob){

        mediaPlayer.seekTo(ob.SeekbarPosition);
        if (ob.play_pause_flag==play_pause_flag){
            play_button_change();
        }
        bottomTextView.setText(ob.songName);
        bottomTextView1.setText(ob.songName);
    }


    @Override
    public void SendData(ArrayList<File> files, int position, boolean flag, frag1.RecyclerViewAdapter recycler) {
        SongList = files;
        recyclerViewAdapter = recycler;
//        Log.d("mtagh",SongList.toString());
        SharedPreferences sharedPreferences = getSharedPreferences("demo", MODE_PRIVATE);
        songPosition = sharedPreferences.getInt("songPosition", 0);
        Log.d("mtagh", songPosition + "");
        seekbarPosition = sharedPreferences.getInt("seekbarPosition", 0);
        Log.d("mtagh", seekbarPosition + "");
        if (sendMode || !device_connected_flag) {
            if (flag) {
                songPosition = position;
                StartTheSong(SongList.get(songPosition), false);
            } else {
                StartTheSong(SongList.get(songPosition), true);
            }
        }


    }


    private void setAlbum(File file) {
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.toString());
        byte[] data = retriever.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bottomImageView.setImageBitmap(bitmap); //associated cover art in bitmap
            musicIcon.setImageBitmap(bitmap);
            Drawable dr = new BitmapDrawable(bitmap);
            cl.setBackground(dr);
        } else {
            bottomImageView.setImageResource(R.drawable._695055_headset_music_player_icon);
            musicIcon.setImageResource(R.drawable._695055_headset_music_player_icon);
            cl.setBackground(getDrawable(R.drawable._695055_headset_music_player_icon));
        }
    }

    public void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        startTime.setText("" + TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition()) + ":" + TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition()) % 60);
        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar();
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onBackPressed() {

        if (behavior.getState() == behavior.STATE_EXPANDED) {
            behavior.setState(behavior.STATE_COLLAPSED);
        } else {
            moveTaskToBack(true);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mbroadcast, intentFilter);
    }





















    public class SendSong extends AsyncTask<Integer, Void, Void> {
        Socket socket;
       public SendSong(Socket socket){
            this.socket =socket;
        }

        @Override
        protected Void doInBackground(Integer... ints) {
            try {
                OutputStream outputStream =  socket.getOutputStream();
//                Log.d("server", "socket stream output");
                FileInputStream fi = new FileInputStream(SongList.get(ints[0]));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
//                Log.d("server", "file stream created");
                byte[] buf = new byte[(int) SongList.get(ints[0]).length()];
                while (fi.read(buf) > 0) {

//                    Log.d("server", "file sending");
//                    outputStream.write(buf, 0, buf.length);
                    bufferedOutputStream.write(buf,0, buf.length);

                }
                Log.d("server", "" + SongList.get(ints[0]).length());
                bufferedOutputStream.close();
                outputStream.flush();
//                outputStream.close();
                fi.close();
                Log.d("server", "song sended");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class ReceiveSong extends AsyncTask<Socket, Void, File> {

        @Override
        protected File doInBackground(Socket... sockets) {

//                    outputStream = client_socket.getOutputStream();
            File file = null;
            try {

                    InputStream inputStream = sockets[0].getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    file = File.createTempFile("playnow", ".mp3", getApplicationContext().getCacheDir());
                    Log.d("server", "file created");
                    FileOutputStream fis = new FileOutputStream(file);
//                Log.d("server","stream writed");
                    byte[] buf = new byte[1024];
                    int offset;
                    while ((offset = bufferedInputStream.read(buf)) != -1) {
//                    Log.d("server", "" + offset);
//                    Log.d("server", "writing file");
                        fis.write(buf, 0, offset);
                    }
                    fis.flush();
                    Log.d("server", "" + file.length());
                    Log.d("server", "songreceived");
//                    StartTheSong(file, true);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        }


        @Override
        protected void onPostExecute(File file) {
            StartTheSong(file, true);
        }
    }





    public class Client1 extends AsyncTask<InetAddress, Void, Void> {

        @Override
        protected Void doInBackground(InetAddress... inetAddresses) {

            try {
                hostSong = new Socket();
                hostSong.connect(new InetSocketAddress(inetAddresses[0], port1), 20000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void unused) {
            ReceiveSong receiveSong = new ReceiveSong();
            receiveSong.e
        }
    }
    public class Client2 extends AsyncTask<InetAddress, Void, Void> {

        @Override
        protected Void doInBackground(InetAddress... inetAddresses) {

            try {
                clientSong = new Socket();
                clientSong.connect(new InetSocketAddress(inetAddresses[0], port2), 20000);
                Log.d("server", "clientSong socket Connected!");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

    }
    public class Client3 extends AsyncTask<InetAddress, Void, Void> {

        @Override
        protected Void doInBackground(InetAddress... inetAddresses) {

            try {
                hostObject = new Socket();
                hostObject.connect(new InetSocketAddress(inetAddresses[0], port3), 20000);
                Log.d("server", "hostObject socket Connected!");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

    }
    public class Client4 extends AsyncTask<InetAddress, Void, Void> {

        @Override
        protected Void doInBackground(InetAddress... inetAddresses) {

            try {
                clientObject = new Socket();
                clientObject.connect(new InetSocketAddress(inetAddresses[0], port4), 20000);
                Log.d("server", "clientSong socket Connected!");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

    }




    public class Host1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(port1);
                Log.d("server", "server lestening");
                hostSong = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class Host2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(port2);

                Log.d("server", "hostSong socket connected!");
                clientSong = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class Host3 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(port3);
                Log.d("server", "server lestening");
                hostObject = serverSocket.accept();
                Log.d("server", "hostObject socket connected!");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class Host4 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(port4);
                Log.d("server", "server lestening");
                clientObject = serverSocket.accept();
                Log.d("server","clientObject socket connected!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }






















//    public class JSONClientSocket extends AsyncTask<InetAddress,Void,Socket> {
//
//        @Override
//        protected Socket doInBackground(InetAddress... inetAddresses) {
//            try {
//                JSONclient_socket = new Socket();
//                JSONclient_socket.connect(new InetSocketAddress(inetAddresses[0],port), 20000);
//                JSONoutput_stream = JSONclient_socket.getOutputStream();
//
//
//                Log.d("server", "JSONconnected with" + inetAddresses[0].getHostAddress());
////                if (sendMode) {
////                    receive_status();
////                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return JSONclient_socket;
//        }
//
//        @Override
//        protected void onPostExecute(Socket socket) {
//             receive_status receiveStatus = new receive_status();
//            receiveStatus.execute();
//
//        }
//    }
    public class send_status extends AsyncTask<SerializableObject,Void,Void>{
        Socket socket;
        public send_status(Socket socket){
            this.socket = socket;
        }

        @Override
        protected Void doInBackground(SerializableObject... serializableObjects) {

            try { Log.d("server","status sending");

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                Log.d("server",serializableObjects[0].SeekbarPosition+"this is seekbar position");
                outputStream.writeObject(serializableObjects[0]);
                Log.d("server","status sended");
                outputStream.flush();

                } catch (IOException ioException) {
                ioException.printStackTrace();
            }
//                JSON_Object_output_stream.flush();
//                outputStream.close();


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            play_button_change();
        }
    }
    public class receive_status extends AsyncTask <Socket,SerializableObject,SerializableObject>{
        @Override
        protected void onProgressUpdate(SerializableObject... values) {
            JSONreceiver(values[0]);
        }

        @Override
        protected SerializableObject doInBackground(Socket... sockets) {
                SerializableObject ob = new SerializableObject();
                try {


                    while (sockets[0]!=null) {
                        ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(sockets[0].getInputStream()));
                        Log.d("server", "starting to receiving");
                        ob = (SerializableObject) inputStream.readObject();
                        Log.d("server",""+ob.SeekbarPosition);
                        publishProgress(ob);
//                        mediaPlayer.seekTo(ob.SeekbarPosition);
//                        if (play_pause_flag == ob.play_pause_flag){
//                            play_button_change();
//                        }
//                        bottomTextView.setText(ob.songName);
//                        bottomTextView1.setText(ob.songName);
//                        if(isCancelled())
//                            break;
                        Log.d("server", ob.SeekbarPosition + "");
                    }
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }

            return ob;
        }

    }

}