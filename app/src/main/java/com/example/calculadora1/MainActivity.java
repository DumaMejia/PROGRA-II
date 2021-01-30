package com.example.calculadora1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void calcular(View view){
        TextView tempVal = (TextView)findViewById(R.id.txtnum1);
        double  num1 = Double.parseDouble(tempVal.getText().toString());

        tempVal = (TextView)findViewById(R.id.txtnum2);
        double num2 = Double.parseDouble(tempVal.getText().toString());

        double respuesta = 1;
        RadioButton optoperacionesbasicas = findViewById(R.id.optSuma);
        if(optoperacionesbasicas.isChecked() ){
            respuesta = num1 + num2;
        }
        optoperacionesbasicas = findViewById(R.id.optResta);
        if(optoperacionesbasicas.isChecked() ){
            respuesta = num1 - num2;
        }
        optoperacionesbasicas = findViewById(R.id.optMultiplicacion);
        if(optoperacionesbasicas.isChecked() ){
            respuesta = num1 * num2;
        }
        optoperacionesbasicas = findViewById(R.id.optDivision);
        if(optoperacionesbasicas.isChecked() ){
            respuesta = num1 / num2;
        }
        optoperacionesbasicas = findViewById(R.id.optFactorial);
        if(optoperacionesbasicas.isChecked() ){
            for (int i=2; i<=num1; i++){
                respuesta *=i;
            }

        }
        Spinner cboopbasicos = findViewById(R.id.cboOperacionesBasicas);
        switch (cboopbasicos.getSelectedItemPosition()){
            case 0:
                respuesta = num1 + num2;
                break;
            case 1:
                respuesta = num1 - num2;
                break;
            case 2:
                respuesta = num1 * num2;
                break;
            case 3:
                respuesta = num1 / num2;
                break;
        }
    }
}