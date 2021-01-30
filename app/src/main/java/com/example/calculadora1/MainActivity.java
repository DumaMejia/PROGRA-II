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
            case 4:
                for (int i=2; i<=num1; i++){
                    respuesta *=i;
                }
                break;
            case 5:
                respuesta = ((num1 / num2) * 100);
                break;
            case 6:
                respuesta = Math.pow(num1, num2);
                break;
            case 7:
                Double raiz = Math.pow(num1, 1/num2);
                respuesta = raiz;
                break;
            case 8:
                respuesta = num1 % num2;
                break;
            case 9:
                if (num1 > num2){
                    respuesta = num1;
                }
                else{
                    respuesta = num2;
                }
                break;
        }
        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("respuesta: " + respuesta);
    }
}