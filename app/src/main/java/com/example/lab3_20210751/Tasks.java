package com.example.lab3_20210751;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3_20210751.Beans.ToDo;
import com.example.lab3_20210751.Beans.User;

import java.util.ArrayList;
import java.util.List;

public class Tasks extends AppCompatActivity {

    ArrayList<String> listaTareas1 = new ArrayList<>();
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tasks);

        user = (User) getIntent().getSerializableExtra("user");
        List<ToDo> todosList = (List<ToDo>) getIntent().getSerializableExtra("todosList");


        for(ToDo toDo: todosList){
            String textoSpinner;

            if(toDo.isCompleted()){
                textoSpinner = toDo.getTodo() + "- Completado";
            }else{
                textoSpinner = toDo.getTodo() + "- No Completado";
            }
            listaTareas1.add(textoSpinner);
        }

        String[] listaTareas = listaTareas1.toArray(new String[0]);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaTareas
        );
        spinner.setAdapter(adapter);


        TextView textito = findViewById(R.id.textUserTask);
        textito.setText("Ver tareas de " + user.getFirstName());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_icono,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        User user = (User) getIntent().getSerializableExtra("user");
        Intent intent = new Intent();
        intent.putExtra("user",user);
        setResult(RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logout){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId()==android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("user",user);
            setResult(RESULT_OK,intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}