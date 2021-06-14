package com.example.calculadora1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //agregar el ID del usuario
    Button login, registro;
    TextView temp;
    DB miconexion;
    detectarInternet di;
    utilidades u;
    JSONArray jsonArrayDatosUsuarios;
    JSONObject jsonObjectDatosUsuarios;
    Cursor datosusuariocursor = null;
    String idU;
    String idl;
    agregarProductos M;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        di = new detectarInternet(getApplicationContext());

        login = findViewById(R.id.btniniciar);
        registro = findViewById(R.id.btnregistrar);
        obtenerDatos();

        login.setOnClickListener(v->{
            logi();
        });

        registro.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), AgregarUsuario.class);
            startActivity(i);
        });
    }

    private void obtenerDatos(){

        try{
            if (di.hayConexionInternet()) {
                mensajes("ESTADO: ONLINE");
                obtenerDatosOnLine();
                obtenerDatosOffLine();
            }else{
                obtenerDatosOffLine();
                mensajes("ESTADO: OFFLINE");
            }


        }catch (Exception e){
            mensajes(e.getMessage());
        }

    }

    private void obtenerDatosOnLine(){
        try {
            ConexionServer conexionServer = new ConexionServer();
            String resp = conexionServer.execute(u.urlServer, "GET").get();
            jsonObjectDatosUsuarios=new JSONObject(resp);
            jsonArrayDatosUsuarios = jsonObjectDatosUsuarios.getJSONArray("rows");
        }catch (Exception ex){
            mensajes(ex.getMessage());
        }
    }

    private void obtenerDatosOffLine(){
        try {
            miconexion = new DB(getApplicationContext(), "", null, 1);
            datosusuariocursor = miconexion.consultar_usuario("consultar");
            if( datosusuariocursor.moveToFirst() ){
            } else {
                mensajes("No hay datos");
            }
        }catch (Exception e){
            mensajes(e.getMessage());
        }

    }


    private void logi() {

        int a = 0;

        temp = findViewById(R.id.txtuss);
        String usuario = temp.getText().toString().trim();

        temp = findViewById(R.id.txtcontra);
        String contra = temp.getText().toString().trim();

        if(di.hayConexionInternet()){

            try {
                JSONObject jsonObject;

                for (int i = 0; i < jsonArrayDatosUsuarios.length(); i++) {
                    jsonObject = jsonArrayDatosUsuarios.getJSONObject(i).getJSONObject("value");

                    if (jsonObject.getString("usuario").equals(usuario)){
                        if (jsonObject.getString("contra").equals(contra)){
                            a++;
                            idU = jsonObject.getString("_id");
                        }
                    }

                }
            }catch (Exception ex){
                mensajes(ex.getMessage());
            }

        } else {
            try {
                if (datosusuariocursor.moveToFirst()){
                    do {
                        if (datosusuariocursor.getString(5).equals(usuario)){
                            if (datosusuariocursor.getString(6).equals(contra)){
                                a++;
                                idl = datosusuariocursor.getString(0);
                            }
                        }
                    }while (datosusuariocursor.moveToNext());

                }

            }catch (Exception e){
                mensajes(e.getMessage());
            }

        }
        if (a>0){

            if (di.hayConexionInternet()){
                if (datosusuariocursor.moveToFirst()) {
                    do {
                        if (datosusuariocursor.getString(5).equals(usuario)) {
                            if (datosusuariocursor.getString(6).equals(contra)) {
                                idl = datosusuariocursor.getString(0);
                            }
                        }
                    } while (datosusuariocursor.moveToNext());
                }
            }else{
                idU="0";
            }

            mensajes("bienvenido");
            Bundle parametros = new Bundle();
            parametros.putString("Idu", idU);
            parametros.putString("Idl", idl);
            Intent i = new Intent(getApplicationContext(), Menue.class);
            i.putExtras(parametros);
            startActivity(i);
        }else {
            mensajes("No se encontro el usuario");
        }
    }

    private void mensajes(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }


    private class ConexionServer extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder result = new StringBuilder();
            try{
                String uri = parametros[0];
                String metodo = parametros[1];
                URL url = new URL(uri);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod(metodo);

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String linea;
                while( (linea=bufferedReader.readLine())!=null ){
                    result.append(linea);
                }
            }catch (Exception e){
                Log.i("GET", e.getMessage());
            }
            return result.toString();
        }
    }

}


