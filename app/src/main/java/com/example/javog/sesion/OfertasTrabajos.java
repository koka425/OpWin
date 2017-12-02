package com.example.javog.sesion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.javog.sesion.Datos.Job;
import com.example.javog.sesion.Datos.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class OfertasTrabajos extends AppCompatActivity {
    private MobileServiceClient mClient;
    private MobileServiceTable<Job> tabla;
    private ArrayList<Job> items;
    private TextView tvTittle;
    private TextView tvDescription;
    private TextView tvTime;
    private TextView tvMoney;
    private TextView tvInfo;
    private static final String API_KEY = "AIzaSyAj_vKjIn0tA0ZSbLWj7F3V3cmZujgKOFw";
    boolean status = false;
    private double latitud;
    private double longitud;
    private String idJob;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas_trabajos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvTittle      = (TextView) findViewById(R.id.tvJobTitle);
        tvDescription = (TextView) findViewById(R.id.tvJobDescription);
        tvTime        = (TextView) findViewById(R.id.tvJobTime);
        tvMoney       = (TextView) findViewById(R.id.tvJobMoney);
        tvInfo        = (TextView) findViewById(R.id.tvJobAditionalInfo);

        items = new ArrayList<Job>();

        Intent intent = getIntent();
        idJob = intent.getStringExtra("jobID");

        imageView = (ImageView) findViewById(R.id.imageView);

        latitud = 19.2686425;
        longitud = -99.7070039;

        initAzureClient();
        obtenerItemsWhere();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfertasTrabajos.this);
                builder.setMessage("¿Desea aceptar el trabajo?").setCancelable(false);
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        actualizarItem();
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

        //getImage(latitud, longitud);
    }

    private void initAzureClient(){
        try{
            mClient = new MobileServiceClient("https://aadsdewf.azurewebsites.net", this);
            Log.d("initAzureClient",  "cliente de Azure inicializado");
        }catch (MalformedURLException mue){
            Log.d("initClientAzure", mue.getMessage());
        }
        tabla = mClient.getTable(Job.class);
    }

    private void obtenerItemsWhere() {
        new AsyncTask<Void, Void, Void>(){
            private ArrayList<Job> res = null;
            @Override
            protected Void doInBackground(Void... voids){
                try {
                    items.clear();
                    //encontrado = false;
                    res  = tabla
                            .where()
                            .field("id")
                            .eq(idJob)
                            .execute()
                            .get();

                    for (Job item : res) {
                        //encontrado=true;
                        items.add(item);
                    }
                } catch (Exception e) {
                    Log.d("george", e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ordenar();
                        //adapter.notifyDataSetChanged();
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                cargarCampos(res);
            }
        }.execute();
    }

    public void actualizarItem(){
                new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    SharedPreferences config = getApplicationContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, MODE_PRIVATE);
                    String id = config.getString(LoginActivity.LOGIN_ID, null);

                    items.get(0).setAceptado(true);
                    items.get(0).setTrabajadorID(id);
                    tabla.update(items.get(0)).get();
                    status = true;

                } catch (InterruptedException e) {
                    status = false;
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                } catch (ExecutionException e) {
                    status = false;
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                } catch (Exception e) {
                    status = false;
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //adapter.notifyItemRemoved(i); adapter.notifyItemRangeChanged(i, items.size());
                    }
                });
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ImprimirStatus(status);
            }
        }.execute();
    }

    private void ImprimirStatus(boolean correcto){
        if (correcto){
            Toast.makeText(OfertasTrabajos.this, "Trabajo Aceptado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OfertasTrabajos.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(OfertasTrabajos.this, "No se pudo aceptar el trabajo", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarCampos(ArrayList<Job> result){
        tvTittle.setText(result.get(0).getTittle());
        tvDescription.setText(result.get(0).getDescription());
        tvTime.setText(result.get(0).getReqTime());
        tvMoney.setText(String.valueOf(result.get(0).getMoney()));
        tvInfo.setText(result.get(0).getAddInfo());

        latitud = result.get(0).getLatitud();
        longitud = result.get(0).getLongitud();

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
