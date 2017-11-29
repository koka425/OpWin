package com.example.javog.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
/*
    private RecyclerViewClickListener listener;
    private RecyclerView rv;
    private ArrayList<Producto> productos;
    private ProductoCRUD crud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productos = new ArrayList<Producto>();
        crud = new ProductoCRUD(this);
        productos = crud.getProductos();

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fbAdd = (FloatingActionButton) findViewById(R.id.fb_plus);
        FloatingActionButton fbSuper = (FloatingActionButton) findViewById(R.id.fb_car);
        FloatingActionButton fbHistorial = (FloatingActionButton) findViewById(R.id.fb_hist);

        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act = new Intent(MainActivity.this,AddActivity.class);
                startActivity(act);
            }
        });

        fbSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act = new Intent(MainActivity.this,CurrentSuper.class);
                //act.putExtra("operacion", "nuevo");
                startActivity(act);
            }
        });

        fbHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act = new Intent(MainActivity.this,HistorySuper.class);
                //act.putExtra("operacion", "nuevo");
                startActivity(act);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(productos,this, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent act = new Intent(MainActivity.this, Modify.class);
                act.putExtra("indice", productos.get(position).getId());
                act.putExtra("nombre",productos.get(position).getNombre());
                act.putExtra("precio",productos.get(position).getPrecio());
                act.putExtra("url",productos.get(position).getUrl());
                startActivity(act);
                Toast.makeText(MainActivity.this, "Elemento " + position, Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
    }*/
}
