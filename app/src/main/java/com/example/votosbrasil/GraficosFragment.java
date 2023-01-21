package com.example.votosbrasil;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraficosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraficosFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";
    private Spinner sp_estado, sp_cidade, sp_turno, sp_zona, sp_secao;
    private Button bt_buscar;
    private BarChart grafico;

    private List<String> estados, cidades, turnos, zonas, secoes, labels;
    private ArrayList barArrayList;
    private int turno, estado;
    private String cidade = "", zona = "", secao = "";

    public GraficosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraficosFragment.
     */
    public static GraficosFragment newInstance(String param1, String param2) {
        GraficosFragment fragment = new GraficosFragment();
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
        return inflater.inflate(R.layout.fragment_graficos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IniciarComponentes(view);

        //SELCIONAR TURNO
        turnos = new ArrayList<String>();
        turnos.add("1º Turno");
        turnos.add("2º Turno");
        setDropDowns(sp_turno, turnos);

        //SELECIONAR ESTADO
        estados = new ArrayList<String>();
        estados.add("Carregando...");
        getEstados();
        setDropDowns(sp_estado, estados);

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarVotos();
            }
        });
    }

    private void setDropDowns(Spinner spinner, List<String> strings){
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, strings);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getId()){
                    case R.id.sp_turno:
                        turno = i;
                        break;
                    case R.id.sp_estado:
                        cidades.clear();
                        cidades.add("Todos municípios");
                        estado = i;
                        if(i == 0){
                            setDropDowns(sp_cidade, cidades);
                        } else {
                            getCidades();
                        }
                        break;
                    case R.id.sp_cidade:
                        zonas.clear();
                        zonas.add("Todas zonas");
                        cidade = cidades.get(i);
                        if(i == 0){
                            setDropDowns(sp_cidade, cidades);
                        } else {
                            getZonas();
                        }
                        break;
                    case R.id.sp_zona:
                        secoes.clear();
                        secoes.add("Todas secoes");
                        zona = zonas.get(i);
                        if(i == 0){
                            setDropDowns(sp_secao, secoes);
                        } else {
                            getSecoes();
                        }
                        break;
                    case R.id.sp_secao:
                        secao = secoes.get(i);
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
                            estados.add("Todos");
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
                data.put("estado", estado);
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

    private void getZonas(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaZonas.php";

            JSONObject data = new JSONObject();
            try {
                data.put("estado", estado);
                data.put("cidade", cidade);
            } catch (JSONException e){
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            JSONArray lista = response.getJSONArray("ZONAS");
                            for (int i=0; i<lista.length(); i++){
                                zonas.add(lista.getString(i));
                            }
                            setDropDowns(sp_zona, zonas);
                        } else {
                            Toast.makeText(getActivity(), "Busca das Zonas Eleitorais falhou", Toast.LENGTH_SHORT).show();
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

    private void getSecoes(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaSecoes.php";

            JSONObject data = new JSONObject();
            try {
                data.put("estado", estado);
                data.put("cidade", cidade);
                data.put("zona", zona);
            } catch (JSONException e){
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            JSONArray lista = response.getJSONArray("SECOES");
                            for (int i=0; i<lista.length(); i++){
                                secoes.add(lista.getString(i));
                            }
                            setDropDowns(sp_secao, secoes);
                        } else {
                            Toast.makeText(getActivity(), "Busca das Seções falhou", Toast.LENGTH_SHORT).show();
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

    private void buscarVotos(){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaVotos.php";

            JSONObject data = new JSONObject();
            try {
                data.put("turno", turno);
                data.put("estado", estado);
                data.put("cidade", cidade);
                data.put("zona", zona);
                data.put("secao", secao);
            } catch (JSONException e){
                e.printStackTrace();
            }
            //System.out.println(data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    try {
                        if(response.getString("BUSCA").equals("OK")){
                            JSONArray lista = response.getJSONArray("VOTOS");
                            for (int i=0; i<lista.length(); i++){
                                //System.out.println(lista.getJSONArray(0).get(0));
                                //System.out.println(lista.getJSONArray(0).get(1));
                                labels.add(lista.getJSONArray(i).get(0).toString());
                                barArrayList.add(new BarEntry(i, (float) lista.getJSONArray(i).getInt(1)));
                            }
                            gerarGraficos(grafico);
                        } else {
                            Toast.makeText(getActivity(), "Busca dos Votos falhou", Toast.LENGTH_SHORT).show();
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

    private void gerarGraficos(BarChart barChart){
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Votos por candidato");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setExtraBottomOffset(4f);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        grafico.requestLayout();
    }

    private void IniciarComponentes(View v){
        sp_estado = (Spinner) v.findViewById(R.id.sp_estado);
        sp_cidade = (Spinner) v.findViewById(R.id.sp_cidade);
        sp_turno = (Spinner) v.findViewById(R.id.sp_turno);
        sp_zona = (Spinner) v.findViewById(R.id.sp_zona);
        sp_secao = (Spinner) v.findViewById(R.id.sp_secao);
        bt_buscar = v.findViewById(R.id.bt_buscar);
        grafico = (BarChart) v.findViewById(R.id.barchart);

        cidades = new ArrayList<String>();
        zonas = new ArrayList<String>();
        secoes = new ArrayList<String>();
        barArrayList = new ArrayList();
        labels = new ArrayList<String>();
    }
}