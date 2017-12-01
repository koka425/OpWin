package com.example.javog.sesion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javog.sesion.Datos.User;
import com.example.javog.sesion.crypto.MessageCrypto;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{
    private MobileServiceClient mClient;
    private MobileServiceTable<User> tabla;
    private ArrayList<User> items;
    private boolean encontrado=false;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    public static final String SHARED_PREFS_CONS = "OpWin_Conf";

    public static final String LOGIN_KEY = "OpWin_Login";

    public static final String LOGIN_PASS = "OpWin_Pass";

    public static final String LOGIN_SAVED = "OpWin_Save";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private MessageCrypto mc;

    // UI references.

    private EditText mPasswordView, mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox checkBoxSave;

    private boolean bSalvar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Azure
        initAzureClient();
        items = new ArrayList<User>();
        // Set up the login form.
        mc = new MessageCrypto();
        mEmailView = (EditText) findViewById(R.id.userEmail);
        mPasswordView = (EditText) findViewById(R.id.userPassword);
        checkBoxSave = (CheckBox) findViewById(R.id.checkBoxSave);

        Button btnLogin = (Button) findViewById(R.id.btnLog);

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidateFields()){
                    String Email = mEmailView.getText().toString();
                    String Pwd = mPasswordView.getText().toString();
                    bSalvar = checkBoxSave.isChecked();
                    doLogin(Email, Pwd);
                } else {
                    Toast.makeText(LoginActivity.this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        AutoLogin();
    }

    private boolean ValidateFields(){
        return !(mEmailView.getText().toString().isEmpty() || mPasswordView.getText().toString().isEmpty());
    }

    private void doLogin(String Email, String Pwd){
        String hash = mc.GenerateHash(Pwd, MessageCrypto.HASH_SHA256);
        obtenerItemsWhere(Email,hash, Pwd);
            /*if (checkBoxSave.isChecked()) {
                SaveCredentials(Email,Pwd);
            }*/

    }

    private void SaveCredentials(String Email, String Pwd){
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_CONS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOGIN_KEY, Email);
        editor.putString(LOGIN_PASS, Pwd);
        editor.putBoolean(LOGIN_SAVED, true);
        editor.commit();
    }

    private void AutoLogin(){
        SharedPreferences config = getApplicationContext().getSharedPreferences(SHARED_PREFS_CONS, MODE_PRIVATE);
        if(config.getBoolean(LOGIN_SAVED, false)){
            String Email = config.getString(LOGIN_KEY, null);
            String Pwd = config.getString(LOGIN_PASS, null);

            if (Email != null && Pwd != null){
                doLogin(Email, Pwd);
            }
        }
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

    private void obtenerItemsWhere(final String email, final String password, final String Pwd) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids){
                try {
                    items.clear();
                    encontrado = false;
                    ArrayList<User> res  = tabla
                            .where()
                            .field("email")
                            .eq(email)
                            .and()
                            .field("password")
                            .eq(password)
                            .execute()
                            .get();

                    for (User item : res) {
                        encontrado = true;
                        Log.d("george", "encontrado dentro el for: "+ String.valueOf(encontrado));
                        Log.d("george", item.getName());
                        items.add(item);
                    }
                    if (encontrado==true){
                        if(bSalvar){
                            SaveCredentials(email, Pwd);
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        //Toast.makeText(LoginActivity.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(LoginActivity.this, "Username or password are incorrect", Toast.LENGTH_SHORT).show();
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
                ImprimirStatus(encontrado);
            }
        }.execute();
    }

    private void ImprimirStatus(boolean correcto){
        if (correcto){
            Toast.makeText(LoginActivity.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, "Username or password are incorrect", Toast.LENGTH_SHORT).show();
        }
    }
}

