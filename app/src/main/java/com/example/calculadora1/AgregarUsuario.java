package com.example.calculadora1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

public class AgregarUsuario extends AppCompatActivity {


    FloatingActionButton btnregresar;
    String idusuario, idlocal, accion = "nuevo", rev;
    Button btnagregar;
    DB miconexion;
    utilidades miUrl;
    detectarInternet di;
    TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuario);

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

            temp = findViewById(R.id.txtdireccion);
            String direccion = temp.getText().toString().trim();

            temp = findViewById(R.id.txttelefono);
            String telefono = temp.getText().toString().trim();

            temp = findViewById(R.id.txtcorreo);
            String correo = temp.getText().toString().trim();

            temp = findViewById(R.id.txtcontra);
            String contra = temp.getText().toString().trim();

            JSONObject datospelis = new JSONObject();
            if(accion.equals("modificar") && idusuario.length()>0 && rev.length()>0 ){
                datospelis.put("_id",idusuario);
                datospelis.put("_rev",rev);
            }



            datospelis.put("nombres",nombres);
            datospelis.put("apellidos",apellidos);
            datospelis.put("direccion",direccion);
            datospelis.put("telefono",telefono);
            datospelis.put("usuario",correo);
            datospelis.put("contra",contra);

            String[] datos = {nombres, apellidos, direccion, telefono, correo, contra};

            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()) {
                enviarDatosUsuarios guardarpelis = new enviarDatosUsuarios(getApplicationContext());
                String resp = guardarpelis.execute(datospelis.toString()).get();
            }

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