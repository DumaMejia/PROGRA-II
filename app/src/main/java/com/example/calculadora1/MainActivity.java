package com.example.calculadora1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TabHost tbhConversores;
    Button btnConvertir;
    TextView tempVal;
    Spinner spnOpcionDe, spnOpcionA;
    conversores miConversor = new conversores();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbhConversores = findViewById(R.id.tbhConversores);
        tbhConversores.setup();

        tbhConversores.addTab(tbhConversores.newTabSpec("Monedas").setContent(R.id.tabMonedas).setIndicator("M"));
        tbhConversores.addTab(tbhConversores.newTabSpec("Longitud").setContent(R.id.tabAgua).setIndicator("A"));

        btnConvertir = findViewById(R.id.btnCalcular);
        btnConvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tempVal = (TextView) findViewById(R.id.txtcantidad);
                    double cantidad = Double.parseDouble(tempVal.getText().toString());

                    spnOpcionDe = findViewById(R.id.cboDe);
                    spnOpcionA = findViewById(R.id.cboA);
                    tempVal = findViewById(R.id.lblRespuesta);
                    tempVal.setText("Respuesta: " + miConversor.convertir(0, spnOpcionDe.getSelectedItemPosition(), spnOpcionA.getSelectedItemPosition(), cantidad));
                }catch (Exception e){
                    tempVal = findViewById(R.id.lblRespuesta);
                    tempVal.setText("Por favor ingrese los valores correspondiente");
                    Toast.makeText(getApplicationContext(), "Por ingrese los valores correspondiente "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                    /*Snackbar snackbar = Snackbar.make(contenidoView, "Por favor ingrese los valores correspondiente", Snackbar.LENGTH_LONG);
                    snackbar.show();*/
                }
            }
        });
        btnConvertir = findViewById(R.id.btnCalcularAgua);
        btnConvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tempVal = findViewById(R.id.txtCantidadAgua);
                    double cantidad = Double.parseDouble(tempVal.getText().toString());

                    tempVal = findViewById(R.id.lblRespuestaAgua);

                    tempVal.setText("Respuesta: " + miConversor.convertiragua(cantidad));
                }catch (Exception e){
                    tempVal = findViewById(R.id.lblRespuestaAgua);
                    tempVal.setText("Por favor ingrese los valores correspondiente");
                    Toast.makeText(getApplicationContext(), "Por ingrese los valores correspondiente "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    }

class conversores{
    double[][] conversor = {
            {1.0,8.75,7.77, 24.03,34.8,611.10,1746296.00,0.000021,104.96,0.83},/*Monedas*/
            {1.0, 100.0,39.37,3.28,0.001,0.000621371,1.09361,1000,1e+6,1e+9},/*Longitud*/
    };
    public double convertir(int opcion, int de, int a, double cantidad){
        return conversor[opcion][a] / conversor[opcion][de] * cantidad;
    }
    public double convertiragua( double cantidad){
        double respuesta = 0;
        double var1 = 0;
        double var2 = 0;

        if (cantidad <= 18) {
            respuesta = 6;
        }
        if (cantidad > 18 && cantidad <= 28){
            respuesta = (cantidad - 18) * 0.45 + 6;
        }
        if (cantidad > 28){
            var1 = (cantidad - 28) * 0.65;
            var2 = (28-18) * 0.45;
            respuesta = var1 + var2 + 6;
        }
        return respuesta;
    }

}
