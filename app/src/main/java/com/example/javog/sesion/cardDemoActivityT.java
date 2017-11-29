package com.example.javog.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by javog on 20/10/2017.
 */

public class cardDemoActivityT extends AppCompatActivity{

    private RecyclerViewClickListener listener;
    private RecyclerView rv;
    private ArrayList<Notificacion> notificaciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificaciones = new ArrayList<Notificacion>();

        rv=(RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapter();
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(notificaciones,this, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent act = new Intent(cardDemoActivityT.this, Trabajos.class);
                startActivity(act);
            }
        });
        rv.setAdapter(adapter);
    }
}
