package com.example.javog.sesion.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.javog.sesion.Actividades.LoginActivity;
import com.example.javog.sesion.Actividades.TerminarTrabajo;
import com.example.javog.sesion.Datos.Job;
import com.example.javog.sesion.Listas_Y_Adaptadores.RVAdapter;
import com.example.javog.sesion.Listas_Y_Adaptadores.RecyclerViewClickListener;
import com.example.javog.sesion.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class Trabajos extends Fragment {
    private MobileServiceClient mClient;
    private MobileServiceTable<Job> tabla;
    private ArrayList<Job> items;

    private RecyclerViewClickListener listener;
    private RecyclerView rv;
    private ArrayList<Job> trabajos;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Trabajos() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Trabajos newInstance(String param1, String param2) {
        Trabajos fragment = new Trabajos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAzureClient();
        items = new ArrayList<Job>();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        trabajos = new ArrayList<Job>();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        obtenerItemsWhere();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trabajos, container, false);
    }

    private void initAzureClient(){
        try{
            mClient = new MobileServiceClient("https://aadsdewf.azurewebsites.net", getContext());
            Log.d("initAzureClient",  "cliente de Azure inicializado");
        }catch (MalformedURLException mue){
            Log.d("initClientAzure", mue.getMessage());
        }
        tabla = mClient.getTable(Job.class);
    }

    private void obtenerItemsWhere() {
        SharedPreferences config = getContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, Context.MODE_PRIVATE);
        final String id = config.getString(LoginActivity.LOGIN_ID, null);
        new AsyncTask<Void, Void, Void>(){
            private ArrayList<Job> res = null;
            @Override
            protected Void doInBackground(Void... voids){
                try {
                    items.clear();
                    //encontrado = false;
                    res  = tabla
                            .where()
                            .field("trabajadorID")
                            .eq(id)
                            .and()
                            .field("terminado")
                            .eq(false)
                            .execute()
                            .get();

                    for (Job item : res) {
                        items.add(item);
                    }
                } catch (Exception e) {
                    Log.d("george", e.getMessage());
                }
                getActivity().runOnUiThread(new Runnable() {
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
                initializeAdapter(res);
            }
        }.execute();
    }

    private void initializeAdapter(ArrayList<Job> resultado){
        trabajos = resultado;
        //trabajos.add(new Job("Notificacion 1","Television Rota", "", 0, "", "", "", 0, 0, false, false));

        RVAdapter adapter = new RVAdapter(trabajos,this.getContext(), new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                String jobID = trabajos.get(position).getId();
                Intent act = new Intent(getContext(), TerminarTrabajo.class);
                act.putExtra("jobID",jobID);
                startActivity(act);
            }
        });
        rv.setAdapter(adapter);
    }

}
