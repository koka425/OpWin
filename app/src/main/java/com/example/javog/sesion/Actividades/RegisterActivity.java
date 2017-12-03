package com.example.javog.sesion.Actividades;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javog.sesion.Datos.User;
import com.example.javog.sesion.R;
import com.example.javog.sesion.crypto.MessageCrypto;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre, etCorreo, etCel, etDes, etPass, etPass2;
    private MobileServiceClient mClient;
    private MobileServiceTable<User> tabla;
    private ArrayList<User> items;
    //private static boolean encontrado=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        items = new ArrayList<User>();

        etNombre = (EditText) findViewById(R.id.editNombre);
        etCorreo = (EditText) findViewById(R.id.editCorreo);
        etPass   = (EditText) findViewById(R.id.editContra);
        etPass2  = (EditText) findViewById(R.id.editContra2);
        etCel    = (EditText)findViewById(R.id.editCel);
        etDes    = (EditText)findViewById(R.id.editDescripcion);

        Button btnRegistrar = (Button) findViewById(R.id.btnRegister);
        Button btnCancelar  = (Button) findViewById(R.id.btnCancelar);

        initAzureClient();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TestConection(RegisterActivity.this)) {
                    String nombre = etNombre.getText().toString();
                    String correo = etCorreo.getText().toString();
                    String password = etPass.getText().toString();
                    String password2 = etPass2.getText().toString();
                    String cel = etCel.getText().toString();
                    String desc = etDes.getText().toString();

                    if (nombre.equals("") || correo.equals("") || password.equals("") || password2.equals("") || cel.equals("") || desc.equals("")) {
                        Toast.makeText(RegisterActivity.this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
                    } else if (!password.equals(password2)) {
                        Toast.makeText(RegisterActivity.this, "Los passwords deben coincidir", Toast.LENGTH_SHORT).show();
                    } else {
                        int c = Integer.parseInt(cel);
                        String passCrypto = new MessageCrypto().GenerateHash(password, MessageCrypto.HASH_SHA256);
                        obtenerItemsWhere(correo, passCrypto, nombre, desc, c);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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

    public void nuevoItem(String email, String password, String name, String description, int phone){
        final User item = new User(email, password, name, description, phone, "");
        //System.out.println(item.toString());
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                User res = null;
                try{
                    res = mClient.getTable(User.class).insert(item).get();
                    Log.d("nuevoItem", "Nuevo elemento añadido");

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
        }.execute();
    }

    private void obtenerItemsWhere(final String email, final String password, final String name, final String desc, final int phone) {
        new AsyncTask<Void, Void, Void>(){
            boolean encontrado = false;
            @Override
            protected Void doInBackground(Void... voids){
                try {
                    items.clear();
                    encontrado = false;
                    ArrayList<User> res  = tabla
                            .where()
                            .field("email")
                            .eq(email)
                            .execute()
                            .get();

                    for (User item : res) {
                        encontrado = true;
                        Log.d("george", item.getEmail());
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
                ChecarExistencia(encontrado, email, password, name, desc, phone);
            }
        }.execute();
    }

    private void ChecarExistencia(boolean correcto, String email, String password, String name, String desc, int phone){
        if (correcto==false){
            nuevoItem(email, password, name, desc, phone);
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(RegisterActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "Ya existe un usuario con ese email", Toast.LENGTH_SHORT).show();
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
}
