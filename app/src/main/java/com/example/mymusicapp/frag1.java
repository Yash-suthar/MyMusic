package com.example.mymusicapp;

import static android.view.View.GONE;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.w3c.dom.Text;

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
    RecyclerView listView;
    //    public static int changerowindex = MainActivity.songPosition;
//    public static int position;
    private RecyclerViewAdapter recyclerViewAdapter;
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


    public interface SendDataInterface {
        public void SendData(ArrayList<File> files, int position, boolean flag, RecyclerViewAdapter recycler);
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


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag1, container, false);
        RecyclerView listView = view.findViewById(R.id.SongListView);
        TextView SongNotFound = view.findViewById(R.id.SongNotFound);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        // Inflate the layout for this fragment

        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                listView.setAdapter(recyclerViewAdapter);
                Asynctask at = new Asynctask();
                at.dothis(listView,SongNotFound);
                at.execute();
//                ArrayAdapter<String> Adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,items);
//                custome_Adapter customeAdapter = new custome_Adapter();
//                listView.setAdapter(customeAdapter);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        position = i;
//                        Toast.makeText(getContext(), "position clicked", Toast.LENGTH_SHORT).show();
//                        sendDataInterface.SendData(all_songs,i);
//
//                    }
//                });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
        return view;
    }


    private void dothhis(RecyclerView listView, TextView songNotFound){
        Thread thread = new Thread(){
            @Override
            public void run() {
                ArrayList<File> all_songs = fetch_Song(Environment.getExternalStorageDirectory());
                if (!all_songs.isEmpty()) {
                    Song_Files = all_songs;
                    sendDataInterface.SendData(all_songs, 0, false, recyclerViewAdapter);
                    items = new String[all_songs.size()];

                    for (int i = 0; i < all_songs.size(); i++) {
                        items[i] = all_songs.get(i).getName().toString().replace(".mp3", "");
//                    Log.d("stringtab", items[i]);
                    }
                    recyclerViewAdapter = new RecyclerViewAdapter(getContext(), Song_Files);
                    listView.setAdapter(recyclerViewAdapter);


                }else{
                    listView.setVisibility(GONE);
                    songNotFound.setVisibility(View.VISIBLE);
                }
            }
        };
        thread.start();

    }

    public class Asynctask extends AsyncTask<Void, Void, Void>  {
        RecyclerView listview;
        TextView textView;
        public void dothis(RecyclerView listview,TextView textView){
            this.listview =listview;
            this.textView = textView;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<File> all_songs = fetch_Song(Environment.getExternalStorageDirectory());
            Song_Files = all_songs;
//            if (!all_songs.isEmpty()) {
//
//
//                items = new String[all_songs.size()];
//
//                for (int i = 0; i < all_songs.size(); i++) {
//                    items[i] = all_songs.get(i).getName().toString().replace(".mp3", "");
////                    Log.d("stringtab", items[i]);
//                }
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (!Song_Files.isEmpty()){
                sendDataInterface.SendData(Song_Files, 0, false, recyclerViewAdapter);
                recyclerViewAdapter = new RecyclerViewAdapter(getContext(), Song_Files);
                listview.setAdapter(recyclerViewAdapter);
            } else{
                listview.setVisibility(GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            sendDataInterface = (SendDataInterface) activity;
        } catch (RuntimeException a) {
            throw new RuntimeException(activity.toString() + "Must implement");
        }
    }

    public ArrayList<File> fetch_Song(File file) {
//        Log.d("mtag","functioncalled");
//        Log.d("mtag",file.toString());
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden() && !singleFile.getName().startsWith(".")) {
//                    Log.d("mtagh",singleFile.getName());
                    arrayList.addAll(fetch_Song(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3")) {

                       if (singleFile.length()>2024){
//                           Log.d("mtagh",singleFile.length()+"");
                        arrayList.add(singleFile);
                    }
                    }
                }

            }
        }


        return arrayList;
    }



    public class RecyclerViewAdapter extends RecyclerView.Adapter {
        int row_index = MainActivity.songPosition;
        public Context context;
        public ArrayList<File> songList;

        public RecyclerViewAdapter(Context context, ArrayList<File> song_files) {
            this.context = context;
            this.songList = song_files;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView Listsongtext = holder.itemView.findViewById(R.id.ListSongText);
            Listsongtext.setText(songList.get(position).getName().toString().replace(".mp3", ""));
            RelativeLayout relativeLayout = holder.itemView.findViewById(R.id.relativeLayout);
            if (MainActivity.songPosition == position) {
                relativeLayout.setBackgroundResource(R.color.white);
            } else {
                relativeLayout.setBackgroundResource(R.color.black);
            }
        }

        @Override
        public int getItemCount() {
            return songList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
//            row_index = this.getPosition();
                sendDataInterface.SendData(songList, this.getPosition(), true, recyclerViewAdapter);
                notifyDataSetChanged();
            }
        }
    }

}



