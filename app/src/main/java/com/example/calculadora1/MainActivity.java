package com.example.calculadora1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    FloatingActionButton btnadd;
    DB miconexion;
    ListView ltspeliculas;
    Cursor datospeliculascursor = null;
    ArrayList<peliculas> peliculasArrayList=new ArrayList<peliculas>();
    ArrayList<peliculas> peliculasArrayListCopy=new ArrayList<peliculas>();
    peliculas misPeliculas;
    JSONArray jsonArrayDatosPeliculas;
    JSONObject jsonObjectDatosPeliculas;
    utilidades u;
    String idlocal;
    detectarInternet di;
    int position = 0;
    int prueba = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        di = new detectarInternet(getApplicationContext());
        btnadd = findViewById(R.id.btnagregar);
        btnadd.setOnClickListener(v->{
            Agregar("nuevo");
        });
        obtenerDatos();
        buscarPelicula();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit, menu);
        try {
            if(di.hayConexionInternet()) {
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
                datospeliculascursor.moveToPosition(adapterContextMenuInfo.position);
                position = adapterContextMenuInfo.position;
                menu.setHeaderTitle(jsonArrayDatosPeliculas.getJSONObject(position).getJSONObject("value").getString("titulo"));
            } else {
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
                datospeliculascursor.moveToPosition(adapterContextMenuInfo.position);
                menu.setHeaderTitle(datospeliculascursor.getString(1));
            }
            idlocal = datospeliculascursor.getString(0);
        }catch (Exception e){
            mensajes(e.getMessage());
        }
    }
    @Override

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.mxnAgregar:
                    Agregar("nuevo");
                    break;
                case R.id.mxnModificar:
                    Modificar ("modificar");
                    break;
                case R.id.mxnEliminar:
                    Eliminar();
                    break;
                case R.id.mxnVista:
                    ver("datos");
                    break;
            }
        }catch (Exception ex){
            mensajes(ex.getMessage());
        }
        return super.onContextItemSelected(item);
    }

    private void ver(String datos) {
        Bundle parametros = new Bundle();
        parametros.putString("accion","ver" );
        parametros.putString("idlocal", idlocal);
        jsonObjectDatosPeliculas = new JSONObject();
        JSONObject jsonValueObject = new JSONObject();
        if(di.hayConexionInternet())
        {
            try {
                if(jsonArrayDatosPeliculas.length()>0){
                    parametros.putString("datos", jsonArrayDatosPeliculas.getJSONObject(position).toString() );
                }
            }catch (Exception e){
                mensajes(e.getMessage());
            }
        }else{
            try {
                jsonArrayDatosPeliculas = new JSONArray();
                jsonObjectDatosPeliculas.put("_id", datospeliculascursor.getString(0));
                jsonObjectDatosPeliculas.put("_rev", datospeliculascursor.getString(0));
                jsonObjectDatosPeliculas.put("titulo", datospeliculascursor.getString(1));
                jsonObjectDatosPeliculas.put("sinopsis", datospeliculascursor.getString(2));
                jsonObjectDatosPeliculas.put("duracion", datospeliculascursor.getString(3));
                jsonObjectDatosPeliculas.put("precio", datospeliculascursor.getString(4));
                jsonObjectDatosPeliculas.put("urlfoto", datospeliculascursor.getString(5));
                jsonObjectDatosPeliculas.put("urltriler", datospeliculascursor.getString(6));
                jsonValueObject.put("value", jsonObjectDatosPeliculas);
                jsonArrayDatosPeliculas.put(jsonValueObject);
                if(jsonArrayDatosPeliculas.length()>0){
                    parametros.putString("datos", jsonArrayDatosPeliculas.getJSONObject(position).toString() );
                }

            }catch (Exception e){
                mensajes(e.getMessage());
            }
        }
        Intent i = new Intent(getApplicationContext(), ver.class);
        i.putExtras(parametros);
        startActivity(i);
    }

    private void Eliminar(){
        try {
            androidx.appcompat.app.AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
            confirmacion.setTitle("Esta seguro de eliminar?");
            if (di.hayConexionInternet())
            {
                jsonObjectDatosPeliculas = jsonArrayDatosPeliculas.getJSONObject(position).getJSONObject("value");
                confirmacion.setMessage(jsonObjectDatosPeliculas.getString("titulo"));
            }else {
                confirmacion.setMessage(datospeliculascursor.getString(1));
            }

            confirmacion.setPositiveButton("Si", (dialog, which) -> {

                try {
                    if(di.hayConexionInternet()){
                        ConexionServer objElimina = new ConexionServer();
                        String resp =  objElimina.execute(u.url_mto +
                                jsonObjectDatosPeliculas.getString("_id")+ "?rev="+
                                jsonObjectDatosPeliculas.getString("_rev"), "DELETE"
                        ).get();
                        JSONObject jsonRespEliminar = new JSONObject(resp);
                        if(jsonRespEliminar.getBoolean("ok")){
                            jsonArrayDatosPeliculas.remove(position);
                            mostrarDatos();
                        }
                    }

                    miconexion = new DB(getApplicationContext(), "", null, 1);
                    datospeliculascursor = miconexion.eliminar("eliminar", datospeliculascursor.getString(0));
                    obtenerDatos();
                    mensajes("Registro eliminado");
                    dialog.dismiss();
                }catch (Exception e){
                }
            });
            confirmacion.setNegativeButton("No", (dialog, which) -> {
                mensajes("Eliminacion detendia");
                dialog.dismiss();
            });
            confirmacion.create().show();
        } catch (Exception ex){
            mensajes(ex.getMessage());
        }
    }

    private void buscarPelicula() {
        TextView tempVal = findViewById(R.id.txtbuscar);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    peliculasArrayList.clear();
                    if (tempVal.getText().toString().length()<1){
                        peliculasArrayList.addAll(peliculasArrayListCopy);
                    } else{//si esta buescando biuscar los datos
                        for (peliculas B : peliculasArrayListCopy){
                            String titulo = B.getTitulo();
                            String sinopsis = B.getSinopsis();

                            String buscando = tempVal.getText().toString().trim().toLowerCase();
                            if(titulo.toLowerCase().contains(buscando) ||
                                    sinopsis.toLowerCase().contains(buscando)
                            ){
                                peliculasArrayList.add(B);
                            }
                        }
                    }
                    adaptadorImagenes adaptadorImagenes = new adaptadorImagenes(getApplicationContext(), peliculasArrayList);
                    ltspeliculas.setAdapter(adaptadorImagenes);
                }catch (Exception e){
                    mensajes(e.getMessage());
                }



            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void Modificar(String accion){
        Bundle parametros = new Bundle();
        parametros.putString("accion", accion);
        parametros.putString("idlocal", idlocal);
        jsonObjectDatosPeliculas = new JSONObject();
        JSONObject jsonValueObject = new JSONObject();
        if(di.hayConexionInternet())
        {
            try {
                if(jsonArrayDatosPeliculas.length()>0){
                    parametros.putString("datos", jsonArrayDatosPeliculas.getJSONObject(position).toString() );
                }
            }catch (Exception e){
                mensajes(e.getMessage());
            }
        }else{
            try {
                jsonArrayDatosPeliculas = new JSONArray();
                jsonObjectDatosPeliculas.put("_id", datospeliculascursor.getString(0));
                jsonObjectDatosPeliculas.put("_rev", datospeliculascursor.getString(0));
                jsonObjectDatosPeliculas.put("titulo", datospeliculascursor.getString(1));
                jsonObjectDatosPeliculas.put("sinopsis", datospeliculascursor.getString(2));
                jsonObjectDatosPeliculas.put("duracion", datospeliculascursor.getString(3));
                jsonObjectDatosPeliculas.put("precio", datospeliculascursor.getString(4));
                jsonObjectDatosPeliculas.put("urlfoto", datospeliculascursor.getString(5));
                jsonObjectDatosPeliculas.put("urltriler", datospeliculascursor.getString(6));
                jsonValueObject.put("value", jsonObjectDatosPeliculas);
                jsonArrayDatosPeliculas.put(jsonValueObject);
                if(jsonArrayDatosPeliculas.length()>0){
                    parametros.putString("datos", jsonArrayDatosPeliculas.getJSONObject(position).toString() );
                }

            }catch (Exception e){
                mensajes(e.getMessage());
            }
        }
        Intent i = new Intent(getApplicationContext(), agregarpelicula.class);
        i.putExtras(parametros);
        startActivity(i);
    }

    private void Agregar(String accion){
        Bundle parametros = new Bundle();
        parametros.putString("accion", accion);
        Intent i = new Intent(getApplicationContext(), agregarpelicula.class);
        i.putExtras(parametros);
        startActivity(i);
    }

    private void obtenerDatosOffLine(){
        try {
            miconexion = new DB(getApplicationContext(), "", null, 1);
            datospeliculascursor = miconexion.administracion_de_pelis("consultar", null);
            if( datospeliculascursor.moveToFirst() ){
                mostrarDatos();
            } else {
                mensajes("No hay datos");
            }
        }catch (Exception e){
            mensajes(e.getMessage());
        }

    }

    private void obtenerDatosOnLine(){
        try {
            ConexionServer conexionServer = new ConexionServer();
            String resp = conexionServer.execute(u.urlServer, "GET").get();
            jsonObjectDatosPeliculas=new JSONObject(resp);
            jsonArrayDatosPeliculas = jsonObjectDatosPeliculas.getJSONArray("rows");
            mostrarDatos();
        }catch (Exception ex){
            mensajes(ex.getMessage());
        }
    }

    private void obtenerDatos(){
        if(di.hayConexionInternet()) {
            mensajes("Mostrando datos desde la nube");
            obtenerDatosOnLine();
            obtenerDatosOffLine();

        } else {
            mensajes("Mostrando datos locales");
            obtenerDatosOffLine();
        }
    }

    private void mostrarDatos(){
        try{
            ltspeliculas = findViewById(R.id.listpeliculas);
            peliculasArrayList.clear();
            peliculasArrayListCopy.clear();
            JSONObject jsonObject;
            if(di.hayConexionInternet()) {
                if(jsonArrayDatosPeliculas.length()>0) {
                    for (int i = 0; i < jsonArrayDatosPeliculas.length(); i++) {
                        jsonObject = jsonArrayDatosPeliculas.getJSONObject(i).getJSONObject("value");
                        misPeliculas = new peliculas(
                                jsonObject.getString("_id"),
                                jsonObject.getString("_rev"),
                                jsonObject.getString("titulo"),
                                jsonObject.getString("sinopsis"),
                                jsonObject.getString("duracion"),
                                jsonObject.getString("precio"),
                                jsonObject.getString("urlfoto"),
                                jsonObject.getString("urltriler")
                        );
                        peliculasArrayList.add(misPeliculas);
                    }}
            } else {
                do{
                    misPeliculas = new peliculas(
                            datospeliculascursor.getString(0),//
                            datospeliculascursor.getString(1),//
                            datospeliculascursor.getString(1),//
                            datospeliculascursor.getString(2),//
                            datospeliculascursor.getString(3),//
                            datospeliculascursor.getString(4),//
                            datospeliculascursor.getString(5), //
                            datospeliculascursor.getString(6) //
                    );
                    peliculasArrayList.add(misPeliculas);
                }while(datospeliculascursor.moveToNext());
            }
            adaptadorImagenes adaptadorImagenes = new adaptadorImagenes(getApplicationContext(), peliculasArrayList);
            ltspeliculas.setAdapter(adaptadorImagenes);
            registerForContextMenu(ltspeliculas);
            peliculasArrayListCopy.addAll(peliculasArrayList);

        }catch (Exception e){
            mensajes(e.getMessage());
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

class peliculas{
    String idpelis;
    String rev;
    String titulo;
    String sinopsis;
    String duracion;
    String precio;
    String urlfoto;
    String urltriler;

    public peliculas(String idpelis, String rev, String titulo, String sinopsis, String duracion, String precio, String urlfoto, String urltriler) {
        this.idpelis = idpelis;
        this.rev = rev;
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.duracion = duracion;
        this.precio = precio;
        this.urlfoto = urlfoto;
        this.urltriler = urltriler;
    }

    public String getIdpelis() {
        return idpelis;
    }

    public void setIdpelis(String idpelis) {
        this.idpelis = idpelis;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setUrlfoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }

    public String getUrltriler() {
        return urltriler;
    }

    public void setUrltriler(String urltriler) {
        this.urltriler = urltriler;
    }
}


