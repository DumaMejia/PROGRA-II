package com.example.calculadora1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

public class ver extends AppCompatActivity {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    FloatingActionButton btnregresar;
    ImageView imgpeli;
    String urlfoto, idprod,idlocal, accion = "nuevo", rev;
    TextView temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);


        imgpeli = findViewById(R.id.imgfotopelicula);
        btnregresar = findViewById(R.id.btnregresar);
        btnregresar.setOnClickListener(v -> {
            regresarmainactivity();
        });

try {
    mostrardatos();
}catch (Exception e){

}
    }


    private void mostrardatos() {
        try {
            Bundle recibirparametros = getIntent().getExtras();
            accion = recibirparametros.getString("accion");
            idlocal = recibirparametros.getString("idlocal");
            if(accion.equals("ver")){
                JSONObject datos = new JSONObject(recibirparametros.getString("datos")).getJSONObject("value");
                idprod = datos.getString("_id");

                rev = datos.getString("_rev");

                temp = findViewById(R.id.txtnom1);
                temp.setText(datos.getString("nombre"));

                temp = findViewById(R.id.txtdesc1);
                temp.setText(datos.getString("descripcion"));

                temp = findViewById(R.id.txtpresen1);
                temp.setText(datos.getString("presentacion"));

                temp = findViewById(R.id.txtprecio1);
                temp.setText(datos.getString("precio"));

                urlfoto =  datos.getString("urlfoto");

                imgpeli.setImageURI(Uri.parse(urlfoto));

            }
        }catch (Exception ex){

        }
    }



    private void regresarmainactivity() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

}