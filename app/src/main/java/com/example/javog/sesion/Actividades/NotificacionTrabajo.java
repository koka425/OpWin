package com.example.javog.sesion.Actividades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.javog.sesion.Datos.Job;
import com.example.javog.sesion.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class NotificacionTrabajo extends AppCompatActivity {

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

    private ShareActionProvider shareActionProvider;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_trabajo);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabShare);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "-" + tvTittle.getText().toString() + "\n\n" + tvDescription.getText().toString() + "\n\n" + url;
                        String shareSub = "OpWin - Trabajos Rapidos";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Compartir Usando"));
            }
        });
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
        this.url = url;
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
