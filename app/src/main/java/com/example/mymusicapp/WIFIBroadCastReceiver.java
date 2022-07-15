package com.example.mymusicapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class WIFIBroadCastReceiver extends BroadcastReceiver {
    private final WifiP2pManager wifiP2pManager;
    private final  WifiP2pManager.Channel channel;
    private final MainActivity mainActivity;
    Boolean bool=true;
    public WIFIBroadCastReceiver(WifiP2pManager p2pManager, WifiP2pManager.Channel channel, MainActivity mainActivity) {
        this.wifiP2pManager = p2pManager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
//            Log.d("wifip2p", "" + state);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "Wifi enabled", Toast.LENGTH_SHORT).show();
            } else if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
//                Toast.makeText(context, "turn on wifi for sync", Toast.LENGTH_SHORT).show();

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (wifiP2pManager != null) {
                if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                wifiP2pManager.requestPeers(channel, mainActivity.peerListListener);
          }
      }else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
         if (wifiP2pManager==null){
             return;
         }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
         if (networkInfo.isConnected()){

             if (bool){
                 wifiP2pManager.requestConnectionInfo(channel, mainActivity.connectionInfoListener);
                 bool=false;
             }

         }else{
            if (!bool) {
                try {
                    mainActivity.ondisconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bool = true;
                Toast.makeText(context, "device disconnected", Toast.LENGTH_SHORT).show();
            }
         }
      }else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
          //do something
      }
    }
}
