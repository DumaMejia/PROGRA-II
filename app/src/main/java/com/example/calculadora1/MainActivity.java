package com.example.calculadora1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btn;
    DB miBD;
    ListView ltsProductos;
    Cursor datosProductosCursor = null;
    ArrayList<Productos> productosArrayList=new ArrayList<Productos>();
    ArrayList<Productos> productosArrayListCopy=new ArrayList<Productos>();
    Productos Invproductos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
        datosProductosCursor.moveToPosition(adapterContextMenuInfo.position);
        menu.setHeaderTitle(datosProductosCursor.getString(1));
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.mnxAgregar:
                    agregarProducto("nuevo", new String[]{});
                    break;
                case R.id.mnxModificar:
                    String[] datos = {
                            datosProductosCursor.getString(0),//idProducto
                            datosProductosCursor.getString(1),//codigo
                            datosProductosCursor.getString(2),//descripcion
                            datosProductosCursor.getString(3),//marca
                            datosProductosCursor.getString(4), //presentacion
                            datosProductosCursor.getString(5), //precio
                            datosProductosCursor.getString(6) //url photo
                    };
                    agregarProducto("modificar", datos);
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
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
            confirmacion.setTitle("Esta seguro de eliminar el registro?");
            confirmacion.setMessage(datosProductosCursor.getString(1));
            confirmacion.setPositiveButton("Si", (dialog, which) -> {
                miBD = new DB(getApplicationContext(), "", null, 1);
                datosProductosCursor = miBD.administracion_productos("eliminar", new String[]{datosProductosCursor.getString(0)});
                obtenerDatosProducto();
                mostrarMsgToask("Registro Eliminado con exito...");
                dialog.dismiss();//cerrar el cuadro de dialogo
            });
            confirmacion.setNegativeButton("No", (dialog, which) -> {
                mostrarMsgToask("Eliminacion cancelada por el usuario...");
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

                            String buscando = tempVal.getText().toString().trim().toLowerCase();//escribe en la caja de texto...

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
    private void agregarProducto(String accion, String[] datos){
        try {
            Bundle parametrosProducto = new Bundle();
            parametrosProducto.putString("accion", accion);
            parametrosProducto.putStringArray("datos", datos);

            Intent agregarProducto = new Intent(getApplicationContext(), AgregarProducto.class);
            agregarProducto.putExtras(parametrosProducto);
            startActivity(agregarProducto);
        }catch (Exception e){
            mostrarMsgToask(e.getMessage());
        }
    }
    private void obtenerDatosProducto(){
        miBD = new DB(getApplicationContext(),"",null,1);
        datosProductosCursor = miBD.administracion_productos("consultar",null);
        if( datosProductosCursor.moveToFirst() ){
            mostrarDatosProducto();
        } else {
            mostrarMsgToask("No hay productos que mostrar, por favor agregue nuevos");
            agregarProducto("nuevo", new String[]{});
        }
    }
    private void mostrarDatosProducto(){
        ltsProductos = findViewById(R.id.ltsproductos);
        productosArrayList.clear();
        productosArrayListCopy.clear();
        do{
            Invproductos = new Productos(
                    datosProductosCursor.getString(0),//idProducto
                    datosProductosCursor.getString(1),//codigo
                    datosProductosCursor.getString(2),//descripcion
                    datosProductosCursor.getString(3),//marca
                    datosProductosCursor.getString(4),//presentacion
                    datosProductosCursor.getString(5),//precio
                    datosProductosCursor.getString(6) //urlPhoto
            );
            productosArrayList.add(Invproductos);
        }while(datosProductosCursor.moveToNext());
        adaptadorImagenes adaptadorImagenes = new adaptadorImagenes(getApplicationContext(), productosArrayList);
        ltsProductos.setAdapter(adaptadorImagenes);

        registerForContextMenu(ltsProductos);

        productosArrayListCopy.addAll(productosArrayList);
    }
    private void mostrarMsgToask(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }


    }

class Productos{
    String idProducto;
    String codigo;
    String descripcion;
    String marca;
    String presentacion;
    String precio;
    String urlImg;

    public Productos(String idProducto, String codigo, String descripcion, String marca, String presentacion, String precio, String urlImg) {
        this.idProducto = idProducto;
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


