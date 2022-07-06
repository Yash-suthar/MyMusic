package com.example.mymusicapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

public class TcpConnectionManager {
    InetAddress tcp_ip;
    int port= 2000;
    InputStream inputStream;
    OutputStream outputStream;
    File send_file,receivefile;
    Context context;
    public TcpConnectionManager(){

        receive_connection();
    }
    public TcpConnectionManager(InetAddress inetAddress,File file){
        tcp_ip = inetAddress;
        send_file = file;
        send_connection();
    }
    public void receive_connection (){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("server","connection start to stablish");
                    ServerSocket serverSocket = new ServerSocket(port);
                    Log.d("server","port binded listening");

                    Socket client_socket = serverSocket.accept();
                    Log.d("server","connectinon stablished with"+ client_socket.getInetAddress().getHostName());
                    inputStream = client_socket.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//                    outputStream = client_socket.getOutputStream();
                    File file = File.createTempFile("playnow",".mp3", context.getCacheDir());
                    Log.d("server","file created");
                    FileOutputStream fis = new FileOutputStream(file);
                    Log.d("server","stream writed");
                    byte[] buf = new byte[1024];
                    int offset;
                    while((offset = bufferedInputStream.read(buf)) != -1){
                        Log.d("server","writing file");
                        fis.write(buf,0,offset);

                    }
                    fis.flush();
                    Log.d("server",""+file.length());
                    Log.d("server","songreceived");
                    Uri uri = Uri.parse(file.toString());
                    MediaPlayer mediaPlayer = MediaPlayer.create(context,uri);
                    mediaPlayer.start();
                    Log.d("server","song started");
//                    outputStream.write("hello world".getBytes());
//                    Log.d("server","stream writed");
//                    byte[] buffer = new byte[1200];
//                    client_socket.getInputStream().read(buffer);
//                    String str = new String(buffer);
//                    Log.d("server",str);
                    client_socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
    public void send_connection(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {Log.d("server","connection sending process start");
                    Socket client_socket = new Socket(tcp_ip,port);
                    Log.d("server","socket sended");
                    outputStream = client_socket.getOutputStream();
                    Log.d("server","socket stream output");
                    FileInputStream fi = new FileInputStream(send_file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fi);
                    Log.d("server","file stream created");
                    byte[] buf = new byte[(int)send_file.length()];
                    while (bufferedInputStream.read(buf) >0) {

                        Log.d("server","file sending");
                        outputStream.write(buf, 0, buf.length);

                    }
                    outputStream.flush();
                    Log.d("server",""+send_file.length());
//                    inputStream = client_socket.getInputStream();
                    Log.d("server","song sended");
//                    byte[] buffer = new byte[1200];
//                    inputStream.read(buffer);
//                    String str = new String(buffer);
//                    Log.d("server",str);
//                    client_socket.getOutputStream().write("how are you".getBytes());
                    client_socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public void send_sond() throws IOException {


        FileInputStream fi = new FileInputStream(send_file);
        byte[] buf = new byte[1024];
        int read;
        while ((read = fi.read(buf, 0, 1024)) != -1) {
            outputStream.write(buf, 0, read);
            outputStream.flush();
        }
    }

    public File receive_song() throws IOException {

        File file = File.createTempFile("playnow",".mp3", context.getCacheDir());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int read;
        while((read=inputStream.read(buf,0, 1024)) != -1){
            fis.write(buf,0,buf.length);
            fis.flush();
        }
        return file;
    }
}
