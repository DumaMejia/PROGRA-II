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

    Button Bproductos, Uproductos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);

        Bproductos = findViewById(R.id.btnBuscarProducto);
        Uproductos = findViewById(R.id.btnProductosUsuario);

        Bproductos.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), MenuInicio.class);
            startActivity(i);
        });

        Uproductos.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), MenuProductosUsuarios.class);
            startActivity(i);
        });
    }



}


