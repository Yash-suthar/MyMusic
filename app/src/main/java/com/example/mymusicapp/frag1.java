package com.example.mymusicapp;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link frag1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class frag1 extends Fragment {
    String[] items;
    public static ArrayList<File> Song_Files;
    public static int position;
    SendDataInterface sendDataInterface;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public frag1() {
        // Required empty public constructor
    }

    public interface SendDataInterface{
        public void SendData(ArrayList<File> files,int position);
    }
    public static frag1 newInstance(String param1, String param2) {
        frag1 fragment = new frag1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag1, container, false);
        ListView listView = view.findViewById(R.id.SongListView);
        // Inflate the layout for this fragment
        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                ArrayList<File> all_songs = fetch_Song(Environment.getExternalStorageDirectory());
                Song_Files = all_songs;
                items = new String[all_songs.size()];

                for (int i = 0; i < all_songs.size(); i++) {
                    items[i] = all_songs.get(i).getName().toString().replace(".mp3", "");
//                    Log.d("stringtab", items[i]);
                }
//                ArrayAdapter<String> Adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,items);
                custome_Adapter customeAdapter = new custome_Adapter();
                listView.setAdapter(customeAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        position = i;
                        Toast.makeText(getContext(), "position clicked", Toast.LENGTH_SHORT).show();
                        sendDataInterface.SendData(all_songs,i);

                    }
                });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

        return view ;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try {
            sendDataInterface=(SendDataInterface) activity;
        }catch(RuntimeException a){
           throw new RuntimeException(activity.toString()+"Must implement");
        }
    }

    public ArrayList<File> fetch_Song(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if(files!=null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(fetch_Song(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3")) {
                        arrayList.add(singleFile);
                    }
                }

            }
        }


        return arrayList;
    }


//    public void makeListView() {
//        ArrayList<File> all_songs = fetch_Song(Environment.getExternalStorageDirectory());
//
//        items = new String[all_songs.size()];
//
//        for (int i = 0; i < all_songs.size(); i++) {
//            items[i] = all_songs.get(i).getName().toString().replace(".mp3", "");
//            Log.d("stringtab", items[i]);
//        }

//       custome_Adapter adapter = new custome_Adapter();
//
//        listView.setAdapter(adapter);

//    }

    public class custome_Adapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View views = getLayoutInflater().inflate(R.layout.list_view, null);
            TextView textView = views.findViewById(R.id.ListSongText);
            textView.setSelected(true);
            textView.setText(items[i]);

            return views;
        }
    }
    public class SongOperation {
        MediaPlayer mediaPlayer = new MediaPlayer();
        public SongOperation(){

        }
        Uri uri;
        public void playSong(Uri uri){
            this.uri = uri;
            if (mediaPlayer!=null){
                mediaPlayer.start();
                mediaPlayer.release();
            }

            mediaPlayer.start();

        }
        public void pauseSong(){

        }
    }
}



