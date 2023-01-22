package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NetworkUtils;
import com.example.votosbrasil.databinding.ActivityCadCandidatoBinding;
import com.example.votosbrasil.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CadCandidatoActivity extends AppCompatActivity {
    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";
    private ActivityCadCandidatoBinding binding;
    private Toolbar app_bar;

    private Button bt_salvar;
    private EditText edit_candidato;
    private Spinner sp_partido, sp_cargo;
    private String candidato;

    private List<String> cargos, partidos;
    private int idCargo, idPartido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadCandidatoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        binding.mainCandAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.bt_exit:
                    sair();
                    break;
            }
            return true;
        });

        partidos = new ArrayList<String>();
        cargos = new ArrayList<String>();
        getCargo_Partido();
        setDropDowns(sp_partido, partidos);
        setDropDowns(sp_cargo, cargos);

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarCandidato();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    private void setDropDowns(Spinner spinner, List<String> strings){
        ArrayAdapter<String> adapter = new ArrayAdapter(CadCandidatoActivity.this, android.R.layout.simple_list_item_1, strings);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getId()){
                    case R.id.sp_partido:
                        idPartido = i;
                        break;
                    case R.id.sp_cargo:
                        idCargo = i;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void cadastrarCandidato(){
        if(NetworkUtils.isConnected()){
            if(validarCampos()) {
                String url = HOST + "/cadastroCandidato.php";

                JSONObject data = new JSONObject();
                try {
                    data.put("candidato", candidato);
                    data.put("partido", idPartido);
                    data.put("cargo", idCargo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println(response);
                        try {
                            if (response.getString("CADASTRO").equals("OK")) {
                                Toast.makeText(CadCandidatoActivity.this, "Cadastro efetuado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CadCandidatoActivity.this, "Cadastro falhou", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CadCandidatoActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        } else {
            Toast.makeText(CadCandidatoActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void getCargo_Partido(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaCargoPartido.php";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            JSONArray lista = response.getJSONArray("CARGOS");
                            for (int i=0; i<lista.length(); i++){
                                cargos.add(lista.getString(i));
                            }
                            lista = response.getJSONArray("PARTIDOS");
                            for (int i=0; i<lista.length(); i++){
                                partidos.add(lista.getString(i));
                            }
                        } else {
                            Toast.makeText(CadCandidatoActivity.this, "Busca dos Partidos e Cargos Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CadCandidatoActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(CadCandidatoActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void sair(){
        Intent intent = new Intent(CadCandidatoActivity.this, FormLogin.class);
        finish();
        startActivity(intent);
    }

    private boolean validarCampos(){
        edit_candidato.setError(null);

        candidato = edit_candidato.getText().toString();

        boolean ok = true;

        if(candidato.isEmpty()){
            edit_candidato.setError(getString(R.string.empty_field));
            edit_candidato.requestFocus();
            ok = false;
        }

        return ok;
    }

    protected void iniciarComponentes(){
        app_bar = findViewById(R.id.main_cand_app_bar);
        setSupportActionBar(app_bar);

        bt_salvar = findViewById(R.id.bt_salvar);
        edit_candidato = findViewById(R.id.edit_candidato);
        sp_cargo = findViewById(R.id.sp_cargo);
        sp_partido = findViewById(R.id.sp_partido);
    }
}