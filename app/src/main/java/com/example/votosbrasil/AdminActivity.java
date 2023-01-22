package com.example.votosbrasil;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.votosbrasil.databinding.ActivityAdminBinding;
import com.example.votosbrasil.databinding.ActivityMainBinding;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private Toolbar app_bar;
    private Button bt_cad_partido, bt_cad_candidato, bt_cad_cidade, bt_analisar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        binding.mainAdmAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.bt_exit:
                    sair();
                    break;
            }
            return true;
        });

        bt_cad_partido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, CadPartidoActivity.class);
                startActivity(intent);
            }
        });

        bt_cad_candidato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, CadCandidatoActivity.class);
                startActivity(intent);
            }
        });

        bt_cad_cidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, CadMunicipioActivity.class);
                startActivity(intent);
            }
        });

        bt_analisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AnaliseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    private void sair(){
        Intent intent = new Intent(AdminActivity.this, FormLogin.class);
        finish();
        startActivity(intent);
    }

    private void iniciarComponentes(){
        app_bar = findViewById(R.id.main_adm_app_bar);
        setSupportActionBar(app_bar);

        bt_cad_partido = findViewById(R.id.bt_cad_partido);
        bt_cad_candidato = findViewById(R.id.bt_cad_candidato);
        bt_cad_cidade = findViewById(R.id.bt_cad_cidade);
        bt_analisar = findViewById(R.id.bt_analisar);
    }
}