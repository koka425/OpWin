package com.example.javog.sesion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class AddJobs extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PLACE_PICKER_REQUEST = 1;

    // TODO 2. Definir Varialbes
    private GoogleApiClient googleApiClient;
    private Location location;

    private boolean hasLocation = false;

    private double latitud;
    private double longitud;

    private final int REQUEST_LOCATION = 1;

    private Button bLocal;
    private Button bPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jobs);

        bLocal = (Button) findViewById(R.id.bMiLoc);
        bPlace = (Button) findViewById(R.id.bPlace);

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

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
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
