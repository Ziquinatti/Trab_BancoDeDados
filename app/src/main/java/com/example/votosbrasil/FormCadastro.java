package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Button;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class FormCadastro extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_senha, edit_confSenha;
    private Button bt_cadastrar;
    private Handler mHandler = new Handler();

    private Usuario user;

    private String HOST = "http://172.17.10.193/trab_final";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();

        bt_cadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CadastrarUsuario(v);
            }
        });
    }

    private void CadastrarUsuario(View v){
        if(NetworkUtils.isConnected()){
            if(validarCampos()){
                //Cadastrar novo usuário
                String url = HOST + "/cadastro.php";

                Ion.with(FormCadastro.this)
                        .load(url)
                        .setBodyParameter("nome", user.nome)
                        .setBodyParameter("email", user.email)
                        .setBodyParameter("senha", user.senha)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if(result.get("CADASTRO").getAsString().equals("OK")){
                                    Snackbar snackbar = Snackbar.make(v, R.string.cad_ok, Snackbar.LENGTH_SHORT);
                                    snackbar.setBackgroundTint(Color.WHITE);
                                    snackbar.setTextColor(Color.BLACK);
                                    snackbar.show();

                                    user.id = result.get("ID").getAsInt();

                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(FormCadastro.this, MainActivity.class);
                                            intent.putExtra("user", user);
                                            startActivity(intent);
                                        }
                                    }, 4000);
                                } else {
                                    Snackbar snackbar = Snackbar.make(v, R.string.cad_error, Snackbar.LENGTH_SHORT);
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
        edit_nome.setError(null);
        edit_email.setError(null);
        edit_senha.setError(null);
        edit_confSenha.setError(null);

        user = new Usuario();

        user.nome = edit_nome.getText().toString();
        user.email = edit_email.getText().toString();
        user.senha = edit_senha.getText().toString();
        user.confSenha = edit_confSenha.getText().toString();

        boolean ok = true;

        if(!user.senha.equals(user.confSenha)){
            edit_senha.setError(getString(R.string.pass_not_match));
            edit_confSenha.setError(getString(R.string.pass_not_match));
            edit_senha.requestFocus();
            ok = false;
        }

        if(user.confSenha.isEmpty()){
            edit_confSenha.setError(getString(R.string.empty_field));
            edit_confSenha.requestFocus();
            ok = false;
        }
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
        if(user.nome.isEmpty()){
            edit_nome.setError(getString(R.string.empty_field));
            edit_nome.requestFocus();
            ok = false;
        }

        return ok;
    }

    private void IniciarComponentes(){
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        edit_confSenha = findViewById(R.id.edit_confSenha);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
    }
}