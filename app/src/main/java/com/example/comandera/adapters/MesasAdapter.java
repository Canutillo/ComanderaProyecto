package com.example.comandera.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;
import com.example.comandera.utils.Mesa;

import java.util.List;

public class MesasAdapter extends RecyclerView.Adapter<MesasAdapter.MesaViewHolder> {

    private List<Mesa> mesas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String mesaNombre);
    }
    public MesasAdapter(List<Mesa> mesas, OnItemClickListener listener) {
        this.mesas = mesas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MesaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mesa, parent, false);
        return new MesaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MesaViewHolder holder, int position) {
        Mesa mesa = mesas.get(position);
        holder.bind(mesa, listener);
    }

    @Override
    public int getItemCount() {
        return mesas.size();
    }

    public static class MesaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;

        public MesaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
        }

        public void bind(Mesa mesa, final OnItemClickListener listener) {
            textViewNombre.setText(mesa.getNombre());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listener.onItemClick(mesa.getNombre());
                }
            });
        }
    }
}
