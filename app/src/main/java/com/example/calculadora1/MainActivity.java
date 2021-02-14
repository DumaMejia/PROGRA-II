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
        tbhConversores.addTab(tbhConversores.newTabSpec("Masa").setContent(R.id.tabMasa).setIndicator("P"));
        tbhConversores.addTab(tbhConversores.newTabSpec("Almacenamiento").setContent(R.id.tabMasa).setIndicator("A"));
        tbhConversores.addTab(tbhConversores.newTabSpec("Tiempo").setContent(R.id.tabMasa).setIndicator("T"));
        tbhConversores.addTab(tbhConversores.newTabSpec("Temperatura").setContent(R.id.tabMasa).setIndicator("°F"));
        tbhConversores.addTab(tbhConversores.newTabSpec("Volumen").setContent(R.id.tabMasa).setIndicator("V"));
        tbhConversores.addTab(tbhConversores.newTabSpec("Area").setContent(R.id.tabMasa).setIndicator("m²"));


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
    }

    }

class conversores{
    double[][] conversor = {
            {1.0,8.75,7.77, 24.03,34.8,611.10,1746296.00,0.000021,104.96,0.83},/*Monedas*/
            {1.0, 100.0,39.37,3.28,0.001,0.000621371,1.09361,1000,1e+6,1e+9},/*Longitud*/
            {1.0,1000,1e+6,1e+9,1e+12,0.984207,1.10231,157.473,2204.62,35274},/*Masa*/
            {1.0,1000,0.001,8e+6,1e-6,1e-9,0.953674,976.563,0.000931323,0.008},/*Almacenamiento*/
            {1.0,86400,1440,24,0.142857,0.0328767,0.00273973,0.000273973,2.7397e-5,8.64e+7},/*Tiempo*/
            {1.0,1000,0.219969,0.879877,1.75975,3.51951,35.1951,56.3121,168.936,0.0353147},/*Volumen*/
            {1.0,1000000,10000,0.0001,0.000001,1550.003,10.76391,1.19599,0.000247,0.000000386102159}/*Area*/
    };
    public double convertir(int opcion, int de, int a, double cantidad){
        return conversor[opcion][a] / conversor[opcion][de] * cantidad;
    }
}
