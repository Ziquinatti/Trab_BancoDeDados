package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NetworkUtils;
import com.example.votosbrasil.databinding.ActivityAnaliseBinding;
import com.example.votosbrasil.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnaliseActivity extends AppCompatActivity {
    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";
    private ActivityAnaliseBinding binding;
    private Toolbar app_bar;

    private ListView list_votos;
    private ArrayAdapter<String> adapter;
    private List<String> votos;
    private List<Integer> idTmpVotos;
    private View background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnaliseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        votos = new ArrayList<String>();
        idTmpVotos = new ArrayList<Integer>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, votos);
        getTmpVotos();

        binding.mainAnaliseAppBar.setOnMenuItemClickListener(item -> {
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

    private void getTmpVotos(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaTmpVotos.php";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //System.out.println(response);
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            JSONArray lista = response.getJSONArray("VOTOS");
                            for (int i=0; i<lista.length(); i++){
                                idTmpVotos.add(lista.getJSONArray(i).getInt(0));
                                String nome =  lista.getJSONArray(i).getString(1);
                                String ano = lista.getJSONArray(i).getString(2);
                                int turno = lista.getJSONArray(i).getInt(3);
                                String item = "User: "+nome+" | Ano: "+ano+" | "+(turno+1)+"º Turno";
                                adapter.add(item);
                            }
                            ajustaLista();
                        } else {
                            Toast.makeText(AnaliseActivity.this, "Busca dos Estados Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AnaliseActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(AnaliseActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void ajustaLista(){
        list_votos.setAdapter(adapter);

        int totalHeight = 0;
        for(int i=0; i<adapter.getCount(); i++){
            View listItem = adapter.getView(i, null, list_votos);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = list_votos.getLayoutParams();
        int old_height = params.height;
        params.height = totalHeight + (list_votos.getDividerHeight() * (adapter.getCount() - 1));
        list_votos.setLayoutParams(params);
        list_votos.requestLayout();

        ViewGroup.LayoutParams params_back = background.getLayoutParams();
        params_back.height += (params.height - old_height);
        background.setLayoutParams(params_back);
        background.requestLayout();

        list_votos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return (motionEvent.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        list_votos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AnaliseActivity.this, CheckVotosActivity.class);
                intent.putExtra("idTmp_Votos", idTmpVotos.get(i));
                startActivity(intent);
                //System.out.println(idTmpVotos.get(i));
            }
        });
    }

    private void sair(){
        Intent intent = new Intent(AnaliseActivity.this, FormLogin.class);
        finish();
        startActivity(intent);
    }

    private void iniciarComponentes(){
        app_bar = findViewById(R.id.main_analise_app_bar);
        setSupportActionBar(app_bar);
        background = findViewById(R.id.containerListVotos);
        list_votos = (ListView) findViewById(R.id.list_votos);
    }
}