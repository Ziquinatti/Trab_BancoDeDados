package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NetworkUtils;
import com.example.votosbrasil.databinding.ActivityCadPartidoBinding;
import com.example.votosbrasil.databinding.ActivityMainBinding;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CadPartidoActivity extends AppCompatActivity {
    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";
    private ActivityCadPartidoBinding binding;
    private Toolbar app_bar;

    private Button bt_salvar;
    private EditText edit_partido;
    private String partido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadPartidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        app_bar = findViewById(R.id.main_part_app_bar);
        setSupportActionBar(app_bar);

        bt_salvar = findViewById(R.id.bt_salvar);
        edit_partido = findViewById(R.id.edit_partido);

        binding.mainPartAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.bt_exit:
                    sair();
                    break;
            }
            return true;
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarPartido();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    private void cadastrarPartido(){
        if(NetworkUtils.isConnected()){
            if(validarCampos()) {
                String url = HOST + "/cadastroPartido.php";

                JSONObject data = new JSONObject();
                try {
                    data.put("partido", partido);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println(response);
                        try {
                            if (response.getString("CADASTRO").equals("OK")) {
                                Toast.makeText(CadPartidoActivity.this, "Cadastro efetuado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CadPartidoActivity.this, "Cadastro falhou", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CadPartidoActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        } else {
            Toast.makeText(CadPartidoActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void sair(){
        Intent intent = new Intent(CadPartidoActivity.this, FormLogin.class);
        finish();
        startActivity(intent);
    }

    private boolean validarCampos(){
        edit_partido.setError(null);

        partido = edit_partido.getText().toString();

        boolean ok = true;

        if(partido.isEmpty()){
            edit_partido.setError(getString(R.string.empty_field));
            edit_partido.requestFocus();
            ok = false;
        }

        return ok;
    }
}