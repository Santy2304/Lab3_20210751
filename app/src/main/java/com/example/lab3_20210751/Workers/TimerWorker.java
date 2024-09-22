package com.example.lab3_20210751.Workers;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.lab3_20210751.R;

public class TimerWorker extends Worker {


    public TimerWorker(Context context,WorkerParameters workerParams) {
        super(context, workerParams);
    }
    Data data;
    long tiempo;
    @NonNull
    @Override
    public Result doWork() {
        tiempo = 300000;

        while (tiempo!=0){
            tiempo = tiempo -1000;
            setProgressAsync(new Data.Builder().putLong("tiempoRestante",tiempo).build());
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                return Result.failure();
            }
        }

        return Result.success(data);
    }


}
