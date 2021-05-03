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
    VideoView vdipeli;
    String urlfoto, urlvideo,idpeli,idlocal, accion = "nuevo", rev;
    TextView temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);


        vdipeli = findViewById(R.id.vdipelicula);
        imgpeli = findViewById(R.id.imgfotopelicula);
        btnregresar = findViewById(R.id.btnregresar);
        btnregresar.setOnClickListener(v -> {
            regresarmainactivity();
        });

try {
    mostrardatos();
    controles();
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

        }
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

}