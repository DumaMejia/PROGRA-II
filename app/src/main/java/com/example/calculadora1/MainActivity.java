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

        tbhConversores.addTab(tbhConversores.newTabSpec("Monedas").setContent(R.id.tabArea).setIndicator("AREA"));
        tbhConversores.addTab(tbhConversores.newTabSpec("Longitud").setContent(R.id.tabAgua).setIndicator("AGUA"));

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
            { 0.092903, 0.00367, 0.111111,0.00014774656489,0.000013292827545,0.000009290304},
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
