package com.example.lab3_20210751.Workers;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.lab3_20210751.R;

public class TimerWorker extends Worker {


    public TimerWorker(Context context,WorkerParameters workerParams) {
        super(context, workerParams);
    }

    long tiempo;
    TextView temporizador;
    @NonNull
    @Override
    public Result doWork() {
        temporizador= temporizador.findViewById(R.id.runningTime);
        tiempo = 300000;
        CountDownTimer Fivetimer = new CountDownTimer(tiempo, 1000){

            @Override
            public void onTick(long l) {
                tiempo = l;
                mostrarTiempo();
            }

            @Override
            public void onFinish() {
                temporizador.setText("00:00");

            }
        };
        Fivetimer.start();
        return Result.success();
    }

    public void mostrarTiempo(){
        int minutos = (int) (tiempo / 1000) / 60;
        int segundos = (int) (tiempo / 1000) % 60;

        //Código tomado de chatGPT para formatear el texto
        String tiempoTexto = String.format("%02d:%02d", minutos, segundos);

        temporizador.setText(tiempoTexto);
    }
}
