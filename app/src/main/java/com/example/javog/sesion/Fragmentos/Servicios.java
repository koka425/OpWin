package com.example.javog.sesion.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.javog.sesion.Actividades.LoginActivity;
import com.example.javog.sesion.Datos.Job;
import com.example.javog.sesion.Listas_Y_Adaptadores.ListViewAdapter;
import com.example.javog.sesion.Actividades.OfertasTrabajos;
import com.example.javog.sesion.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.ArrayList;


public class Servicios extends Fragment implements SearchView.OnQueryTextListener {
    private MobileServiceClient mClient;
    private MobileServiceTable<Job> tabla;
    private ArrayList<Job> items;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ListView list;
    private ListViewAdapter adapter;
    private SearchView editsearch;
    public static ArrayList<Job> trabajosArrayList = new ArrayList<Job>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Servicios() {
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
    public static Servicios newInstance(String param1, String param2) {
        Servicios fragment = new Servicios();
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

        trabajosArrayList = new ArrayList<>();

        items = new ArrayList<Job>();
        initAzureClient();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        editsearch = (SearchView) view.findViewById(R.id.search);
        list = (ListView) view.findViewById(R.id.listview);

        obtenerItemsWhere(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_servicios, container, false);


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

    private void obtenerItemsWhere(final View view) {
        SharedPreferences config = getContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, Context.MODE_PRIVATE);
        final String id = config.getString(LoginActivity.LOGIN_ID, null);
        new AsyncTask<Void, Void, Void>(){
            private ArrayList<Job> res = null;
            @Override
            protected Void doInBackground(Void... voids){
                try {
                    items.clear();
                    trabajosArrayList.clear();
                    //encontrado = false;
                    res  = tabla
                            .where()
                            .field("userID")
                            .ne(id)
                            .and()
                            .field("aceptado")
                            .eq(false)
                            .execute()
                            .get();

                    for (Job item : res) {
                        //encontrado=true;
                        trabajosArrayList.add(item);
                        //Log.d("Entro", "Aqui");
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
                initializeAdapter(trabajosArrayList);
            }
        }.execute();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        //adapter.notifyDataSetChanged();
        return false;
    }

    private void initializeAdapter(ArrayList<Job> resultado){
        //resultado.add(new Job("Notificacion 1","Television Rota", "", 0, "", "", "", 0, 0, false, false));
        adapter = new ListViewAdapter(getContext(), resultado);
        //Log.d("Size", String.valueOf(resultado.size()));


        // Binds the Adapter to the ListView
        list.setAdapter(adapter);
        editsearch.setOnQueryTextListener(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String jobID = trabajosArrayList.get(position).getId();
                Intent act = new Intent(getContext(), OfertasTrabajos.class);
                act.putExtra("jobID",jobID);
                startActivity(act);
            }
        });
    }
}
