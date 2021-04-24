package com.example.calculadora1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
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
    FloatingActionButton btn;
    DB miBD;
    ListView ltsProductos;
    Cursor datosProductosCursor = null;
    ArrayList<Productos> productosArrayList=new ArrayList<Productos>();
    ArrayList<Productos> productosArrayListCopy=new ArrayList<Productos>();
    Productos Invproductos;
    JSONArray jsonArrayDatosProductos;
    JSONObject jsonObjectDatosProductos;
    utilidades u;
    detectarInternet di;
    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        di = new detectarInternet(getApplicationContext());
        btn = findViewById(R.id.btnAgregarProducto);
        btn.setOnClickListener(v->{
            agregarProducto("nuevo", new String[]{});
        });
        obtenerDatosProducto();
        buscarProducto();

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_productos, menu);

        try {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            position = adapterContextMenuInfo.position;

            menu.setHeaderTitle(jsonArrayDatosProductos.getJSONObject(position).getJSONObject("value").getString("descripcion"));
        }catch (Exception e){
            mostrarMsgToask(e.getMessage());
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.mnxAgregar:
                    agregarProducto("nuevo", new String[]{});
                    break;
                case R.id.mnxModificar:
                    agregarProducto( "modificar");
                    break;
                case R.id.mnxEliminar:
                    eliminarProducto();
                    break;
            }
        }catch (Exception ex){
            mostrarMsgToask(ex.getMessage());
        }
        return super.onContextItemSelected(item);
    }
    private void eliminarProducto(){
        try {
            jsonObjectDatosProductos = jsonArrayDatosProductos.getJSONObject(position).getJSONObject("value");
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
            confirmacion.setTitle("¿Esta seguro de eliminar el producto?");
            confirmacion.setMessage(jsonObjectDatosProductos.getString("descripcion"));
            confirmacion.setPositiveButton("Si", (dialog, which) -> {
                try {
                    if(di.hayConexionInternet()){
                        ConexionServer objEliminarProducto = new ConexionServer();
                        String resp =  objEliminarProducto.execute(u.url_mto +
                                jsonObjectDatosProductos.getString("_id")+ "?rev="+
                                jsonObjectDatosProductos.getString("_rev"), "DELETE"
                        ).get();
                        JSONObject jsonRespEliminar = new JSONObject(resp);
                        if(jsonRespEliminar.getBoolean("ok")){
                            jsonArrayDatosProductos.remove(position);
                            mostrarDatosProducto();
                        }
                    }
                    miBD = new DB(getApplicationContext(), "", null, 1);
                    datosProductosCursor = miBD.administracion_productos("eliminar", new String[]{jsonObjectDatosProductos.getString("_id")});//idProducto
                    obtenerDatosProducto();
                    mostrarMsgToask("producto Eliminado con exito");
                    dialog.dismiss();
                }catch (Exception e){
                    mostrarMsgToask(e.getMessage());
                }
            });
            confirmacion.setNegativeButton("No", (dialog, which) -> {
                mostrarMsgToask("Eliminacion cancelada");
                dialog.dismiss();
            });
            confirmacion.create().show();
        }catch (Exception ex){
            mostrarMsgToask(ex.getMessage());
        }
    }
    private void buscarProducto() {
        TextView tempVal = findViewById(R.id.txtBuscarProductos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    productosArrayList.clear();
                    if( tempVal.getText().toString().trim().length()<1 ){
                        productosArrayList.addAll(productosArrayListCopy);
                    } else {
                        for (Productos am : productosArrayListCopy){
                            String codigo = am.getCodigo();
                            String descripcion = am.getDescripcion();
                            String presentacion = am.getPresentacion();
                            String marca = am.getMarca();

                            String buscando = tempVal.getText().toString().trim().toLowerCase();

                            if(marca.toLowerCase().trim().contains(buscando) ||
                                    presentacion.trim().contains(buscando) ||
                                    descripcion.trim().toLowerCase().contains(buscando) ||
                                    codigo.trim().toLowerCase().contains(buscando)
                            ){
                                productosArrayList.add(am);
                            }
                        }
                    }
                    adaptadorImagenes adaptadorImagenes = new adaptadorImagenes(getApplicationContext(), productosArrayList);
                    ltsProductos.setAdapter(adaptadorImagenes);
                }catch (Exception e){
                    mostrarMsgToask(e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void agregarProducto(String accion){
        try {
            Bundle parametrosProductos = new Bundle();
            parametrosProductos.putString("accion", accion);
            if(jsonArrayDatosProductos.length()>0){
                parametrosProductos.putString("datos", jsonArrayDatosProductos.getJSONObject(position).toString() );
            }
            Intent agregarProductos = new Intent(getApplicationContext(), AgregarProducto.class);
            agregarProductos.putExtras(parametrosProductos);
            startActivity(agregarProductos);
        }catch (Exception e){
            mostrarMsgToask(e.getMessage());
        }
    }
    private void obtenerDatosProductosOffLine(){
        try {
            miBD = new DB(getApplicationContext(), "", null, 1);
            datosProductosCursor = miBD.administracion_productos("consultar", null);
            if (datosProductosCursor.moveToFirst()) {
                jsonObjectDatosProductos = new JSONObject();
                JSONObject jsonValueObject = new JSONObject();
                jsonArrayDatosProductos = new JSONArray();
                do {
                    jsonObjectDatosProductos.put("_id", datosProductosCursor.getString(0));
                    jsonObjectDatosProductos.put("_rev", datosProductosCursor.getString(0));
                    jsonObjectDatosProductos.put("codigo", datosProductosCursor.getString(1));
                    jsonObjectDatosProductos.put("descripcion", datosProductosCursor.getString(2));
                    jsonObjectDatosProductos.put("marca", datosProductosCursor.getString(3));
                    jsonObjectDatosProductos.put("presentacion", datosProductosCursor.getString(4));
                    jsonObjectDatosProductos.put("marca", datosProductosCursor.getString(5));
                    jsonObjectDatosProductos.put("urlPhoto", datosProductosCursor.getString(6));
                    jsonValueObject.put("value", jsonObjectDatosProductos);

                    jsonArrayDatosProductos.put(jsonValueObject);
                } while (datosProductosCursor.moveToNext());
                mostrarDatosProducto();
            } else {
                mostrarMsgToask("No hay productos que mostrar, por favor agregue nuevos");
                agregarProducto("nuevo");
            }
        }catch (Exception e){
            mostrarMsgToask(e.getMessage());
        }
    }
    private void obtenerDatosProductosOnLine(){
        try {
            ConexionServer conexionServer = new ConexionServer();
            String resp = conexionServer.execute(u.url_consulta, "GET").get();

            jsonObjectDatosProductos=new JSONObject(resp);
            jsonArrayDatosProductos = jsonObjectDatosProductos.getJSONArray("rows");
            mostrarDatosProducto();
        }catch (Exception ex){
            mostrarMsgToask(ex.getMessage());
        }
    }

    private void obtenerDatosProducto(){
        if(di.hayConexionInternet()) {
            mostrarMsgToask("Hay internet, mostrando datos de la nube");
            obtenerDatosProductosOnLine();
        } else {
            jsonArrayDatosProductos = new JSONArray();
            mostrarMsgToask("NO hay internet, mostrando datos local");
            obtenerDatosProductosOffLine();
        }
    }

    private void mostrarDatosProducto(){
        try{
            if(jsonArrayDatosProductos.length()>0){
                ltsProductos = findViewById(R.id.ltsproductos);
                productosArrayList.clear();
                productosArrayListCopy.clear();

                JSONObject jsonObject;
                for(int i=0; i<jsonArrayDatosProductos.length(); i++){
                    jsonObject=jsonArrayDatosProductos.getJSONObject(i).getJSONObject("value");
                    Invproductos = new Productos(
                            jsonObject.getString("idProducto"),
                            jsonObject.getString("_rev"),
                            jsonObject.getString("codigo"),
                            jsonObject.getString("descripcion"),
                            jsonObject.getString("marca"),
                            jsonObject.getString("presentacion"),
                            jsonObject.getString("precio"),
                            jsonObject.getString("urlPhoto")
                    );
                    productosArrayList.add(Invproductos);
                }
                adaptadorImagenes adaptadorImagenes = new adaptadorImagenes(getApplicationContext(), productosArrayList);
                ltsProductos.setAdapter(adaptadorImagenes);

                registerForContextMenu(ltsProductos);

                productosArrayListCopy.addAll(productosArrayList);
            }else{
                mostrarMsgToask("no hay productos que mostrar");
                agregarProducto("nuevo");
            }
        }catch (Exception e){
            mostrarMsgToask(e.getMessage());
        }

    }
    private void mostrarMsgToask(String msg){
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

class Productos{
    String idProducto;
    String _rev;
    String codigo;
    String descripcion;
    String marca;
    String presentacion;
    String precio;
    String urlImg;

    public Productos(String idProducto, String _rev, String codigo, String descripcion, String marca, String presentacion, String precio, String urlImg) {
        this.idProducto = idProducto;
        this._rev = _rev;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.marca = marca;
        this.presentacion = presentacion;
        this.precio = precio;
        this.urlImg = urlImg;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String email) {
        this.presentacion = presentacion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String email) {
        this.precio = precio;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}


