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


    FloatingActionButton btnregresar;
    String accion = "nuevo";
    Button btnagregar;
    DB miconexion;
    TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        miconexion = new DB(getApplicationContext(),"",null,1);
        btnregresar = findViewById(R.id.btnregresar);
        btnagregar = findViewById(R.id.btnguardar);

        btnregresar.setOnClickListener(v -> {
            regresarmainactivity();
        });

        btnagregar.setOnClickListener(v -> {
            agregar();
        });
    }

    private void agregar() {
        try {
            temp = findViewById(R.id.txtnombres);
            String nombres = temp.getText().toString().trim();

            temp = findViewById(R.id.txtapellidos);
            String apellidos = temp.getText().toString().trim();

            temp = findViewById(R.id.txtusuario);
            String usuario = temp.getText().toString().trim();

            temp = findViewById(R.id.txtcontra);
            String contra = temp.getText().toString().trim();

            String[] datos = {nombres, apellidos, usuario, contra};
            miconexion.agregar_usuario(accion, datos);

            mensajes("Registro guardado con exito.");
            regresarmainactivity();
        }catch (Exception w){
            mensajes(w.getMessage());
        }
    }
    private void regresarmainactivity() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void mensajes(String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }
}