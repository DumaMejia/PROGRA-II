package com.example.calculadora1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Comprar extends AppCompatActivity {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    FloatingActionButton btnregresar;
    Button comp;
    ImageView imgpeli;
    String urlfoto, Idl,dueno,dir, Idu,IduU, IdlU, idprod,idlocal, accion = "nuevo", rev;
    TextView temp;
    detectarInternet di;
    DBP miconexion;
    JSONArray jsonArrayDatosUsuarios;
    JSONObject jsonObjectDatosUsuarios;
    JSONArray jsonArrayDatosProductos, jsonarraycopy;
    JSONObject jsonObjectDatosProductos, jsonobj;
    Cursor datosusuariocursor = null;
    Cursor datosproductoscursor = null;
    utilidades u;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar);

        try {

            Bundle recibirparametros = getIntent().getExtras();

            IduU = recibirparametros.getString("IduU");
            IdlU = recibirparametros.getString("IdlU");


        }catch (Exception e){
            mensajes(e.getMessage());
        }

        di = new detectarInternet(getApplicationContext());

        btnregresar = findViewById(R.id.btnregresarver);
        btnregresar.setOnClickListener(v -> {
            regresarmenu();
        });
        comp = findViewById(R.id.btnObtener);
        comp.setOnClickListener(v -> {
            Eliminar();
        });

try {
    obtenerDatos();
}catch (Exception e){

}
    }

    private void Eliminar(){
        try {
            temp.findViewById(R.id.txtnTarjeta);
            String a = temp.getText().toString();

            temp.findViewById(R.id.txtnomtar);
            String b = temp.getText().toString();

            temp.findViewById(R.id.txtfecha);
            String c = temp.getText().toString();

            temp.findViewById(R.id.txtcodigo);
            String d = temp.getText().toString();
            if (a==""||b==""||c==""||d==""){
                mensajes("por favor llene los campos");
            }else{
                mensajes("compra realizada con exito");
                regresarmenuInicio();
            }

        } catch (Exception ex) {
            mensajes(ex.getMessage());
        }
    }
    private void obtenerDatos(){

        try{
            if (di.hayConexionInternet()) {
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
            Comprar.ConexionServer conexionServer = new Comprar.ConexionServer();
            String resp = conexionServer.execute(u.urlServerP, "GET").get();
            jsonObjectDatosUsuarios=new JSONObject(resp);
            jsonArrayDatosUsuarios = jsonObjectDatosUsuarios.getJSONArray("rows");
        }catch (Exception ex){
            mensajes(ex.getMessage());
        }
    }

    private void obtenerDatosOffLine(){
        try {
            miconexion = new DBP(getApplicationContext(), "", null, 1);
            datosusuariocursor = miconexion.administracion_de_productos("consultar", null);
            if( datosusuariocursor.moveToFirst() ){
            } else {
                mensajes("No hay datos");
            }
        }catch (Exception e){
            mensajes(e.getMessage());
        }

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


    private void regresarmenu() {
        Bundle parametros = new Bundle();
        parametros.putString("IduU", IduU);
        parametros.putString("IdlU", IdlU);
        Intent i = new Intent(getApplicationContext(), ver.class);
        i.putExtras(parametros);
        startActivity(i);
    }
    private void regresarmenuInicio() {
        Bundle parametros = new Bundle();
        parametros.putString("IduU", IduU);
        parametros.putString("IdlU", IdlU);
        Intent i = new Intent(getApplicationContext(), MenuInicio.class);
        i.putExtras(parametros);
        startActivity(i);
    }

    private void mensajes(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }
    private void mensajesInt(int msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }

}