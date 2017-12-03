package com.example.javog.sesion.Listas_Y_Adaptadores;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.javog.sesion.Datos.Job;
import com.example.javog.sesion.R;
import com.example.javog.sesion.Fragmentos.Servicios;

import java.util.ArrayList;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Job> arraylist;

    public ListViewAdapter(Context context, ArrayList<Job> jobs ) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Job>();
        this.arraylist.addAll(jobs);
    }

    public class ViewHolder {
        TextView name;
        TextView description;
        CardView cv;
    }

    @Override
    public int getCount() {
        return Servicios.trabajosArrayList.size();
    }

    @Override
    public Job getItem(int position) {
        return Servicios.trabajosArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.person_name);
            holder.description = (TextView) view.findViewById(R.id.person_age);
            holder.cv = (CardView) view.findViewById(R.id.cv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(Servicios.trabajosArrayList.get(position).getTittle());
        holder.description.setText(Servicios.trabajosArrayList.get(position).getDescription());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Servicios.trabajosArrayList.clear();
        if (charText.length() == 0) {
            Servicios.trabajosArrayList.addAll(arraylist);
        } else {
            for (Job wp : arraylist) {
                if (wp.getTittle().toLowerCase(Locale.getDefault()).contains(charText) || wp.getDescription().toLowerCase(Locale.getDefault()).contains(charText)) {
                    Servicios.trabajosArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}