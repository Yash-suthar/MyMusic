package com.example.mymusicapp;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;

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
import java.util.ArrayList;

public class TcpConnectionManager {
    InetAddress tcp_ip;
    int port= 1999;
    InputStream inputStream;
    OutputStream outputStream;

    public TcpConnectionManager(){

        receive_connection();
    }
    public TcpConnectionManager(InetAddress inetAddress){
        tcp_ip = inetAddress;
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
                    Log.d("server","connectinon stablished with"+client_socket.getInetAddress());
                    outputStream = client_socket.getOutputStream();
                    outputStream.write("hello world".getBytes());
                    Log.d("server","stream writed");
                    byte[] buffer = new byte[1200];
                    client_socket.getInputStream().read(buffer);
                    String str = new String(buffer);
                    Log.d("server",str);
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
                    inputStream = client_socket.getInputStream();
                    Log.d("server","connection stablished");
                    byte[] buffer = new byte[1200];
                    inputStream.read(buffer);
                    String str = new String(buffer);
                    Log.d("server",str);
                    client_socket.getOutputStream().write("how are you".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public void send_sond(ArrayList<File> files,int position) throws IOException {

        File file = new File(files.get(position).getAbsolutePath());
        FileInputStream fi = new FileInputStream(file);
        byte[] buf = new byte[1024];
        int read;
        while ((read = fi.read(buf, 0, 1024)) != -1) {
            outputStream.write(buf, 0, read);
            outputStream.flush();
        }
    }

    public void receive_song() throws IOException {

        File file = File.createTempFile("playnow",".mp3", Environment.getDownloadCacheDirectory());
    }
}
