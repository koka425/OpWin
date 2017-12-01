package com.example.javog.sesion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class Perfil extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Perfil() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Perfil.
     */
    // TODO: Rename and change types and number of parameters
    public static Perfil newInstance(String param1, String param2) {
        Perfil fragment = new Perfil();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState){
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabLogOut);
        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fabConfigPerfil);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Out", Toast.LENGTH_SHORT).show();
                AskOption(view).show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Lets change your profile", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ConfigurarPerfil.class);
                startActivity(intent);
            }
        });
    }



    private AlertDialog AskOption(final View view)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Cerrar Sesión")
                .setMessage("¿En verdad desea cerrar su sesión?")
                .setIcon(R.drawable.com_facebook_close)

                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        LogOut(view);
                    }

                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void LogOut(View view){
        SharedPreferences config = view.getContext().getSharedPreferences(LoginActivity.SHARED_PREFS_CONS, Context.MODE_PRIVATE);
        if(config.getBoolean(LoginActivity.LOGIN_SAVED, false)){
            SharedPreferences.Editor editor = config.edit();
            editor.putBoolean(LoginActivity.LOGIN_SAVED, false);
            editor.putString(LoginActivity.LOGIN_KEY, null);
            editor.putString(LoginActivity.LOGIN_PASS, null);
            editor.commit();
        }

        SharedPreferences session = view.getContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        editor.putString(LoginActivity.LOGIN_ID, null);
        editor.putString(LoginActivity.LOGIN_EMAIL, null);
        editor.putString(LoginActivity.LOGIN_NAME, null);
        editor.putString(LoginActivity.LOGIN_DESCRIPTION, null);
        editor.putString(LoginActivity.LOGIN_PHONE, null);
        editor.commit();

        Intent login = new Intent(getActivity(), LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
    }
}
