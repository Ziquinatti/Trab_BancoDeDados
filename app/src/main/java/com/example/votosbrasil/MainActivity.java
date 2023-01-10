package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        user = (Usuario) getIntent().getSerializableExtra("user");

        System.out.println("Usuário: " + user.id +"/"+ user.nome +"/"+ user.email +"/"+ user.senha);
    }
}