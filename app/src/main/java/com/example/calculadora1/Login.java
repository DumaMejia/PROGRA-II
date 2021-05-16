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

public class Login extends AppCompatActivity {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    FloatingActionButton btnregresar;
    String idusu, accion = "nuevo", rev;
    Button btniniciar, btnregistrar;
    DB miconexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        miconexion = new DB(getApplicationContext(),"",null,1);
        btnregresar = findViewById(R.id.btnregresar);
        btnregistrar = findViewById(R.id.btnAgregarUsuario);
        btniniciar = findViewById(R.id. IniciarSesion);

        btnregresar.setOnClickListener(v -> {
            mostrarVistaMain();
        });

        btniniciar.setOnClickListener(v -> {
            iniciarsesion();
        });

        btnregistrar.setOnClickListener(v -> {
            mostrarVistaRegistrar();
        });
        iniciarsesion();
    }
    private void mostrarVistaMain(){
        Intent iprincipal = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iprincipal);
    }
    private void mostrarVistaRegistrar(){
        Intent principal = new Intent(getApplicationContext(), AgregarNewUsuario.class);
        startActivity(principal);
    }

    private void iniciarsesion(){
        try {
            int a = 0;
            TextView tempval = findViewById(R.id.txtUsuario);
            TextView tempval1 = findViewById(R.id.txtcontra);
            miconexion = new DB(getApplicationContext(), "", null, 1);
            Cursor datosusuario = miconexion.administracion_usuarios("consultar", null);
            if( datosusuario.moveToFirst() ){
                do {
                    if (datosusuario.getString(3).equals(tempval.getText())&&datosusuario.getString(4).equals(tempval1.getText()));
                    a++;
                }while (datosusuario.moveToNext());
                if (a>0){
                    Toast.makeText(this, "si se logeo jaja",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, "nel te falla compa",Toast.LENGTH_LONG).show();
                }
            }

        }catch (Exception e){
            mostrarMsgToask(e.getMessage());
        }
    }
    private void mostrarMsgToask(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }

}