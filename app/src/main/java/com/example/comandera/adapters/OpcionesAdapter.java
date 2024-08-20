package com.example.comandera.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;

import java.util.List;

public class OpcionesAdapter extends RecyclerView.Adapter<OpcionesAdapter.OpcionViewHolder> {

    private Context context;
    private List<String> opciones;
    private List<String> opcionesSeleccionadas;

    public OpcionesAdapter(Context context, List<String> opciones, List<String> opcionesSeleccionadas) {
        this.context = context;
        this.opciones = opciones;
        this.opcionesSeleccionadas = opcionesSeleccionadas;
    }

    @NonNull
    @Override
    public OpcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_opcion, parent, false);
        return new OpcionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpcionViewHolder holder, int position) {
        String opcion = opciones.get(position);
        holder.tvOpcion.setText(opcion);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(opcionesSeleccionadas.contains(opcion));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                opcionesSeleccionadas.add(opcion);
            } else {
                opcionesSeleccionadas.remove(opcion);
            }
        });

        // Aquí puedes cargar la imagen usando alguna librería como Glide o Picasso
        // Glide.with(context).load(opcion).into(holder.ivOpcion);

        holder.itemView.setOnClickListener(v -> holder.checkBox.setChecked(!holder.checkBox.isChecked()));
    }

    @Override
    public int getItemCount() {
        return opciones.size();
    }

    public static class OpcionViewHolder extends RecyclerView.ViewHolder {
        TextView tvOpcion;
        CheckBox checkBox;
        ImageView ivOpcion;

        public OpcionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOpcion = itemView.findViewById(R.id.tvOpcion);
            checkBox = itemView.findViewById(R.id.checkBox);
            ivOpcion = itemView.findViewById(R.id.ivOpcion);
        }
    }
}
