package com.example.calculadora1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class adaptadorImagenes extends BaseAdapter {
    Context context;
    ArrayList<Productos> datosProductosArrayList;
    LayoutInflater layoutInflater;
    Productos Invproductos;

    public adaptadorImagenes(Context context, ArrayList<Productos> datosProductosArrayList) {
        this.context = context;
        this.datosProductosArrayList = datosProductosArrayList;
    }

    @Override
    public int getCount() {
        return datosProductosArrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return datosProductosArrayList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return Long.parseLong( datosProductosArrayList.get(position).getIdProducto() );
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, parent, false);
        TextView tempVal = itemView.findViewById(R.id.lblTitulo);
        ImageView imgViewView = itemView.findViewById(R.id.imgPhoto);
        try{
            Invproductos = datosProductosArrayList.get(position);
            tempVal.setText(Invproductos.getMarca());

            tempVal = itemView.findViewById(R.id.lblpresentacion);
            tempVal.setText(Invproductos.getPresentacion());

            tempVal = itemView.findViewById(R.id.lblprecio);
            tempVal.setText(Invproductos.getPrecio());

            Bitmap imagenBitmap = BitmapFactory.decodeFile(Invproductos.getUrlImg());
            imgViewView.setImageBitmap(imagenBitmap);

        }catch (Exception e){
        }
        return itemView;
    }
}
