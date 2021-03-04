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
        tbhConversores.addTab(tbhConversores.newTabSpec("Longitud").setContent(R.id.tabLongitud).setIndicator("L"));

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
        btnConvertir = findViewById(R.id.btnCalcularL);
        btnConvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tempVal = findViewById(R.id.txtCantidadL);
                    double cantidad = Double.parseDouble(tempVal.getText().toString());

                    spnOpcionDe = findViewById(R.id.cboDeL);
                    spnOpcionA = findViewById(R.id.cboAL);
                    tempVal = findViewById(R.id.lblRespuestaL);

                    tempVal.setText("Respuesta: " + miConversor.convertir(1, spnOpcionDe.getSelectedItemPosition(), spnOpcionA.getSelectedItemPosition(), cantidad));
                }catch (Exception e){
                    tempVal = findViewById(R.id.lblRespuestaL);
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
    public double temperatura(int de, int a, double cantidad){
        double respuesta = 0;
        /*Celsius*/
        if (de == 0) {
            if (a == 0) {
                respuesta = cantidad;
            } else if (a == 1) {
                respuesta = (cantidad * 9 / 5) + 32;
            } else if (a == 2) {
                respuesta = cantidad + 273.15;
            }
        }
        /*Fahrenheit*/
        if (de == 1) {
            if (a == 0) {
                respuesta = (cantidad - 32) * 5 / 9;
            } else if (a == 1) {
                respuesta = cantidad;
            } else if (a == 2) {
                respuesta = (cantidad - 32) * 5 / 9 + 273.15;
            }
        }
        /*Kelvin*/
        if (de == 2) {
            if (a == 0) {
                respuesta = cantidad - 273.15;
            } else if (a == 1) {
                respuesta = (cantidad - 273.15) * 9 / 5 + 32;
            } else if (a == 2) {
                respuesta = cantidad;
            }
        }

        return respuesta;
    }

}
