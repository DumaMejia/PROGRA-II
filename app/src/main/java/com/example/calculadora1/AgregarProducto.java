package com.example.calculadora1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgregarProducto extends AppCompatActivity {

    FloatingActionButton btnAtras;
    ImageView imgFotoProducto;
    Intent tomarFotoIntent;
    String urlCompletaImg, idProducto, rev,accion="nuevo";
    Button btn;
    DB miBD;
    TextView tempVal;
    utilidades miUrl;
    detectarInternet di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        miBD = new DB(getApplicationContext(),"",null,1);
        btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v->{
            mostrarVistaPrincipal();
        });
        imgFotoProducto = findViewById(R.id.imgFotoProducto);
        imgFotoProducto.setOnClickListener(v->{
            tomarFotoAmigo();
        });
        btn = findViewById(R.id.btnGuardarProducto);
        btn.setOnClickListener(v->{
            try {
            tempVal = findViewById(R.id.txtCodigo);
            String codigo = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDescipcion);
            String descripcion = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtMarca);
            String marca = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtPresentacion);
            String presentacion = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtPrecio);
            String precio = tempVal.getText().toString();

            JSONObject datosProducto = new JSONObject();
            if(accion.equals("modificar") && idProducto.length()>0 && rev.length()>0 ){
                datosProducto.put("_id",idProducto);
                datosProducto.put("_rev",rev);
            }
            datosProducto.put("codigo",codigo);
            datosProducto.put("descripcion",descripcion);
            datosProducto.put("marca",marca);
            datosProducto.put("presentacion",presentacion);
            datosProducto.put("precio",precio);
            datosProducto.put("urlPhoto",urlCompletaImg);
            String[] datos = {idProducto, codigo, descripcion, marca, presentacion, precio, urlCompletaImg};

            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()) {
                enviarDatosProductos objGuardarAmigo = new enviarDatosProductos(getApplicationContext());
                String resp = objGuardarAmigo.execute(datosProducto.toString()).get();
            }
            miBD.administracion_productos(accion, datos);
            mostrarMsgToast("Registro guardado con exito.");

            mostrarVistaPrincipal();
        }catch (Exception e){
            mostrarMsgToast(e.getMessage());
        }

        });
        mostrarDatosProducto();
    }

    private void mostrarDatosProducto() {

        try{
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if(accion.equals("modificar")){
                JSONObject datos = new JSONObject(recibirParametros.getString("datos")).getJSONObject("value");

                idProducto = datos.getString("_id");
                rev = datos.getString("_rev");

                tempVal = findViewById(R.id.txtCodigo);
                tempVal.setText(datos.getString("codigo"));

                tempVal = findViewById(R.id.txtDescipcion);
                tempVal.setText(datos.getString("descripcion"));

                tempVal = findViewById(R.id.txtMarca);
                tempVal.setText(datos.getString("marca"));

                tempVal = findViewById(R.id.txtPresentacion);
                tempVal.setText(datos.getString("presentacion"));

                tempVal = findViewById(R.id.txtPrecio);
                tempVal.setText(datos.getString("precio"));

                urlCompletaImg = datos.getString("urlPhoto");
                Bitmap bitmap = BitmapFactory.decodeFile((urlCompletaImg));
                imgFotoProducto.setImageBitmap(bitmap);
            }
        }catch (Exception e){
            mostrarMsgToast(e.getMessage());
        }
    }
    private void mostrarVistaPrincipal(){
        Intent iprincipal = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iprincipal);
    }
    private void tomarFotoAmigo(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if( tomarFotoIntent.resolveActivity(getPackageManager())!=null ){
            File photoProducto = null;
            try{
                photoProducto = crearImagenProducto();
            }catch (Exception e){
                mostrarMsgToast(e.getMessage());
            }
            if( photoProducto!=null ){
                try{
                    Uri uriPhotoProducto = FileProvider.getUriForFile(AgregarProducto.this, "com.example.calculadora1.fileprovider",photoProducto);
                    tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhotoProducto);
                    startActivityForResult(tomarFotoIntent,1);
                }catch (Exception e){
                    mostrarMsgToast(e.getMessage());
                }
            } else {
                mostrarMsgToast("No fue posible tomar la foto");
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if( requestCode==1 && resultCode==RESULT_OK ){
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFotoProducto.setImageBitmap(imagenBitmap);
            }
        }catch (Exception e){
            mostrarMsgToast(e.getMessage());
        }
    }
    private File crearImagenProducto() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreImagen = "imagen_"+ timeStamp +"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( dirAlmacenamiento.exists()==false ){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(nombreImagen,".jpg",dirAlmacenamiento);
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }
    private void mostrarMsgToast(String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }
}