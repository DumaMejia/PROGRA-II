package com.example.calculadora1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

public class ver extends AppCompatActivity {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    FloatingActionButton btnregresar;
    Button  btncomprar;
    ImageView imgpeli;
    String urlfoto, Idl,dueno,dir, Idu,IduU, IdlU, idprod,idlocal, accion = "nuevo", rev;
    TextView temp;
    detectarInternet di;
    DB miconexion;
    JSONArray jsonArrayDatosUsuarios;
    JSONObject jsonObjectDatosUsuarios, obj;
    Cursor datosusuariocursor = null;
    utilidades u;
    int posicion = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);

        try {

            Bundle recibirparametros = getIntent().getExtras();

            IduU = recibirparametros.getString("IduU");
            IdlU = recibirparametros.getString("IdlU");


        }catch (Exception e){
            mensajes(e.getMessage());
        }

        di = new detectarInternet(getApplicationContext());


        imgpeli = findViewById(R.id.imgfotopelicula);
        btnregresar = findViewById(R.id.btnregresar);
        btnregresar.setOnClickListener(v -> {
            regresarmenu();
        });

        btncomprar = findViewById(R.id.btncomprar);
        btncomprar.setOnClickListener(v -> {
            comprar();
        });

try {
    obtenerDatos();
    mostrardatos();
}catch (Exception e){

}
    }


    private void mostrardatos() {

        try {
            Bundle recibirparametros = getIntent().getExtras();
            accion = recibirparametros.getString("accion");
            idlocal = recibirparametros.getString("idlocal");
            Idu = recibirparametros.getString("Idu");
            Idl = recibirparametros.getString("Idl");



            if(di.hayConexionInternet()){


                try {
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArrayDatosUsuarios.length(); i++) {
                        jsonObject = jsonArrayDatosUsuarios.getJSONObject(i).getJSONObject("value");

                        if (jsonObject.getString("_id").equals(Idu)){

                                 dueno = jsonObject.getString("nombres");
                                 dir =  jsonObject.getString("direccion");
                        }

                    }
                }catch (Exception ex){
                    mensajes(ex.getMessage());
                }
            } else{

                try {
                    if (datosusuariocursor.moveToFirst()){
                        do {
                            if (datosusuariocursor.getString(0).equals(Idl)){

                                dueno = datosusuariocursor.getString(1);
                                dir = datosusuariocursor.getString(3);

                            }
                        }while (datosusuariocursor.moveToNext());

                    }

                }catch (Exception e){
                    mensajes(e.getMessage());
                }

            }


            if(accion.equals("ver")){
                JSONObject datos = new JSONObject(recibirparametros.getString("datos")).getJSONObject("value");
                idprod = datos.getString("_id");

                rev = datos.getString("_rev");

                temp = findViewById(R.id.txtDueno1);
                temp.setText(dueno);

                temp = findViewById(R.id.txtnom1);
                temp.setText(datos.getString("nombre"));

                temp = findViewById(R.id.txtdesc1);
                temp.setText(datos.getString("descripcion"));

                temp = findViewById(R.id.txtpresen1);
                temp.setText(datos.getString("presentacion"));

                temp = findViewById(R.id.txtprecio1);
                temp.setText(datos.getString("precio"));

                temp = findViewById(R.id.txtubi1);
                temp.setText(dir);

                urlfoto =  datos.getString("urlfoto");

                imgpeli.setImageURI(Uri.parse(urlfoto));

            }
        }catch (Exception ex){

        }
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
            ver.ConexionServer conexionServer = new ver.ConexionServer();
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
        Intent i = new Intent(getApplicationContext(), MenuInicio.class);
        i.putExtras(parametros);
        startActivity(i);
    }
    private void comprar() {
        Bundle parametros = new Bundle();
        parametros.putString("IduU", IduU);
        parametros.putString("IdlU", IdlU);
        parametros.putString("Idl", Idl);
        parametros.putString("Idu", Idu);
        Intent i = new Intent(getApplicationContext(), Comprar.class);
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