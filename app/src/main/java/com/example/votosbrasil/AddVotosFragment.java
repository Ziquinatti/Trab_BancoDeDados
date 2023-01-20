package com.example.votosbrasil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddVotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVotosFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String id_user;

    private List<String> turno, estados, cidades;
    private List<String> candidatos, mData;
    private Spinner sp_turno, sp_estado, sp_cidade;
    private ListView list_candidatos;
    private ListviewAdapter adapter;
    //private final int GALLERY_REQ_CODE = 100;
    //private Uri image;
    private Button bt_add_imagem, bt_add_voto, bt_salvar;
    private View background;
    private EditText edit_zona, edit_secao;

    //Dados
    private int id_turno = 0, id_estado = 0;
    private String cidade, zona, secao;
    private ArrayList<ArrayList<String>> addCandidatos;

    //Imagem
    private ImageView imageView;
    private String img_tag;
    private Bitmap bitmap;

    //private String HOST = "http://192.168.42.54/trab_final";
    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";

    public AddVotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddVotosFragment.
     */
    public static AddVotosFragment newInstance(String param1, String param2) {
        AddVotosFragment fragment = new AddVotosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_user = Integer.toString(getArguments().getInt("id"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_votos, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IniciarComponentes(view);

        //SELCIONAR TURNO
        turno = new ArrayList<String>();
        turno.add("1º Turno");
        turno.add("2º Turno");
        setDropDowns(sp_turno, turno);

        //SELECIONAR ESTADO
        estados = new ArrayList<String>();
        estados.add("Carregando...");
        getEstados();
        setDropDowns(sp_estado, estados);

        //SELECIONAR CIDADE
        cidades = new ArrayList<String>();
        cidades.add("Selecione um estado primeiro");
        setDropDowns(sp_cidade, cidades);

        //LISTA DE CANDIDADOS E VOTOS
        addCandidatos = new ArrayList<ArrayList<String>>();
        addCandidatos.add(new ArrayList<String>(){{
            add(" - ");
            add(" - ");
        }});
        mData = new ArrayList<String>();
        mData.add("Candidato");

        candidatos = new ArrayList<String>();
        candidatos.add("Carregando...");
        getCandidatos();

        adapter = new ListviewAdapter((ArrayList<String>) mData, (ArrayList<String>) candidatos, getActivity(), new OnSpinnerItemSelected() {
            @Override
            public void onItemSelected(int position, String selectedItem, String num_votos) {
                addCandidatos.get(position).set(0, selectedItem);
                addCandidatos.get(position).set(1, num_votos);
                System.out.println(addCandidatos);
            }
        });
        list_candidatos.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getActivity().getPackageName()));
            getActivity().finish();
            startActivity(intent);
            return;
        }

        bt_add_imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        list_candidatos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return (motionEvent.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        bt_add_voto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCandidatos.add(new ArrayList<String>(){{
                    add(" - ");
                    add(" - ");
                }});
                mData.add("Candidato");
                adapter.notifyDataSetChanged();

                int totalHeight = 0;
                for(int i=0; i<adapter.getCount(); i++){
                    View listItem = adapter.getView(i, null, list_candidatos);
                    listItem.measure(0,0);
                    totalHeight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = list_candidatos.getLayoutParams();
                int old_height = params.height;
                params.height = totalHeight + (list_candidatos.getDividerHeight() * (adapter.getCount() - 1));
                list_candidatos.setLayoutParams(params);
                list_candidatos.requestLayout();

                ViewGroup.LayoutParams params_back = background.getLayoutParams();
                params_back.height += (params.height - old_height);
                background.setLayoutParams(params_back);
                background.requestLayout();
            }
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalvarDados();
            }
        });
    }

    //IMAGEM ////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 100 && data != null){
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap){
        img_tag = "ImagemTeste";

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
                            Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("tags", img_tag);
                params.put("id_user", id_user);
                params.put("id_secao", secao);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData(){
                Map<String, DataPart> params = new HashMap();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(volleyMultipartRequest);
    }

    /////////////////////////////////

    private void setDropDowns(Spinner spinner, List<String> strings){
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, strings);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getId()){
                    case R.id.sp_turno:
                        id_turno = i;
                        break;
                    case R.id.sp_estado:
                        if(i == 0){
                            cidades.clear();
                            cidades.add("Selecione um estado primeiro");
                            setDropDowns(sp_cidade, cidades);
                        }
                        else if(id_estado != i) {
                            cidades.clear();
                            id_estado = i;
                            getCidades();
                        }
                        break;
                    case R.id.sp_cidade:
                        cidade = cidades.get(i);
                        break;
                }
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
                            estados.add("Estados");
                            JSONArray lista = response.getJSONArray("ESTADOS");
                            for (int i=0; i<lista.length(); i++){
                                estados.add(lista.getString(i));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Busca dos Estados Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(getActivity(), R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void getCidades(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaCidades.php";

            JSONObject data = new JSONObject();
            try {
                data.put("estado", id_estado);
            } catch (JSONException e){
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            JSONArray lista = response.getJSONArray("CIDADES");
                            for (int i=0; i<lista.length(); i++){
                                cidades.add(lista.getString(i));
                            }
                            setDropDowns(sp_cidade, cidades);
                        } else {
                            Toast.makeText(getActivity(), "Busca dos Municípios Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(getActivity(), R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private void getCandidatos(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaCandidatos.php";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            candidatos.clear();
                            JSONArray lista = response.getJSONArray("CANDIDATOS");
                            for (int i=0; i<lista.length(); i++){
                                candidatos.add(lista.getString(i));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Busca dos Candidatos Falhou", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(getActivity(), R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    //SALVAR VOTOS //////////////////
    private void SalvarDados(){
        if(NetworkUtils.isConnected()){
            if(ValidarDados()){
                String url = HOST + "/salvarVotos.php";

                JSONArray array = new JSONArray();
                for(int i=0; i<addCandidatos.size(); i++){
                    JSONArray tmp = new JSONArray();
                    tmp.put(addCandidatos.get(i).get(0));
                    tmp.put(addCandidatos.get(i).get(1));
                    array.put(tmp);
                }

                JSONObject data = new JSONObject();
                try {
                    data.put("turno", id_turno);
                    data.put("estado", id_estado);
                    data.put("cidade", cidade);
                    data.put("zona", zona);
                    data.put("secao", secao);
                    data.put("candidatos", array);
                    data.put("user", id_user);
                } catch (JSONException e){
                    e.printStackTrace();
                }

                System.out.println(data);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("CADVOTOS").equals("OK")){
                                Toast.makeText(getActivity(), "Votos cadastrados com sucesso", Toast.LENGTH_SHORT).show();
                            } else {
                                System.out.println(response.getString("ERRO"));
                                Toast.makeText(getActivity(), "Erro no cadastro dos votos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
                requestQueue.add(jsonObjectRequest);

                //UPLOAD IMAGE
                uploadBitmap(bitmap);
            }
        } else {
            Toast.makeText(getActivity(), R.string.con_disable, Toast.LENGTH_LONG).show();
        }
    }

    private boolean ValidarDados(){
        edit_secao.setError(null);
        edit_zona.setError(null);

        secao = edit_secao.getText().toString().trim();
        zona = edit_zona.getText().toString().trim();

        boolean ok = true;

        if(secao.isEmpty()){
            edit_secao.setError(getString(R.string.empty_field));
            edit_secao.requestFocus();
            ok = false;
        }

        if(zona.isEmpty()){
            edit_zona.setError(getString(R.string.empty_field));
            edit_zona.requestFocus();
            ok = false;
        }

        return ok;
    }

    private void IniciarComponentes(View v){
        sp_turno = (Spinner) v.findViewById(R.id.sp_turno);
        sp_estado = (Spinner) v.findViewById(R.id.sp_estado);
        sp_cidade = (Spinner) v.findViewById(R.id.sp_cidade);
        edit_zona = v.findViewById(R.id.edit_zona);
        edit_secao = v.findViewById(R.id.edit_secao);

        //sp_candidato = (Spinner) v.findViewById(R.id.sp_candidato);
        list_candidatos = (ListView) v.findViewById(R.id.list_candidato);
        bt_add_voto = v.findViewById(R.id.bt_add_voto);

        imageView = v.findViewById(R.id.img_galeria);
        bt_add_imagem = v.findViewById(R.id.bt_add_imagem);

        background = v.findViewById(R.id.containerForm);

        bt_salvar = v.findViewById(R.id.bt_salvar);
    }
}