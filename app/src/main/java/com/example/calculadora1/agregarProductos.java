package com.example.calculadora1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

public class agregarProductos extends AppCompatActivity {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    FloatingActionButton btnregresar;
    ImageView imgpeli;
    String urlfoto,idprod,Idu,Idl,idlocal, accion = "nuevo", rev;
    Button btnagregar;
    DBP miconexion;
    TextView temp;
    utilidades miUrl;
    MainActivity M;
    detectarInternet di;
    private static final int RPQ= 100;
    private static final int RIG= 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        try {

        Bundle recibirparametros = getIntent().getExtras();
        Idl = recibirparametros.getString("Idl");
        Idu = recibirparametros.getString("Idu");

        mensajes(Idl);
        mensajes(Idu);

        }catch (Exception e) {
            mensajes(e.getMessage());
        }

        miconexion = new DBP(getApplicationContext(),"",null,1);
        btnregresar = findViewById(R.id.btnAtras);
        imgpeli = findViewById(R.id.imgFotoProducto);
        btnagregar = findViewById(R.id.btnGuardarProducto);

        try {

            btnregresar.setOnClickListener(v -> {
                regresarmainactivity();
            });

            imgpeli.setOnClickListener(v -> {
                abrirgaleriaimagen();
            });

            btnagregar.setOnClickListener(v -> {
                agregar();
            });
        }catch (Exception w){
            mensajes(w.getMessage());
        }

           permisos();
        mostrardatos();
    }


    private void agregar() {


        try {
            temp = findViewById(R.id.txtNombre);
            String nombre = temp.getText().toString();

            String idUsu = Idu;

            String idl = Idl;

            temp = findViewById(R.id.txtDescripcion);
            String descripcion = temp.getText().toString();

            temp = findViewById(R.id.txtPresentacion);
            String presentacion = temp.getText().toString();

            temp = findViewById(R.id.txtPrecio);
            String precio = temp.getText().toString();


            JSONObject datosproductos = new JSONObject();
            if(accion.equals("modificar") && idprod.length()>0 && rev.length()>0 ){
                datosproductos.put("_id",idprod);
                datosproductos.put("_rev",rev);
            }



            datosproductos.put("nombre",nombre);
            datosproductos.put("idUsu",idUsu);
            datosproductos.put("idl",idl);
            datosproductos.put("descripcion",descripcion);
            datosproductos.put("presentacion",presentacion);
            datosproductos.put("precio",precio);

            if (urlfoto != ""){
                datosproductos.put("urlfoto",urlfoto);
            }else {
                urlfoto = "404nofound";
                datosproductos.put("urlfoto",urlfoto);
            }

            String[] datos = {idlocal, nombre, idUsu, idl, descripcion, presentacion, precio, urlfoto };

            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()) {
                enviarDatosProductos guardarpelis = new enviarDatosProductos(getApplicationContext());
                String resp = guardarpelis.execute(datosproductos.toString()).get();
            }

            miconexion.administracion_de_productos(accion, datos);
            mensajes("Registro guardado con exito.");
            regresarmainactivity();
        }catch (Exception w){
            mensajes(w.getMessage());
        }
    }

    private void mostrardatos() {
        try {
            Bundle recibirparametros = getIntent().getExtras();
            accion = recibirparametros.getString("accion");
            idlocal = recibirparametros.getString("idlocal");
            if(accion.equals("modificar")){
                JSONObject datos = new JSONObject(recibirparametros.getString("datos")).getJSONObject("value");
                idprod = datos.getString("_id");

                rev = datos.getString("_rev");

                temp = findViewById(R.id.txtNombre);
                temp.setText(datos.getString("nombre"));

                temp = findViewById(R.id.txtDescripcion);
                temp.setText(datos.getString("descripcion"));

                temp = findViewById(R.id.txtPresentacion);
                temp.setText(datos.getString("presentacion"));

                temp = findViewById(R.id.txtPrecio);
                temp.setText(datos.getString("precio"));

                urlfoto =  datos.getString("urlfoto");



                imgpeli.setImageURI(Uri.parse(urlfoto));
            }
        }catch (Exception ex){
            mensajes(ex.getMessage());

        }
    }

    private void permisos() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        if (ActivityCompat.checkSelfPermission(agregarProductos.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
         }else {
            ActivityCompat.requestPermissions(agregarProductos.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},RPQ);
        }
    }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataimagen) {
            if (resultCode == Activity.RESULT_OK && dataimagen != null) {
                if (requestCode == RIG) {
                Uri photo = dataimagen.getData();
                imgpeli.setImageURI(photo);

                urlfoto = getRealUrl(this,photo);
            }
        }
        super.onActivityResult(requestCode, resultCode, dataimagen);
    }

    private void abrirgaleriaimagen(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT );
        i.setType("image/*");
        startActivityForResult(i, RIG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode== RPQ){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }else{
                mensajes("Por favor valida permisos");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void regresarmainactivity() {
        Bundle parametros = new Bundle();
        parametros.putString("Idu", Idu);
        parametros.putString("Idl", Idl);
        Intent i = new Intent(getApplicationContext(), MenuProductosUsuarios.class);
        i.putExtras(parametros);
        startActivity(i);
    }


    private void mensajes(String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }

    public static String getRealUrl(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}