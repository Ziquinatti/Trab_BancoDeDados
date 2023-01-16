package com.example.votosbrasil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_senha, edit_confSenha;
    private Button bt_cadastrar;
    private Handler mHandler = new Handler();

    private Usuario user;

    //private String HOST = "http://172.17.10.193/trab_final";
    private String HOST = "http://192.168.42.161/trab_final";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        //getSupportActionBar().hide();
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
                String url = HOST + "/cadastro.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("sucesso")) {
                            Toast.makeText(FormCadastro.this, R.string.cad_ok, Toast.LENGTH_SHORT).show();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(FormCadastro.this, MainActivity.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000);
                        } else if (response.equals("erro")) {
                            Toast.makeText(FormCadastro.this, R.string.cad_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FormCadastro.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("nome", user.nome);
                        data.put("email", user.email);
                        data.put("senha", user.senha);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        } else {
            Toast.makeText(FormCadastro.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarCampos(){
        edit_nome.setError(null);
        edit_email.setError(null);
        edit_senha.setError(null);
        edit_confSenha.setError(null);

        user = new Usuario();

        user.nome = edit_nome.getText().toString().trim();
        user.email = edit_email.getText().toString().trim();
        user.senha = edit_senha.getText().toString().trim();
        user.confSenha = edit_confSenha.getText().toString().trim();

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