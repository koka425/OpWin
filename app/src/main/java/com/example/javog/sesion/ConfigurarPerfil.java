package com.example.javog.sesion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javog.sesion.Datos.User;
import com.example.javog.sesion.crypto.MessageCrypto;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class ConfigurarPerfil extends AppCompatActivity {
    private EditText etNombre, etCorreo, etCel, etDes, etPass, etPass2;
    private MobileServiceClient mClient;
    private MobileServiceTable<User> tabla;
    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_perfil);

        settings = getApplicationContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, MODE_PRIVATE);

        etNombre = (EditText) findViewById(R.id.editNombreConfigurar);
        etNombre.setText(settings.getString(LoginActivity.LOGIN_NAME, null));
        etCorreo = (EditText) findViewById(R.id.editCorreoConfigurar);
        etCorreo.setText(settings.getString(LoginActivity.LOGIN_EMAIL, null));
        etPass   = (EditText) findViewById(R.id.editContraConfigurar);
        etPass.setText(settings.getString(LoginActivity.LOGIN_PASSWORD, null));
        etCel    = (EditText)findViewById(R.id.editTelefonoConfigurar);
        etCel.setText(settings.getString(LoginActivity.LOGIN_PHONE, null));
        etDes    = (EditText)findViewById(R.id.editDescripcionConfigurar);
        etDes.setText(settings.getString(LoginActivity.LOGIN_DESCRIPTION, null));

        Button btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        Button btnGaleria = (Button) findViewById(R.id.btnGaleria);
        Button btnModificar = (Button) findViewById(R.id.btnModify);
        Button btnCancelar  = (Button) findViewById(R.id.btnCancelarChange);

        initAzureClient();

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TestConection(ConfigurarPerfil.this)) {
                    String nombre = etNombre.getText().toString();
                    String correo = etCorreo.getText().toString();
                    String password = etPass.getText().toString();
                    String cel = etCel.getText().toString();
                    String desc = etDes.getText().toString();

                    if (nombre.equals("") || correo.equals("") || password.equals("") || cel.equals("") || desc.equals("")) {
                        Toast.makeText(ConfigurarPerfil.this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        int c = Integer.parseInt(cel);
                        String passCrypto = new MessageCrypto().GenerateHash(password, MessageCrypto.HASH_SHA256);
                        actualizarItem(correo, passCrypto, nombre, desc, c);
                    }
                } else {
                    Toast.makeText(ConfigurarPerfil.this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigurarPerfil.this, MainActivity.class);
                startActivity(intent);
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
        tabla = mClient.getTable(User.class);
    }

    public void actualizarItem(String email, String password, String name, String description, int phone){
        final User item = new User(email, password, name, description, phone);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    SharedPreferences config = getApplicationContext().getSharedPreferences(LoginActivity.SHARED_PREFS_CONS, MODE_PRIVATE);
                    String id = config.getString(LoginActivity.LOGIN_ID, null);
                    item.setId(id);
                    tabla.update(item).get();
                    //Toast.makeText(ConfigurarPerfil.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfigurarPerfil.this, MainActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                } catch (Exception e) {
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
        }.execute();
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
}
