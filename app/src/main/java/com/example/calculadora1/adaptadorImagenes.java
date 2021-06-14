package com.example.calculadora1;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class adaptadorImagenes extends BaseAdapter {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    Context context;
    ArrayList<Productos> datosprodArrayList;
    LayoutInflater layoutInflater;
    Productos misProductos;

    public adaptadorImagenes(Context context, ArrayList<Productos> datospelisArrayList) {
        this.context = context;
        this.datosprodArrayList = datospelisArrayList;
    }

    @Override
    public int getCount() {
        return datosprodArrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return datosprodArrayList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View encuadre = layoutInflater.inflate(R.layout.listview_imagenes, parent, false);
        TextView temp = encuadre.findViewById(R.id.lblnombreP);
        ImageView img = encuadre.findViewById(R.id.imgPhoto);
        try{
            misProductos = datosprodArrayList.get(position);
            temp.setText(misProductos.getNombre());

            temp = encuadre.findViewById(R.id.lbldescrip);
            temp.setText(misProductos.getDescripcion());

            temp = encuadre.findViewById(R.id.lblprecio);
            temp.setText("$"+misProductos.getPrecio());

          String urldefoto = misProductos.getUrlfoto();


         img.setImageURI(Uri.parse(urldefoto));

        }catch (Exception e){
        }
        return encuadre;
    }
}

