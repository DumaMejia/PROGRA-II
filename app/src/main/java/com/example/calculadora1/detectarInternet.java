package com.example.calculadora1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class detectarInternet {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

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
