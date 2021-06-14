package com.example.calculadora1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class AgregarUsuario extends AppCompatActivity {


    FloatingActionButton btnregresar;
    String idusuario, idlocal, accion = "nuevo", rev;
    Button btnagregar, btngps;
    DB miconexion;
    JSONArray jsonArrayDatosUsuarios;
    JSONObject jsonObjectDatosUsuarios;
    Cursor datosusuariocursor = null;
    utilidades miUrl;
    detectarInternet di;
    TextView temp;
    TextView Dir;
    FusedLocationProviderClient FusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuario);


        di = new detectarInternet(getApplicationContext());

        miconexion = new DB(getApplicationContext(), "", null, 1);
        btnregresar = findViewById(R.id.btnregresar);
        btnagregar = findViewById(R.id.btnguardar);
        btngps = findViewById(R.id.btngps);
        Dir =  findViewById(R.id.txtdirecciongps);
        obtenerDatos();
        FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btnregresar.setOnClickListener(v -> {
            regresarmainactivity();
        });

        btnagregar.setOnClickListener(v -> {
            agregar();
        });

        try {


            btngps.setOnClickListener(v -> {
                ubicacion();
            });
        } catch (Exception w) {
            mensajes(w.getMessage());
        }
    }

    private void agregar() {
        try {
            temp = findViewById(R.id.txtnombres);
            String nombres = temp.getText().toString().trim();

            temp = findViewById(R.id.txtapellidos);
            String apellidos = temp.getText().toString().trim();

            temp = findViewById(R.id.txtdirecciongps);
            String direccion = temp.getText().toString().trim();

            temp = findViewById(R.id.txttelefono);
            String telefono = temp.getText().toString().trim();

            temp = findViewById(R.id.txtcorreo);
            String correo = temp.getText().toString().trim();

            temp = findViewById(R.id.txtcontra);
            String contra = temp.getText().toString().trim();

            int a = 0;

            if (di.hayConexionInternet()) {
                try {
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArrayDatosUsuarios.length(); i++) {
                        jsonObject = jsonArrayDatosUsuarios.getJSONObject(i).getJSONObject("value");

                        if (jsonObject.getString("usuario").equals(correo)) {

                            if (datosusuariocursor.moveToFirst()) {
                                do {
                                    if (datosusuariocursor.getString(5).equals(correo)) {
                                        a++;
                                    }
                                } while (datosusuariocursor.moveToNext());

                            }

                        }

                    }
                } catch (Exception ex) {
                    mensajes(ex.getMessage());
                }
            } else {

                if (datosusuariocursor.moveToFirst()) {
                    do {
                        if (datosusuariocursor.getString(3).equals(correo)) {
                            a++;
                        }
                    } while (datosusuariocursor.moveToNext());

                }

            }

            if (a > 0) {

                mensajes("ESE CORREO YA ESTA EN USO");
                temp = findViewById(R.id.txtcorreo);
                temp.setText("");

            }
            if (a == 0) {
                if(direccion==""||nombres==""||apellidos==""||telefono==""||correo==""||contra==""){
                    mensajes("por favor llene los campos");
                }else {
                    JSONObject datospelis = new JSONObject();
                    if (accion.equals("modificar") && idusuario.length() > 0 && rev.length() > 0) {
                        datospelis.put("_id", idusuario);
                        datospelis.put("_rev", rev);
                    }


                    datospelis.put("nombres", nombres);
                    datospelis.put("apellidos", apellidos);
                    datospelis.put("direccion", direccion);
                    datospelis.put("telefono", telefono);
                    datospelis.put("usuario", correo);
                    datospelis.put("contra", contra);

                    String[] datos = {nombres, apellidos, direccion, telefono, correo, contra};

                    di = new detectarInternet(getApplicationContext());
                    if (di.hayConexionInternet()) {
                        enviarDatosUsuarios guardarpelis = new enviarDatosUsuarios(getApplicationContext());
                        String resp = guardarpelis.execute(datospelis.toString()).get();
                    }

                    miconexion.agregar_usuario(accion, datos);
                    mensajes("Registro guardado con exito.");
                    regresarmainactivity();
                }
            }

        } catch (Exception w) {
            mensajes(w.getMessage());
        }
    }

    private void obtenerDatos() {

        try {
            if(di.hayConexionInternet()) {
                mensajes("Esta en linea");
                obtenerDatosOnLine();
                obtenerDatosOffLine();
            }else{
                mensajes("Imposible conectar con los servidores, conectese a internet");
            }

        } catch (Exception e) {
            mensajes(e.getMessage());
        }

    }

    private void ubicacion() {
        try {


            if (ActivityCompat.checkSelfPermission(AgregarUsuario.this

                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Cuando tengamos permiso
                getLocation();
            } else {
                //Si no tenemos permiso
                ActivityCompat.requestPermissions(AgregarUsuario.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        } catch (Exception e) {
            mensajes(e.getMessage());
        }
    }

    private void getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            FusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {

                        try {

                            Geocoder geocoder = new Geocoder(AgregarUsuario.this,
                                    Locale.getDefault());

                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );

                                Dir.setText(addresses.get(0).getAddressLine(0));


                        } catch (Exception e) {
                            mensajes(e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception e) {
            mensajes(e.getMessage());
        }
    }

    private void regresarmainactivity() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void obtenerDatosOnLine(){
        try {
            AgregarUsuario.ConexionServer conexionServer = new AgregarUsuario.ConexionServer();
            String resp = conexionServer.execute(miUrl.urlServer, "GET").get();
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

    private void mensajes(String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }
}