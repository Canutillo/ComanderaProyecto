package com.example.comandera.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;
import com.example.comandera.utils.ZonaVenta;

import java.util.List;

public class ZonasAdapter extends RecyclerView.Adapter<ZonasAdapter.ZonaViewHolder> {

    private List<ZonaVenta> zonas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ZonasAdapter(List<ZonaVenta> zonas, OnItemClickListener listener) {
        this.zonas = zonas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ZonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zona, parent, false);
        return new ZonaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZonaViewHolder holder, int position) {
        int screenWidth = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;

        // Establecer el ancho del ítem al 25% del ancho de la pantalla
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = (int) (screenWidth * 0.25);  // 25% del ancho de la pantalla
        holder.itemView.setLayoutParams(params);
        ZonaVenta zona = zonas.get(position);
        holder.bind(zona,position, listener);
    }

    @Override
    public int getItemCount() {
        return zonas.size();
    }

    public static class ZonaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewZona;

        public ZonaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewZona = itemView.findViewById(R.id.textViewZona);
        }

        public void bind(final ZonaVenta zonaVenta, final int position, final OnItemClickListener listener) {
            textViewZona.setText(zonaVenta.getZona());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}
