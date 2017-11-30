package com.example.javog.sesion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javog.sesion.crypto.MessageCrypto;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        if(Email.equalsIgnoreCase("javo") && Pwd.equals("javo")){
            //String hash = mc.GenerateHash(Pwd, MessageCrypto.HASH_SHA256);

            if (checkBoxSave.isChecked()) {
                SaveCredentials(Email,Pwd);
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(LoginActivity.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
            //Toast.makeText(LoginActivity.this, hash, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
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


}

