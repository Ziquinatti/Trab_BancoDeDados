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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.HashMap;
import java.util.Map;

public class FormLogin extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private ProgressBar pb_entrando;
    private TextView text_tela_cadastro;
    private Handler mHandler = new Handler();

    private Usuario user;

    //private String HOST = "http://172.17.10.193/trab_final";
    private String HOST = "http://192.168.42.161/trab_final";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        //getSupportActionBar().hide();
        IniciarComponentes();

        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FormLogin.this, FormCadastro.class);
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
                pb_entrando.setVisibility(View.VISIBLE);
                String url = HOST + "/login.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("sucesso")) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(FormLogin.this, MainActivity.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000);
                        } else if (response.equals("erro")) {
                            Toast.makeText(FormLogin.this, R.string.user_not_find, Toast.LENGTH_SHORT).show();
                            pb_entrando.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pb_entrando.setVisibility(View.INVISIBLE);
                        Toast.makeText(FormLogin.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("email", user.email);
                        data.put("senha", user.senha);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        } else {
            Toast.makeText(FormLogin.this, R.string.con_disable, Toast.LENGTH_LONG).show();
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
