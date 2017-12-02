package com.example.javog.sesion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class OfertasTrabajos extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyAj_vKjIn0tA0ZSbLWj7F3V3cmZujgKOFw";

    private double latitud;
    private double longitud;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas_trabajos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);

        latitud = 19.2686425;
        longitud = -99.7070039;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfertasTrabajos.this);
                builder.setMessage("¿Desea aceptar el trabajo?").setCancelable(false);
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(OfertasTrabajos.this, "Trabajo Aceptado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(OfertasTrabajos.this, "awwwwwwwww", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                        dialog.setTitle("Confirm");
                dialog.show();

            }
        });

        getImage(latitud, longitud);
    }

    public void getImage(final double lat, final double lon){
        String url = "https://maps.googleapis.com/maps/api/staticmap?autoscale=1&size=640x480&maptype=roadmap&key="+API_KEY+"&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:1%7C"+lat+","+lon;
        // "https://maps.googleapis.com/maps/api/staticmap?autoscale=1&size=640x480&maptype=roadmap&key=AIzaSyAj_vKjIn0tA0ZSbLWj7F3V3cmZujgKOFw&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:1%7C47.5951518,-122.3316393"
        Picasso.with(this).load(url).error(R.drawable.com_facebook_tooltip_blue_xout).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:0,0"+"?q="+lat+", "+lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }
}
