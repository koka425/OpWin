package com.example.javog.sesion;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javog.sesion.Datos.User;
import com.example.javog.sesion.crypto.MessageCrypto;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre, etCorreo, etCel, etDes, etPass, etPass2;
    private MobileServiceClient mClient;
    private MobileServiceTable<User> tabla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                String nombre    = etNombre.getText().toString();
                String correo    = etCorreo.getText().toString();
                String password  = etPass.getText().toString();
                String password2 = etPass2.getText().toString();
                String cel       = etCel.getText().toString();
                String desc      = etDes.getText().toString();

                if (nombre.equals("")||correo.equals("")||password.equals("")||password2.equals("")||cel.equals("")||desc.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(password2)){
                    Toast.makeText(RegisterActivity.this, "Los passwords deben coincidir", Toast.LENGTH_SHORT).show();
                } else {
                    int c = Integer.parseInt(cel);
                    String passCrypto = new MessageCrypto().GenerateHash(password, MessageCrypto.HASH_SHA256);
                    nuevoItem(correo, passCrypto, nombre, desc, c);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
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
        final User item = new User(email, password, name, description, phone);
        System.out.println(item.toString());
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
}
