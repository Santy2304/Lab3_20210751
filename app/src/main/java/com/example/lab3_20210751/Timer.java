package com.example.lab3_20210751;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3_20210751.Beans.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Timer extends AppCompatActivity {

    TextView temporizador;
    //Clase tomada de video y chatGPT para implementar temporizador
    CountDownTimer timer;
    boolean estaCorriendo = false;
    long tiempo;
    ImageButton actionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);
        User user = (User) getIntent().getSerializableExtra("user");

        TextView fullname = findViewById(R.id.name);
        fullname.setText(user.getFirstName() +" " + user.getLastName());

        TextView email = findViewById(R.id.email);
        email.setText(user.getEmail());

        String gender = user.getGender();
        ImageView genderIcon = findViewById(R.id.iconGender);

        if(gender.equals("male")){
            genderIcon.setImageResource(R.drawable.man_24px);
        }else{
            genderIcon.setImageResource(R.drawable.woman_24px);
        }


        actionButton = findViewById(R.id.actionButton);
        temporizador=findViewById(R.id.runningTime);
        actionButton.setOnClickListener(view -> {
            if (!estaCorriendo) {
                iniciarCuentaAtras();
            }else{
                reiniciar();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void iniciarCuentaAtras(){
        tiempo = 60000; //debe ser 1500000
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
                actionButton.setImageResource(R.drawable.play_arrow_24px);
                mostrarDiolog();
            }
        }.start();
        estaCorriendo=true;
        actionButton.setImageResource(R.drawable.replay_24px);
    }


    public void mostrarTiempo(){
        int minutos = (int) (tiempo / 1000) / 60;
        int segundos = (int) (tiempo / 1000) % 60;

        //Código tomado de chatGPT para formatear el texto
        String tiempoTexto = String.format("%02d:%02d", minutos, segundos);

        temporizador.setText(tiempoTexto);
    }

    public void reiniciar() {
        timer.cancel();
        iniciarCuentaAtras();
    }


    public void mostrarDiolog(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("¡Felicidades!")
                .setMessage("Empezo el tiempo de descanso!")
                .setPositiveButton("Entendido", (dialog, which) -> {

                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_icono,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logout){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}