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
import com.example.votosbrasil.databinding.ActivityCadMunicipioBinding;
import com.example.votosbrasil.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CadMunicipioActivity extends AppCompatActivity {
    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";
    private ActivityCadMunicipioBinding binding;
    private Toolbar app_bar;

    private Button bt_salvar;
    private EditText edit_municipio;
    private Spinner sp_estado;
    private String municipio;

    private List<String> estados;
    private int idEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadMunicipioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        binding.mainMunicAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.bt_exit:
                    sair();
                    break;
            }
            return true;
        });

        estados = new ArrayList<String>();
        getEstados();
        setDropDowns(sp_estado, estados);

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarMunicipio();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    private void cadastrarMunicipio(){
        if(NetworkUtils.isConnected()){
            if(validarCampos()) {
                String url = HOST + "/cadastroMunicipio.php";

                JSONObject data = new JSONObject();
                try {
                    data.put("municipio", municipio);
                    data.put("estado", idEstado);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println(response);
                        try {
                            if (response.getString("CADASTRO").equals("OK")) {
                                Toast.makeText(CadMunicipioActivity.this, "Cadastro efetuado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CadMunicipioActivity.this, "Cadastro falhou", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CadMunicipioActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        } else {
            Toast.makeText(CadMunicipioActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void setDropDowns(Spinner spinner, List<String> strings){
        ArrayAdapter<String> adapter = new ArrayAdapter(CadMunicipioActivity.this, android.R.layout.simple_list_item_1, strings);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idEstado = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getEstados(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaEstados.php";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            estados.clear();
                            estados.add("Todos");
                            JSONArray lista = response.getJSONArray("ESTADOS");
                            for (int i=0; i<lista.length(); i++){
                                estados.add(lista.getString(i));
                            }
                        } else {
                            Toast.makeText(CadMunicipioActivity.this, "Busca dos Estados Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CadMunicipioActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(CadMunicipioActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void sair(){
        Intent intent = new Intent(CadMunicipioActivity.this, FormLogin.class);
        finish();
        startActivity(intent);
    }

    private boolean validarCampos(){
        edit_municipio.setError(null);

        municipio = edit_municipio.getText().toString();

        boolean ok = true;

        if(municipio.isEmpty()){
            edit_municipio.setError(getString(R.string.empty_field));
            edit_municipio.requestFocus();
            ok = false;
        }

        return ok;
    }

    protected void iniciarComponentes(){
        app_bar = findViewById(R.id.main_munic_app_bar);
        setSupportActionBar(app_bar);

        bt_salvar = findViewById(R.id.bt_salvar);
        edit_municipio = findViewById(R.id.edit_municipio);
        sp_estado = findViewById(R.id.sp_estado);
    }
}