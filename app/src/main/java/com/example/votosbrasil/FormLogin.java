package com.example.votosbrasil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class FormLogin extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private ProgressBar pb_entrando;
    private TextView text_tela_cadastro;
    private Handler mHandler = new Handler();

    private Usuario user;

    private String HOST = "http://172.17.10.193/trab_final";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        getSupportActionBar().hide();
        IniciarComponentes();

        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FormLogin.this,FormCadastro.class);
                startActivity(intent);
            }
        });

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Entrar(view);
            }
        });
    }

    private void Entrar(View v){
        if(NetworkUtils.isConnected()){
            if(validarCampos()){
                //Entrar
                String url = HOST + "/login.php";

                Ion.with(FormLogin.this)
                        .load(url)
                        .setBodyParameter("email", user.email)
                        .setBodyParameter("senha", user.senha)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if(result.get("LOGIN").getAsString().equals("OK")){
                                    pb_entrando.setVisibility(View.VISIBLE);
                                    user.id = result.get("ID").getAsInt();
                                    user.nome = result.get("NOME").getAsString();

                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pb_entrando.setVisibility(View.INVISIBLE);
                                            edit_email.setText(null);
                                            edit_senha.setText(null);

                                            Intent intent = new Intent(FormLogin.this, MainActivity.class);
                                            intent.putExtra("user", user);
                                            startActivity(intent);
                                        }
                                    }, 4000);
                                } else {
                                    Snackbar snackbar = Snackbar.make(v, R.string.user_not_find, Snackbar.LENGTH_SHORT);
                                    snackbar.setBackgroundTint(Color.WHITE);
                                    snackbar.setTextColor(Color.BLACK);
                                    snackbar.show();
                                }
                            }
                        });
            }
        } else {
            Snackbar snackbar = Snackbar.make(v, R.string.con_disable, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }
    }

    private boolean validarCampos(){
        edit_email.setError(null);
        edit_senha.setError(null);

        user = new Usuario();

        user.email = edit_email.getText().toString();
        user.senha = edit_senha.getText().toString();

        boolean ok = true;

        if(user.senha.isEmpty()){
            edit_senha.setError(getString(R.string.empty_field));
            edit_senha.requestFocus();
            ok = false;
        }
        if(user.email.isEmpty()){
            edit_email.setError(getString(R.string.empty_field));
            edit_email.requestFocus();
            ok = false;
        }

        return ok;
    }

    private void IniciarComponentes(){
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_entrar = findViewById(R.id.bt_entrar);
        pb_entrando = findViewById(R.id.progressbar);
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
    }
}
