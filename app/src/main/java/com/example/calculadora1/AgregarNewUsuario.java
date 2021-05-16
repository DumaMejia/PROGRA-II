package com.example.calculadora1;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AgregarNewUsuario extends AppCompatActivity {
    FloatingActionButton btnAtras;
    String  idusu,accion="nuevo";
    Button btn;
    DB miconexion;
    TextView tempVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuarios);

        miconexion = new DB(getApplicationContext(),"",null,1);
        btnAtras = findViewById(R.id.btnregresar);
        btnAtras.setOnClickListener(v->{
            mostrarVistaLogin();
        });

        btn = findViewById(R.id.btnguardar);
        btn.setOnClickListener(v->{
            agregar();
        });

    }

    private void agregar(){

        tempVal = findViewById(R.id.txtnombres);
        String nombres = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtapellidos);
        String apellidos = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtusuario1);
        String usuario = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtcontra1);
        String contra = tempVal.getText().toString();

        String[] datos = {idusu,nombres,apellidos,usuario,contra};
        miconexion.administracion_usuarios(accion,datos);
        mostrarMsgToast("Registro guardado con exito.");

        mostrarVistaLogin();
    }


    private void mostrarVistaLogin(){
        Intent iprincipal = new Intent(getApplicationContext(), Login.class);
        startActivity(iprincipal);
    }

    private void mostrarMsgToast(String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }
}