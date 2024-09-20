package com.example.lab3_20210751;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Timer extends AppCompatActivity {

    TextView temporizador;
    CountDownTimer timer;
    boolean estaCorriendo = false;
    long tiempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);


        Button actionButton = findViewById(R.id.actionButton);

        actionButton.setOnClickListener(view -> {
            if (!estaCorriendo) {
                iniciarCuentaAtras();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void iniciarCuentaAtras(){
        tiempo = 1500000;
        timer = new CountDownTimer(tiempo, 1000) {
            @Override
            public void onTick(long tiempoRestante) {
                tiempo = tiempoRestante;
                mostrarTiempo();
            }

            @Override
            public void onFinish() {
                temporizador.setText("00:00");
                estaCorriendo = false;
            }
        }.start();
        estaCorriendo=true;
    }


    public void mostrarTiempo(){
        int minutos = (int) (tiempo / 1000) / 60;
        int segundos = (int) (tiempo / 1000) % 60;

        //Código tomado de chatGPT para formatear el texto
        String tiempoTexto = String.format("%02d:%02d", minutos, segundos);
        temporizador.setText(tiempoTexto);
    }

}