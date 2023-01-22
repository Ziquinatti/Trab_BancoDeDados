package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.example.votosbrasil.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Toolbar app_bar;

    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();
        trocarFragment(new InicioFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_ini:
                    trocarFragment(new InicioFragment());
                    break;
                case R.id.menu_graf:
                    trocarFragment(new GraficosFragment());
                    break;
                case R.id.menu_add_votos:
                    trocarFragment(new AddVotosFragment());
                    break;
            }
            return true;
        });

        binding.mainAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.bt_exit:
                    sair();
                    break;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    private void trocarFragment(Fragment fragment){
        Bundle data = new Bundle();
        data.putInt("id", user.id);
        data.putString("nome", user.nome);
        fragment.setArguments(data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void sair(){
        user = null;
        Intent intent = new Intent(MainActivity.this, FormLogin.class);
        finish();
        startActivity(intent);
    }

    private void iniciarComponentes(){
        app_bar = findViewById(R.id.main_app_bar);
        setSupportActionBar(app_bar);

        user = new Usuario();
        user = (Usuario) getIntent().getSerializableExtra("user");
        //user.id = 1;
        //user.nome = "TestBot";
        System.out.println("ID: " + user.id);
        System.out.println("NOME: " + user.nome);
        System.out.println("EMAIL: " + user.email);
        System.out.println("SENHA: " + user.senha);
    }
}