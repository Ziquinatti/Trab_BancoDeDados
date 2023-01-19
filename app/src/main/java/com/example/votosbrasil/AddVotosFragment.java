package com.example.votosbrasil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NetworkUtils;
import com.github.mikephil.charting.charts.BarChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddVotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVotosFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private List<String> turno, estados, cidades, candidatos;
    private Spinner sp_turno, sp_estado, sp_cidade, sp_candidato;

    private final int GALLERY_REQ_CODE = 1000;
    private ImageView img_galeria;
    private Button bt_add_imagem;

    private int id_turno, id_estado = 0, id_cidade;

    //private String HOST = "http://192.168.42.54/trab_final";
    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final/";

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_votos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IniciarComponentes(view);

        turno = new ArrayList<String>();
        turno.add("1º Turno");
        turno.add("2º Turno");
        setDropDowns(sp_turno, turno);

        estados = new ArrayList<String>();
        estados.add("Estados");
        getEstados();
        setDropDowns(sp_estado, estados);

        cidades = new ArrayList<String>();
        cidades.add("Selecione um estado");
        setDropDowns(sp_cidade, cidades);

        candidatos = new ArrayList<String>();
        candidatos.add("Bob");
        candidatos.add("Rose");
        candidatos.add("TestBot");
        candidatos.add("Jorge");
        setDropDowns(sp_candidato, candidatos);

        bt_add_imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGaleria = new Intent(Intent.ACTION_PICK);
                iGaleria.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGaleria, GALLERY_REQ_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQ_CODE){

            img_galeria.setImageURI(data.getData());
        }
    }

    private void setDropDowns(Spinner spinner, List<String> strings){
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, strings);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getId()){
                    case R.id.sp_turno:
                        id_turno = i+1;
                        break;
                    case R.id.sp_estado:
                        if(i == 0){
                            cidades.clear();
                            cidades.add("Selecione um estado");
                            setDropDowns(sp_cidade, cidades);
                        }
                        else if(id_estado != i) {
                            cidades.clear();
                            id_estado = i;
                            getCidades();
                        }
                        break;
                    case R.id.sp_cidade:
                        id_cidade = i;
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

    private void IniciarComponentes(View v){
        sp_turno = (Spinner) v.findViewById(R.id.sp_turno);
        sp_estado = (Spinner) v.findViewById(R.id.sp_estado);
        sp_cidade = (Spinner) v.findViewById(R.id.sp_cidade);

        sp_candidato = (Spinner) v.findViewById(R.id.sp_candidato);

        img_galeria = v.findViewById(R.id.img_galeria);
        bt_add_imagem = v.findViewById(R.id.bt_add_imagem);
    }
}