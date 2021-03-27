package com.example.calculadora1;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btn;
    DB miBD;
    ListView ltsAmigos;
    Cursor datosAmigosCursor = null;
    ArrayList<amigos> amigosArrayList=new ArrayList<amigos>();
    ArrayList<amigos> amigosArrayListCopy=new ArrayList<amigos>();
    amigos misAmigos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnAgregarAmigos);
        btn.setOnClickListener(v->{
            agregarAmigos("nuevo", new String[]{});
        });
        obtenerDatosAmigos();
        buscarAmigos();

    }

    }


