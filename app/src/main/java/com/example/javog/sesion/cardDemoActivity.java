package com.example.javog.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.javog.sesion.Datos.Job;

import java.util.ArrayList;

/**
 * Created by javog on 20/10/2017.
 */

public class cardDemoActivity extends AppCompatActivity{

    private RecyclerViewClickListener listener;
    private RecyclerView rv;
    private ArrayList<Job> notificaciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificaciones = new ArrayList<Job>();

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
                Intent act = new Intent(cardDemoActivity.this, General.class);
                startActivity(act);
            }
        });
        rv.setAdapter(adapter);
    }
}
