package com.example.calculadora1;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Menue extends AppCompatActivity {

    Button Bproductos, Uproductos, Bc;
    String Idu, Idl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);

        try {

            Bundle recibirparametros = getIntent().getExtras();
            Idl = recibirparametros.getString("Idl");
            Idu = recibirparametros.getString("Idu");

        }catch (Exception e){
        mensajes(e.getMessage());
    }
        Bproductos = findViewById(R.id.btnBuscarProducto);
        Uproductos = findViewById(R.id.btnProductosUsuario);
        Bc = findViewById(R.id.cerrarsesion);

        Bproductos.setOnClickListener(v->{
            Bundle parametros = new Bundle();
            parametros.putString("IduU", Idu);
            parametros.putString("IdlU", Idl);
            Intent i = new Intent(getApplicationContext(), MenuInicio.class);
            i.putExtras(parametros);
            startActivity(i);
        });

        Bc.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        });

        Uproductos.setOnClickListener(v->{
            Bundle parametros = new Bundle();
            parametros.putString("Idu", Idu);
            parametros.putString("Idl", Idl);
            Intent i = new Intent(getApplicationContext(), MenuProductosUsuarios.class);
            i.putExtras(parametros);
            startActivity(i);
        });
    }
    private void mensajes(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }



}


