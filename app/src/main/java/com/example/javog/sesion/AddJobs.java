package com.example.javog.sesion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javog.sesion.Datos.Job;
import com.example.javog.sesion.Datos.User;
import com.example.javog.sesion.crypto.MessageCrypto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class AddJobs extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private MobileServiceClient mClient;
    private MobileServiceTable<Job> tabla;

    private static final int PLACE_PICKER_REQUEST = 1;

    // TODO 2. Definir Varialbes
    private GoogleApiClient googleApiClient;
    private Location location;

    private boolean hasLocation = false;

    private double latitud;
    private double longitud;

    private final int REQUEST_LOCATION = 1;

    private EditText etTittle;
    private EditText etDescription;
    private EditText etTime;
    private EditText etMoney;
    private EditText etInfo;

    private Button bLocal;
    private Button bPlace;
    private Button bAddJob;
    private Button bCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jobs);

        etTittle      = (EditText) findViewById(R.id.newJobTitle);
        etDescription = (EditText) findViewById(R.id.newJobDescription);
        etTime        = (EditText) findViewById(R.id.newJobTime);
        etMoney       = (EditText) findViewById(R.id.newJobMoney);
        etInfo        = (EditText) findViewById(R.id.newJobAditionalInfo);

        bLocal  = (Button) findViewById(R.id.bMiLoc);
        bPlace  = (Button) findViewById(R.id.bPlace);
        bAddJob = (Button) findViewById(R.id.btnNewJob);
        bCancel = (Button) findViewById(R.id.btnCancelarJob);

        initAzureClient();

        bLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObtenerLocal();
            }
        });

        bPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObtenerPlacePicker();
            }
        });

        bAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TestConection(AddJobs.this)) {
                    String tittle = etTittle.getText().toString();
                    String description = etDescription.getText().toString();
                    String time = etTime.getText().toString();
                    String money = etMoney.getText().toString();
                    String info = etInfo.getText().toString();

                    if (tittle.equals("") || description.equals("") || time.equals("") || money.equals("")) {
                        Toast.makeText(AddJobs.this, "Favor de llenar los campos titulo, descripcion, tiempo y dinero", Toast.LENGTH_SHORT).show();
                    } else if (hasLocation==false){
                        Toast.makeText(AddJobs.this, "Debe registrar la ubicacion antes de proceder", Toast.LENGTH_SHORT).show();
                    } else {
                        int m = Integer.parseInt(money);
                        nuevoItem(tittle, description, time, m, latitud, longitud);
                    }
                } else {
                    Toast.makeText(AddJobs.this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddJobs.this, MainActivity.class);
                startActivity(intent);
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
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

    public void nuevoItem(String tittle, String description, String time, int money, double latitud, double longitud){
        SharedPreferences config = getApplicationContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, MODE_PRIVATE);
        String id = config.getString(LoginActivity.LOGIN_ID, null);

        final Job item = new Job(tittle, description, time, money, "", id, "", latitud, longitud, false, false);
        //System.out.println(item.toString());
        new AsyncTask<Void, Void, Void>(){
            boolean resultado = false;
            @Override
            protected Void doInBackground(Void... voids) {
                Job res = null;
                try{
                    res = mClient.getTable(Job.class).insert(item).get();
                    Log.d("nuevoItem", "Nuevo trabajo añadido");
                    resultado = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // run() sirve para ejectuar
                            // operaciones en la interfaz o elementos gráficos
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("george", "InterruptedException");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ImprimirResultado(resultado);
            }
        }.execute();
    }

    private void ImprimirResultado(boolean correcto){
        if (correcto==true){
            Toast.makeText(AddJobs.this, "Trabajo añadido exitosamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddJobs.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(AddJobs.this, "No se pudo añadir el trabajo", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean TestConection(Context context) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    private void ObtenerLocal(){
        processLocation();
    }

    private void ObtenerPlacePicker(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //processLocation();
    }

    // TODO 4. Mandar a llamar la funcion processLocation()
    private void processLocation(){
        //Se trata de obtener la última ubicación registrada
        getLocation();

        // Si ubicación es diferente de nulo se actualizan los campos para escribir las variables de la ubicacion

        if(location != null){
            updateLocationUI();
        } else {
            Log.d("george", "Errrrrooooooorrr");
            Toast.makeText(AddJobs.this, "Necesita activar su ubicacion", Toast.LENGTH_LONG).show();
        }
    }

    private void getLocation(){
        // Se valida que haya permisos garantizados
        try {
            if(isLocationPermissionGranted()){
                // Si los hay se regresa la ubicacion al objeto Location
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            } else {
                // De otra forma se le piden permisos al usuario
                requestPermission();
            }
        }catch (SecurityException se) {

           se.printStackTrace();
        }
    }

    private boolean isLocationPermissionGranted(){
        // Valida si ya se dio permiso o no
        int permission = ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        // Se regresa el resultado del permiso
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "No quisiste dar acceso a tu ubicacion", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION
            );
        }
    }

    private void updateLocationUI(){
        hasLocation = true;
        latitud = location.getLatitude();
        longitud = location.getLongitude();

        Toast.makeText(this, "Ubicación Registrada Correctamente", Toast.LENGTH_SHORT).show();
        Log.d("coords", String.valueOf(location.getLatitude() + ", " + String.valueOf(location.getLongitude())));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQUEST_LOCATION){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if (location != null) {
                        updateLocationUI();
                    } else {
                        Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
                    }
                } catch (SecurityException se){
                    se.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Permisos no Otorgados", Toast.LENGTH_SHORT).show();
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                hasLocation = true;
                latitud = place.getLatLng().latitude;
                longitud = place.getLatLng().longitude;

                Toast.makeText(this, "Ubicación Registrada Correctamente", Toast.LENGTH_SHORT).show();
                Log.d("coords", String.valueOf(place.getLatLng().latitude + ", " + String.valueOf(place.getLatLng().longitude)));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i){
        // STUB function
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){

    }

    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        googleApiClient.disconnect();
    }
}
