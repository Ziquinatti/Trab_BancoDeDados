package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
        trocarFragment(new InicioFragment());

       iniciarComponentes();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    private void trocarFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void iniciarComponentes(){
        app_bar = findViewById(R.id.main_app_bar);
        setSupportActionBar(app_bar);

        user = (Usuario) getIntent().getSerializableExtra("user");
        System.out.println("ID: " + user.id);
        System.out.println("NOME: " + user.nome);
        System.out.println("EMAIL: " + user.email);
        System.out.println("SENHA: " + user.senha);
    }
}