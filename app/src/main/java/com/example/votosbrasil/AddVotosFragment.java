package com.example.votosbrasil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddVotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVotosFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String[] estados, cidades;
    private Spinner sp_estado, sp_cidade;

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
    // TODO: Rename and change types and number of parameters
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
        getInfos();
        setDropDows(sp_estado);

    }

    private void setDropDows(Spinner spinner){

    }

    private void getInfos(){
        estados = new String[]{"Rio Grande do Sul", "Santa Catarina", "Rio de Janeiro"};

    }

    private void IniciarComponentes(View v){
        sp_estado = v.findViewById(R.id.sp_estado);
        sp_cidade = v.findViewById(R.id.sp_cidade);
    }
}