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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgregarProducto extends AppCompatActivity {

    FloatingActionButton btnAtras;
    ImageView imgFotoProducto;
    Intent tomarFotoIntent;
    String urlCompletaImg, idProducto,accion="nuevo";
    Button btn;
    DB miBD;
    TextView tempVal;

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
            tempVal = findViewById(R.id.txtNombre);
            String nombre = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtTelefono);
            String tel = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDireccion);
            String direccion = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtEmail);
            String email = tempVal.getText().toString();

            String[] datos = {idProducto,nombre,tel,direccion,email,urlCompletaImg};
            miBD.administracion_productos(accion,datos);
            mostrarMsgToast("Registro guardado con exito.");

            mostrarVistaPrincipal();
        });
        mostrarDatosAmigos();
    }

    private void mostrarDatosAmigos() {
        try{
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if(accion.equals("modificar")){
                String[] datos = recibirParametros.getStringArray("datos");

                idAmigo = datos[0];

                tempVal = findViewById(R.id.txtNombre);
                tempVal.setText(datos[1]);

                tempVal = findViewById(R.id.txtTelefono);
                tempVal.setText(datos[2]);

                tempVal = findViewById(R.id.txtDireccion);
                tempVal.setText(datos[3]);

                tempVal = findViewById(R.id.txtEmail);
                tempVal.setText(datos[4]);

                urlCompletaImg = datos[5];
                Bitmap bitmap = BitmapFactory.decodeFile((urlCompletaImg));
                imgFotoAmigo.setImageBitmap(bitmap);
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
            File photoAmigo = null;
            try{
                photoAmigo = crearImagenAmigo();
            }catch (Exception e){
                mostrarMsgToast(e.getMessage());
            }
            if( photoAmigo!=null ){
                try{
                    Uri uriPhotoAmigo = FileProvider.getUriForFile(AgregarProducto.this, "com.ugb.miprimerproyecto.fileprovider",photoAmigo);
                    tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhotoAmigo);
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
                imgFotoAmigo.setImageBitmap(imagenBitmap);
            }
        }catch (Exception e){
            mostrarMsgToast(e.getMessage());
        }
    }
    private File crearImagenAmigo() throws Exception {
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