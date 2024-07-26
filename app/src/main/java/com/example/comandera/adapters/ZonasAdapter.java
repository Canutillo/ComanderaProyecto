package com.example.comandera.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        void onItemClick(int zonaId);
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
        ZonaVenta zona = zonas.get(position);
        holder.bind(zona, listener);
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

        public void bind(final ZonaVenta zonaVenta, final OnItemClickListener listener) {
            textViewZona.setText(zonaVenta.getZona());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(zonaVenta.getId());
                }
            });
        }
    }
}
