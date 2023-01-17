package com.example.votosbrasil;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

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

    private TextView text_hello_user;
    private BarChart barChart1, barChart2;

    private ArrayList barArrayList;

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

        gerarGraficos(barChart1);
        gerarGraficos(barChart2);
    }

    private void gerarGraficos(BarChart barChart){
        getData();
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

        final String[] labels = new String[]{"Bob", "Rose", "TestBot", "Roger", "Talita"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
    }

    private void getData(){
        barArrayList = new ArrayList();
        barArrayList.add(new BarEntry(0f, 10));
        barArrayList.add(new BarEntry(1f, 20));
        barArrayList.add(new BarEntry(2f, 30));
        barArrayList.add(new BarEntry(3f, 40));
        barArrayList.add(new BarEntry(4f, 50));
    }

    private void IniciarComponentes(View v){
        barChart1 = (BarChart) v.findViewById(R.id.barchart1);
        barChart2 = (BarChart) v.findViewById(R.id.barchart2);
        text_hello_user = v.findViewById(R.id.hello_user);
    }
}