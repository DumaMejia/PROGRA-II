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
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

public class agregarpelicula extends AppCompatActivity {


    //Roberto carlos hernandez melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    FloatingActionButton btnregresar;
    ImageView imgpeli;
    VideoView vdipeli;
    String urlfoto, urlvideo,idpeli,idlocal, accion = "nuevo", rev;
    Button btnagregar, btncargarvideo;
    DB miconexion;
    TextView temp;
    utilidades miUrl;
    detectarInternet di;
    private static final int RPQ= 100;
    private static final int RIG= 101;
    private static final int RVD= 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregarpeliculas);

        miconexion = new DB(getApplicationContext(),"",null,1);
        btnregresar = findViewById(R.id.btnregresar);
        btncargarvideo = findViewById(R.id.btncargarvideo);
        imgpeli = findViewById(R.id.imgfotopelicula);
        vdipeli = findViewById(R.id.vdipelicula);
        btnagregar = findViewById(R.id.btnguardarpelicula);

        btnregresar.setOnClickListener(v -> {
            regresarmainactivity();
        });

        imgpeli.setOnClickListener(v -> {
            abrirgaleriaimagen();
        });

        btncargarvideo.setOnClickListener(v -> {
            abrirgaleriavideo();
        });

        btnagregar.setOnClickListener(v -> {
           agregar();
        });

            permisos();
            mostrardatos();
            controles();
    }


    private void agregar() {
        try {
            temp = findViewById(R.id.txtTitulo);
            String titulo = temp.getText().toString();

            temp = findViewById(R.id.txtsinopsis);
            String sinopsis = temp.getText().toString();

            temp = findViewById(R.id.txtduracion);
            String duracion = temp.getText().toString();

            temp = findViewById(R.id.txtprecio);
            String precio = temp.getText().toString();

            JSONObject datospelis = new JSONObject();
            if(accion.equals("modificar") && idpeli.length()>0 && rev.length()>0 ){
                datospelis.put("_id",idpeli);
                datospelis.put("_rev",rev);
            }



            datospelis.put("titulo",titulo);
            datospelis.put("sinopsis",sinopsis);
            datospelis.put("duracion",duracion);
            datospelis.put("precio",precio);

            if (urlfoto != ""){
                datospelis.put("urlfoto",urlfoto);
            }else {
                urlfoto = "404nofound";
                datospelis.put("urlfoto",urlfoto);
            }
            if (urlvideo != ""){
                datospelis.put("urltriler",urlvideo);
            }else {
                urlvideo = "404nofound";
                datospelis.put("urlfoto",urlfoto);
            }

            String[] datos = {idlocal, titulo, sinopsis, duracion, precio, urlfoto, urlvideo };

            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()) {
                enviarDatos guardarpelis = new enviarDatos(getApplicationContext());
                String resp = guardarpelis.execute(datospelis.toString()).get();
            }

            miconexion.administracion_de_pelis(accion, datos);
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
                idpeli = datos.getString("_id");

                rev = datos.getString("_rev");

                temp = findViewById(R.id.txtTitulo);
                temp.setText(datos.getString("titulo"));

                temp = findViewById(R.id.txtsinopsis);
                temp.setText(datos.getString("sinopsis"));

                temp = findViewById(R.id.txtduracion);
                temp.setText(datos.getString("duracion"));

                temp = findViewById(R.id.txtprecio);
                temp.setText(datos.getString("precio"));

                urlfoto =  datos.getString("urlfoto");
                urlvideo =  datos.getString("urltriler");



                imgpeli.setImageURI(Uri.parse(urlfoto));
                vdipeli.setVideoURI(Uri.parse(urlvideo));

            }
        }catch (Exception ex){
            mensajes(ex.getMessage());

        }
    }

    private void permisos() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        if (ActivityCompat.checkSelfPermission(agregarpelicula.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
         }else {
            ActivityCompat.requestPermissions(agregarpelicula.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},RPQ);
        }
    }else {
      }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataimagen) {
            if (resultCode == Activity.RESULT_OK && dataimagen != null) {
                if (requestCode == RIG) {
                Uri photo = dataimagen.getData();
                imgpeli.setImageURI(photo);

                urlfoto = getRealUrl(this,photo);


            }else if (requestCode == RVD){
                    Uri video = dataimagen.getData();
                    vdipeli.setVideoURI(video);

                    urlvideo = getRealUrl(this,video);
                }
        }
        super.onActivityResult(requestCode, resultCode, dataimagen);
    }

    private void abrirgaleriaimagen(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT );
        i.setType("image/*");
        startActivityForResult(i, RIG);
    }

    private void abrirgaleriavideo(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT );
        i.setType("video/*");
        startActivityForResult(i, RVD);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode== RPQ){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }else{
                mensajes("Por favor dame los permisos");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void regresarmainactivity() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void controles(){
        //Controles de video
        MediaController mediaController = new MediaController(this);
        vdipeli.setMediaController(mediaController);
        mediaController.setAnchorView(vdipeli);
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
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
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