package com.example.comandera.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;
import com.example.comandera.utils.FichaPersonal;

import java.util.List;

public class FichaPersonalAdapter extends RecyclerView.Adapter<FichaPersonalAdapter.FichaPersonalViewHolder> {

    private final List<FichaPersonal> fichaPersonalList;
    private final OnItemClickListener onItemClickListener;

    // Interfaz personalizada para manejar clics
    public interface OnItemClickListener {
        void onItemClick(FichaPersonal fichaPersonal);
    }

    // Constructor del adaptador
    public FichaPersonalAdapter(List<FichaPersonal> fichaPersonalList, OnItemClickListener onItemClickListener) {
        this.fichaPersonalList = fichaPersonalList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FichaPersonalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ficha_personal, parent, false);
        return new FichaPersonalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FichaPersonalViewHolder holder, int position) {
        FichaPersonal fichaPersonal = fichaPersonalList.get(position);
        holder.textViewUsuario.setText(fichaPersonal.getUsuarioApp());

        // Configurar el clic en el elemento del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(fichaPersonal);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fichaPersonalList.size();
    }

    static class FichaPersonalViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsuario;

        public FichaPersonalViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsuario = itemView.findViewById(R.id.nombreUsuario);
        }
    }
}
