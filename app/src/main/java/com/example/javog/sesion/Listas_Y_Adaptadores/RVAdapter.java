package com.example.javog.sesion.Listas_Y_Adaptadores;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.javog.sesion.Datos.Job;
import com.example.javog.sesion.R;

import java.util.ArrayList;

/**
 * Created by T410 on 19/09/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ProductViewHolder>{
    private Context context;
    private RecyclerViewClickListener listener;

    ArrayList<Job> notificacion;

    public RVAdapter(ArrayList<Job> noti, Context context, RecyclerViewClickListener listener){
        this.notificacion = noti;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ProductViewHolder pvh = new ProductViewHolder(v);
        return new RowViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.productName.setText(notificacion.get(position).getTittle());
        holder.productPrice.setText(notificacion.get(position).getDescription());
        if(notificacion.get(position).isAceptado()){
            holder.imageStatus.setImageResource(R.drawable.clock);
        }
        if(notificacion.get(position).isTerminado()){
            holder.imageStatus.setImageResource(R.drawable.tick);
        }
    }

    @Override
    public int getItemCount() {
        return notificacion.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView productName;
        TextView productPrice;
        ImageView imageStatus;

        ProductViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            productName = (TextView)itemView.findViewById(R.id.person_name);
            productPrice = (TextView)itemView.findViewById(R.id.person_age);
            imageStatus = (ImageView)itemView.findViewById(R.id.person_photo);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
