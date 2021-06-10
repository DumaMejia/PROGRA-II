package com.example.calculadora1;

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

import androidx.annotation.NonNull;
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
import java.util.ArrayList;

public class MenuProductosUsuarios extends AppCompatActivity {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220


    FloatingActionButton btnadd;
    DBP miconexion;
    ListView ltsproductos;
    Cursor datosproductoscursor = null;
    ArrayList<Productos> productosArrayList=new ArrayList<Productos>();
    ArrayList<Productos> productosArrayListCopy=new ArrayList<Productos>();
    Productos misProductos;
    JSONArray jsonArrayDatosProductos;
    JSONObject jsonObjectDatosProductos;
    utilidades u;
    String idlocal;
    detectarInternet di;
    int position = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_usu);

        di = new detectarInternet(getApplicationContext());
        btnadd = findViewById(R.id.btnAgregarProducto);
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
                datosproductoscursor.moveToPosition(adapterContextMenuInfo.position);
                position = adapterContextMenuInfo.position;
                menu.setHeaderTitle(jsonArrayDatosProductos.getJSONObject(position).getJSONObject("value").getString("nombre"));
            } else {
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
                datosproductoscursor.moveToPosition(adapterContextMenuInfo.position);
                menu.setHeaderTitle(datosproductoscursor.getString(2));
            }
            idlocal = datosproductoscursor.getString(0);
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
                    Modificar("modificar");
                    break;
                case R.id.mxnEliminar:
                    Eliminar();
                    break;
            }
            }catch(Exception ex){
                mensajes(ex.getMessage());
            }
            return super.onContextItemSelected(item);

        }
    private void ver(String datos) {
        Bundle parametros = new Bundle();
        parametros.putString("accion","ver" );
        parametros.putString("idlocal", idlocal);
        jsonObjectDatosProductos = new JSONObject();
        JSONObject jsonValueObject = new JSONObject();
        if(di.hayConexionInternet())
        {
            try {
                if(jsonArrayDatosProductos.length()>0){
                    parametros.putString("datos", jsonArrayDatosProductos.getJSONObject(position).toString() );
                }
            }catch (Exception e){
                mensajes(e.getMessage());
            }
        }else{
            try {
                jsonArrayDatosProductos = new JSONArray();
                jsonObjectDatosProductos.put("_id", datosproductoscursor.getString(0));
                jsonObjectDatosProductos.put("_rev", datosproductoscursor.getString(0));
                jsonObjectDatosProductos.put("idUsu", datosproductoscursor.getString(1));
                jsonObjectDatosProductos.put("nombre", datosproductoscursor.getString(2));
                jsonObjectDatosProductos.put("descripcion", datosproductoscursor.getString(3));
                jsonObjectDatosProductos.put("presentacion", datosproductoscursor.getString(4));
                jsonObjectDatosProductos.put("precio", datosproductoscursor.getString(5));
                jsonObjectDatosProductos.put("urlfoto", datosproductoscursor.getString(6));
                jsonValueObject.put("value", jsonObjectDatosProductos);
                jsonArrayDatosProductos.put(jsonValueObject);
                if(jsonArrayDatosProductos.length()>0){
                    parametros.putString("datos", jsonArrayDatosProductos.getJSONObject(position).toString() );
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
                androidx.appcompat.app.AlertDialog.Builder confirmacion = new AlertDialog.Builder(MenuProductosUsuarios.this);
                confirmacion.setTitle("Esta seguro de eliminar?");
                if (di.hayConexionInternet())
                {
                    jsonObjectDatosProductos = jsonArrayDatosProductos.getJSONObject(position).getJSONObject("value");
                    confirmacion.setMessage(jsonObjectDatosProductos.getString("titulo"));
                }else {
                    confirmacion.setMessage(datosproductoscursor.getString(2));
                }

                confirmacion.setPositiveButton("Si", (dialog, which) -> {

                    try {
                        if(di.hayConexionInternet()){
                            ConexionServer objElimina = new ConexionServer();
                            String resp =  objElimina.execute(u.url_mto +
                                    jsonObjectDatosProductos.getString("_id")+ "?rev="+
                                    jsonObjectDatosProductos.getString("_rev"), "DELETE"
                            ).get();
                            JSONObject jsonRespEliminar = new JSONObject(resp);
                            if(jsonRespEliminar.getBoolean("ok")){
                                jsonArrayDatosProductos.remove(position);
                                mostrarDatos();
                            }
                        }

                        miconexion = new DBP(getApplicationContext(), "", null, 1);
                        datosproductoscursor = miconexion.eliminar("eliminar", datosproductoscursor.getString(0));
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
        TextView tempVal = findViewById(R.id.txtBuscarProductos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    productosArrayList.clear();
                    if (tempVal.getText().toString().length()<1){
                        productosArrayList.addAll(productosArrayListCopy);
                    } else{//si esta buescando biuscar los datos
                        for (Productos B : productosArrayListCopy){
                            String idU = B.getIdUsu();

                            String buscando = tempVal.getText().toString().trim().toLowerCase();
                            if(idU.toLowerCase().contains(buscando)
                            ){
                                productosArrayList.add(B);
                            }
                        }
                    }
                    adaptadorImagenes adaptadorImagenes = new adaptadorImagenes(getApplicationContext(), productosArrayList);
                    ltsproductos.setAdapter(adaptadorImagenes);
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
        jsonObjectDatosProductos = new JSONObject();
        JSONObject jsonValueObject = new JSONObject();
        if(di.hayConexionInternet())
        {
            try {
                if(jsonArrayDatosProductos.length()>0){
                    parametros.putString("datos", jsonArrayDatosProductos.getJSONObject(position).toString() );
                }
            }catch (Exception e){
                mensajes(e.getMessage());
            }
        }else{
            try {
                jsonArrayDatosProductos = new JSONArray();
                jsonObjectDatosProductos.put("_id", datosproductoscursor.getString(0));
                jsonObjectDatosProductos.put("_rev", datosproductoscursor.getString(0));
                jsonObjectDatosProductos.put("idUsu", datosproductoscursor.getString(1));
                jsonObjectDatosProductos.put("nombre", datosproductoscursor.getString(2));
                jsonObjectDatosProductos.put("descripcion", datosproductoscursor.getString(3));
                jsonObjectDatosProductos.put("presentacion", datosproductoscursor.getString(4));
                jsonObjectDatosProductos.put("precio", datosproductoscursor.getString(5));
                jsonObjectDatosProductos.put("urlfoto", datosproductoscursor.getString(6));
                jsonValueObject.put("value", jsonObjectDatosProductos);
                jsonArrayDatosProductos.put(jsonValueObject);
                if(jsonArrayDatosProductos.length()>0){
                    parametros.putString("datos", jsonArrayDatosProductos.getJSONObject(position).toString() );
                }

            }catch (Exception e){
                mensajes(e.getMessage());
            }
        }
        Intent i = new Intent(getApplicationContext(), agregarProductos.class);
        i.putExtras(parametros);
        startActivity(i);
    }


    private void Agregar(String accion){
        Bundle parametros = new Bundle();
        parametros.putString("accion", accion);
        Intent i = new Intent(getApplicationContext(), agregarProductos.class);
        i.putExtras(parametros);
        startActivity(i);
    }

    private void obtenerDatosOffLine(){
        try {
            miconexion = new DBP(getApplicationContext(), "", null, 1);
            datosproductoscursor = miconexion.administracion_de_productos("consultar", null);
            if( datosproductoscursor.moveToFirst() ){
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
            String resp = conexionServer.execute(u.urlServerP, "GET").get();
            jsonObjectDatosProductos=new JSONObject(resp);
            jsonArrayDatosProductos = jsonObjectDatosProductos.getJSONArray("rows");

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
            ltsproductos = findViewById(R.id.ltsproductos);
            productosArrayList.clear();
            productosArrayListCopy.clear();
            JSONObject jsonObject;
            if(di.hayConexionInternet()) {
                if(jsonArrayDatosProductos.length()>0) {
                    for (int i = 0; i < jsonArrayDatosProductos.length(); i++) {
                        jsonObject = jsonArrayDatosProductos.getJSONObject(i).getJSONObject("value");
                        misProductos = new Productos(
                                jsonObject.getString("_id"),
                                jsonObject.getString("_rev"),
                                jsonObject.getString("titulo"),
                                jsonObject.getString("sinopsis"),
                                jsonObject.getString("duracion"),
                                jsonObject.getString("precio"),
                                jsonObject.getString("urlfoto"),
                                jsonObject.getString("urltriler")
                        );
                        productosArrayList.add(misProductos);
                    }}
            } else {
                do{
                    misProductos = new Productos(
                            datosproductoscursor.getString(0),//
                            datosproductoscursor.getString(1),//
                            datosproductoscursor.getString(1),//
                            datosproductoscursor.getString(2),//
                            datosproductoscursor.getString(3),//
                            datosproductoscursor.getString(4),//
                            datosproductoscursor.getString(5), //
                            datosproductoscursor.getString(6) //
                    );
                    productosArrayList.add(misProductos);
                }while(datosproductoscursor.moveToNext());
            }
            adaptadorImagenes adaptadorImagenes = new adaptadorImagenes(getApplicationContext(), productosArrayList);
            ltsproductos.setAdapter(adaptadorImagenes);
            registerForContextMenu(ltsproductos);
            productosArrayListCopy.addAll(productosArrayList);

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



