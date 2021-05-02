package com.example.calculadora1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class detectarInternet {
    //  Brian Nelson Centeno Alvarado     USIS019817
    //  Herson Geovanni López Campos      USIS031120
    //  José Daniel Mejia Jovel           USIS010420
    //  William Alexander Amaya García    USIS032120
    private Context context;

    public detectarInternet(Context context) {
        this.context = context;
    }
    public boolean hayConexionInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if( connectivityManager!=null ){
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if( networkInfos!=null ){
                for (int i=0; i<networkInfos.length; i++){
                    if( networkInfos[i].getState()==NetworkInfo.State.CONNECTED ){
                        return  true;
                    }
                }
            }
        }
        return false;
    }
}
