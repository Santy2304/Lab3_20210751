package com.example.lab3_20210751;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;

import com.example.lab3_20210751.Beans.ToDo;
import com.example.lab3_20210751.Beans.TodosResponse;
import com.example.lab3_20210751.Beans.User;
import com.example.lab3_20210751.Service.AuthenticationService;
import com.example.lab3_20210751.Workers.TimerWorker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Timer extends AppCompatActivity {


    User user;
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
        user = (User) getIntent().getSerializableExtra("user");

        TextView fullname = findViewById(R.id.name);
        fullname.setText(user.getFirstName() +" " + user.getLastName());
        int userId = user.getId();

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
                iniciarCuentaAtras(user);
            }else{
                reiniciar(user);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void iniciarCuentaAtras( User user){
        tiempo = 60000; //debe ser 1500000
        timer = new CountDownTimer(tiempo, 1000) {
            @Override
            public void onTick(long tiempoRestante) {
                tiempo = tiempoRestante;
                mostrarTiempo();
            }

            @Override
            public void onFinish() {
                temporizador.setText("05:00");
                TextView textoAux = findViewById(R.id.timerText);
                textoAux.setVisibility(View.INVISIBLE);
                actionButton.setVisibility(View.INVISIBLE);
                actionButton.setEnabled(false);
                estaCorriendo = false;
                actionButton.setImageResource(R.drawable.play_arrow_24px);

                tieneTareas(user);

                WorkRequest workRequest = new OneTimeWorkRequest.Builder(TimerWorker.class).build();
                WorkManager.getInstance(Timer.this).enqueue(workRequest);

                WorkManager.getInstance(Timer.this).getWorkInfoByIdLiveData(workRequest.getId()).observe(Timer.this,workInfo -> {

                  if(workInfo!=null){

                      if (workInfo.getState()== WorkInfo.State.RUNNING){
                          Data progress = workInfo.getProgress();
                          mostrarTiempo2(progress.getLong("tiempoRestante",0));
                      }else if(workInfo.getState()==WorkInfo.State.SUCCEEDED){
                          temporizador.setText("00:00");
                          mostrarDiolog2();
                          actionButton.setVisibility(View.VISIBLE);
                          actionButton.setEnabled(true);
                          actionButton.setImageResource(R.drawable.replay_24px);

                      }
                  }
              });

                //mostrarDiolog();

            }
        }.start();
        estaCorriendo=true;
        actionButton.setImageResource(R.drawable.replay_24px);
    }


    public void mostrarTiempo2(long tiempito){
        int minutos = (int) (tiempito / 1000) / 60;
        int segundos = (int) (tiempito / 1000) % 60;

        //Código tomado de chatGPT para formatear el texto
        String tiempoTexto = String.format("%02d:%02d", minutos, segundos);

        temporizador.setText(tiempoTexto);
    }


    public void mostrarTiempo(){
        int minutos = (int) (tiempo / 1000) / 60;
        int segundos = (int) (tiempo / 1000) % 60;

        //Código tomado de chatGPT para formatear el texto
        String tiempoTexto = String.format("%02d:%02d", minutos, segundos);

        temporizador.setText(tiempoTexto);
    }

    public void reiniciar(User user) {
        timer.cancel();
        iniciarCuentaAtras(user);
    }


    public void mostrarDiolog(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("¡Felicidades!")
                .setMessage("Empezo el tiempo de descanso!")
                .setPositiveButton("Entendido", (dialog, which) -> {

                })
                .show();
    }

    public void mostrarDiolog2(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("¡Atención!")
                .setMessage("Terminó el tiempo de descanso. Dale al botón de reinicio para empezar otro ciclo. ")
                .setPositiveButton("Entendido", (dialog, which) -> {
                })
                .show();
    }

    public void tieneTareas(User user){
        AuthenticationService authenticationService = new Retrofit.Builder()
                .baseUrl("https://dummyjson.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthenticationService.class);

        if(internetConnection()){
            authenticationService.getTodos(user.getId()).enqueue(new Callback<TodosResponse>() {
                @Override
                public void onResponse(Call<TodosResponse> call, Response<TodosResponse> response) {
                    if (response.isSuccessful()) {
                        TodosResponse todosResponse = response.body();
                        List<ToDo> listTodos;
                        if (todosResponse != null) {
                            listTodos = todosResponse.getTodos();
                        } else {
                            listTodos = new ArrayList<>();
                        }
                        if(listTodos.isEmpty()){
                            mostrarDiolog();
                        }else{
                            Intent intent = new Intent(Timer.this, Tasks.class);
                            intent.putExtra("user", user);
                            intent.putExtra("todosList", (Serializable) listTodos);
                            launcher.launch(intent);
                            //Intent intent = new Intent(Timer.this, Tasks.class);
                            //intent.putExtra("user",user);
                            //startActivity(intent);
                        }
                    }else{
                        mostrarDiolog();
                    }
                }

                @Override
                public void onFailure(Call<TodosResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
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

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Intent resultData = o.getData();
                    user = (User) resultData.getSerializableExtra("user");
                }

            }
    );


    //Código tomado de material de clase para probar conexión a internet
    public boolean internetConnection(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        boolean tieneInternet = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        return tieneInternet;
    }
}