package com.example.votosbrasil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NetworkUtils;
import com.example.votosbrasil.databinding.ActivityAnaliseBinding;
import com.example.votosbrasil.databinding.ActivityCheckVotosBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckVotosActivity extends AppCompatActivity {
    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";
    private ActivityCheckVotosBinding binding;
    private Toolbar app_bar;

    private TextView text_user, text_ano_turno, text_estado, text_municipio;
    private TextView text_zona, text_secao,text_candidato, text_qtd_votos;
    private ImageView bt_aceitar, bt_negar;

    private int idTmp_Votos, idUsuario, idSecao, idImagem;
    private String img_url;

    private NetworkImageView image_boletim;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckVotosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();
        getData();

        bt_aceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceitar();
            }
        });

        bt_negar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                negar();
            }
        });

        binding.mainCheckAppBar.setOnMenuItemClickListener(item -> {
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

    private void getData(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaInsert.php";

            JSONObject data = new JSONObject();
            try {
                data.put("idVotos", idTmp_Votos);
            } catch (JSONException e){
                e.printStackTrace();
            }
            System.out.println(data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            JSONArray lista = response.getJSONArray("RESULT");
                            text_user.setText("Usuário: "+ lista.getString(0));

                            String ano = lista.getString(1);
                            int turno = Integer.parseInt(lista.getString(2)) + 1;
                            text_ano_turno.setText("Ano: "+ano+" - "+turno+"º Turno");

                            text_estado.setText("Estado: "+lista.getString(3));

                            text_municipio.setText("Município: "+lista.getString(4));

                            text_zona.setText("Zona: "+lista.getString(5));

                            text_secao.setText("Seção: "+lista.getString(6));

                            text_candidato.setText("Candidato: "+lista.getString(7));

                            text_qtd_votos.setText("Usuário: "+lista.getString(8));

                            idUsuario = lista.getInt(9);
                            idSecao = lista.getInt(10);
                            getImagem();
                        } else {
                            Toast.makeText(CheckVotosActivity.this, "Busca dos Dados Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CheckVotosActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(CheckVotosActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void getImagem(){
        if(NetworkUtils.isConnected()){
            JSONObject data = new JSONObject();
            try {
                data.put("idUsuario", idUsuario);
                data.put("idSecao", idSecao);
            } catch (JSONException e){
                e.printStackTrace();
            }
            System.out.println(data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, EndPoints.GET_PICS_URL, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //System.out.println(response);
                    try {
                        if(!response.getBoolean("error")){
                            idImagem = response.getInt("idImagem");
                            img_url = EndPoints.ROOT_IMAGE + response.getString("imagem");
                            loadImage();
                        } else {
                            Toast.makeText(CheckVotosActivity.this, "Busca da Imagem Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CheckVotosActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(CheckVotosActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void loadImage(){
        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();
        imageLoader.get(img_url, ImageLoader.getImageListener(image_boletim, R.drawable.image, android.R.drawable.ic_dialog_alert));
        image_boletim.setImageUrl(img_url, imageLoader);
    }

    private void aceitar(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/validarVotos.php";

            JSONObject data = new JSONObject();
            try {
                data.put("idVotos", idTmp_Votos);
            } catch (JSONException e){
                e.printStackTrace();
            }

            //System.out.println(data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("VALIDADO").equals("OK")){
                            Toast.makeText(CheckVotosActivity.this, "Votos validados", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CheckVotosActivity.this, "Validação Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CheckVotosActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(CheckVotosActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void negar(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/apagarVotos.php";

            JSONObject data = new JSONObject();
            try {
                data.put("idVotos", idTmp_Votos);
            } catch (JSONException e){
                e.printStackTrace();
            }
            //System.out.println(data);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("APAGADO").equals("OK")){
                            Toast.makeText(CheckVotosActivity.this, "Votos apagados", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CheckVotosActivity.this, "Falha ao deletar", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CheckVotosActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(CheckVotosActivity.this, R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void sair(){
        Intent intent = new Intent(CheckVotosActivity.this, FormLogin.class);
        finish();
        startActivity(intent);
    }

    private void iniciarComponentes(){
        app_bar = findViewById(R.id.main_check_app_bar);
        setSupportActionBar(app_bar);

        idTmp_Votos = getIntent().getIntExtra("idTmp_Votos", 0);
        System.out.println(idTmp_Votos);

        text_user = findViewById(R.id.text_user);
        text_ano_turno = findViewById(R.id.text_ano_turno);
        text_estado = findViewById(R.id.text_estado);
        text_municipio = findViewById(R.id.text_municipio);
        text_zona = findViewById(R.id.text_zona);
        text_secao = findViewById(R.id.text_secao);
        text_candidato = findViewById(R.id.text_candidato);
        text_qtd_votos = findViewById(R.id.text_qtd_votos);

        image_boletim = (NetworkImageView) findViewById(R.id.imagem);

        bt_aceitar = findViewById(R.id.bt_aceitar);
        bt_negar = findViewById(R.id.bt_negar);
    }
}