package com.example.votosbrasil;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int id;
    private String nome;

    private String HOST = "https://votosbrasil.000webhostapp.com/trab_final";
    private TextView text_hello_user;
    private BarChart grafico1, grafico2;
    private List<String> labels1, labels2;

    private ArrayList barArrayList1, barArrayList2;

    public InicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
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
            id = getArguments().getInt("id");
            nome = getArguments().getString("nome");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        IniciarComponentes(view);
        String hello = "Olá, " + nome;
        text_hello_user.setText(hello);

        buscarVotos(0, labels1, barArrayList1, grafico1);
        buscarVotos(1, labels2, barArrayList2, grafico2);
    }

    private void gerarGraficos(BarChart barChart, ArrayList barArrayList, List<String> labels){
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Teste Gráfico");
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
        barChart.requestLayout();
    }

    private void buscarVotos(int turno, List<String> labels, ArrayList barArrayList, BarChart grafico){
        if(NetworkUtils.isConnected()){
            String url = HOST + "/buscaVotos.php";

            JSONObject data = new JSONObject();
            try {
                data.put("turno", turno);
                data.put("estado", 0);
                data.put("cidade", "Todos municípios");
                data.put("zona", "Todas zonas");
                data.put("secao", "Todas secoes");
            } catch (JSONException e){
                e.printStackTrace();
            }
            System.out.println(data);

            if(grafico.getData() != null){
                grafico.getData().clearValues();
            }

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
                            gerarGraficos(grafico, barArrayList, labels);
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

    private void IniciarComponentes(View v){
        grafico1 = (BarChart) v.findViewById(R.id.barchart1);
        grafico2 = (BarChart) v.findViewById(R.id.barchart2);
        text_hello_user = v.findViewById(R.id.hello_user);

        barArrayList1 = new ArrayList();
        barArrayList2 = new ArrayList();
        labels1 = new ArrayList<String>();
        labels2 = new ArrayList<String>();
    }
}