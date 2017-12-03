package com.example.javog.sesion.Actividades;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javog.sesion.Datos.User;
import com.example.javog.sesion.R;
import com.example.javog.sesion.crypto.MessageCrypto;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

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

    public static final String SHARED_PREFS_SESSION = "OpWin_Session";
    public static final String LOGIN_ID = "OpWin_Id";
    public static final String LOGIN_EMAIL = "OpWin_Email";
    public static final String LOGIN_PASSWORD = "OpWin_Password";
    public static final String LOGIN_NAME = "OpWin_Name";
    public static final String LOGIN_DESCRIPTION = "OpWin_Description";
    public static final String LOGIN_PHONE = "OpWin_Phone";
    public static final String LOGIN_IMAGE = "OpWin_ImageName";
    public static final String LOGIN_FACEBOOKID = "OpWin_FacebookId";

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private String FacebookID;
    private String FBname;
    private String FBemail;



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
                    if (ValidateFields()) {
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

        loginButton = (LoginButton) findViewById(R.id.login_button);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        //setContentView(R.layout.activity_main);


        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("onSuccess", AccessToken.getCurrentAccessToken().toString());
                //Log.d("onSuccess", Profile.getCurrentProfile().toString());
                obtenerDatos();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Usuario cancelo acción.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, "Error al iniciar sesión con Facebook.", Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });

        AutoLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void obtenerDatos() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        //Toast.makeText(LoginActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            String facebookId = object.getString("id");
                            String name = object.getString("name");
                            String email = object.getString("email");
                            Log.d("result", object.toString());
                            LoginManager.getInstance().logOut();
                            //nuevoItem(email, name, facebookId);
                            validarFacebook(facebookId, name, email);
                        } catch (JSONException je){
                            je.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private boolean ValidateFields(){
        return !(mEmailView.getText().toString().isEmpty() || mPasswordView.getText().toString().isEmpty());
    }

    private void doLogin(String Email, String Pwd){
        if (TestConection(LoginActivity.this)) {
            String hash = mc.GenerateHash(Pwd, MessageCrypto.HASH_SHA256);
            obtenerItemsWhere(Email, hash, Pwd);
            /*if (checkBoxSave.isChecked()) {
                SaveCredentials(Email,Pwd);
            }*/
        } else {
            Toast.makeText(LoginActivity.this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void SaveCredentials(String Email, String Pwd){
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_CONS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOGIN_KEY, Email);
        editor.putString(LOGIN_PASS, Pwd);
        editor.putBoolean(LOGIN_SAVED, true);
        editor.commit();
    }

    private void SalvarSession(String Id, String email, String password, String name, String desc, int phone, String imageName){
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_SESSION, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOGIN_ID, Id);
        editor.putString(LOGIN_EMAIL, email);
        editor.putString(LOGIN_PASSWORD, password);
        editor.putString(LOGIN_NAME, name);
        editor.putString(LOGIN_DESCRIPTION, desc);
        editor.putString(LOGIN_PHONE, String.valueOf(phone));
        if (imageName != null) {
            editor.putString(LOGIN_IMAGE,imageName);
        } else {
            editor.putString(LOGIN_IMAGE, "");
        }
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
                    String id="";
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
                        id = item.getId();
                        String name = item.getName();
                        String desc = item.getDecription();
                        int phone = item.getPhone();
                        String imageName = item.getImageName();
                        items.add(item);
                        SalvarSession(id, email, Pwd, name, desc, phone, imageName);
                    }
                    if (encontrado==true){
                        if(bSalvar){
                            SaveCredentials(email, Pwd);
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
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

    private void validarFacebook(String id, String nombre, String email){
        FacebookID = id;
        FBname = nombre;
        FBemail = email;
        obtenerItemsWhereFace(id);
    }

    private void obtenerItemsWhereFace(final String fbid) {

        new AsyncTask<Void, Void, Void>(){
            private String email;
            private String Pwd;
            @Override
            protected Void doInBackground(Void... voids){
                try {
                    String id="";
                    items.clear();
                    encontrado = false;
                    ArrayList<User> res  = tabla
                            .where()
                            .field("facebookid")
                            .eq(fbid)
                            .execute()
                            .get();

                    for (User item : res) {
                        encontrado = true;
                        id = item.getId();
                        String name = item.getName();
                        String desc = item.getDecription();
                        email = item.getEmail();
                        Pwd = item.getPassword();
                        int phone = item.getPhone();
                        String imageName = item.getImageName();
                        items.add(item);
                        SalvarSession(id, email, Pwd, name, desc, phone, imageName);
                    }
                    if (encontrado==true){
                        if(bSalvar){
                            SaveCredentials(email, Pwd);
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
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
                if(!encontrado){
                    //Toast.makeText(LoginActivity.this, "ID no encontrado", Toast.LENGTH_SHORT).show();
                    nuevoItem(FBemail, FBname, FacebookID);
                } else {
                    ImprimirStatus(encontrado);
                }
            }
        }.execute();
    }

    public void nuevoItem(String email, String name, final String facebookId){
        String pass = new MessageCrypto().GenerateHash(getRandomString(12), MessageCrypto.HASH_SHA256);
        final User item = new User(email, name, pass, "¡Creado usando una Cuenta de Facebook!", -1, "fb", facebookId);
        //System.out.println(item.toString());
        new AsyncTask<Void, Void, Void>(){
            boolean created = false;
            @Override
            protected Void doInBackground(Void... voids) {
                User res = null;
                try{
                    res = mClient.getTable(User.class).insert(item).get();
                    created = true;
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

            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(created == true){
                    ImprimirRegistroFB();
                }
                //obtenerItemsWhereFace(facebookId);
            }
        }.execute();


    }

    private void ImprimirRegistroFB(){
        Toast.makeText(LoginActivity.this, "Cuenta creada exitosamente, por favor toque de nuevo.", Toast.LENGTH_SHORT).show();
    }

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}

