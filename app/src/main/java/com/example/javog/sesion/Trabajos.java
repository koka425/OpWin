package com.example.javog.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.javog.sesion.Datos.Job;

import java.util.ArrayList;

public class Trabajos extends Fragment {

    private RecyclerViewClickListener listener;
    private RecyclerView rv;
    private ArrayList<Job> trabajos;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Trabajos() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Trabajos newInstance(String param1, String param2) {
        Trabajos fragment = new Trabajos();
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

        trabajos = new ArrayList<Job>();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trabajos, container, false);
    }

    private void initializeAdapter(){
        trabajos = new ArrayList<>();
        trabajos.add(new Job("Notificacion 1","Television Rota", "", 0, "", "", "", 0, 0, false, false));

        RVAdapter adapter = new RVAdapter(trabajos,this.getContext(), new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent act = new Intent(getContext(), OfertasTrabajos.class);
                startActivity(act);
            }
        });
        rv.setAdapter(adapter);
    }

}
