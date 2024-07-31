package com.example.comandera.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;
import com.example.comandera.utils.Familia;

import java.util.List;

public class FamiliasAdapter extends RecyclerView.Adapter<FamiliasAdapter.FamiliaViewHolder> {
    private List<Familia> familias;
    private Context context;

    public FamiliasAdapter(Context context, List<Familia> familias) {
        this.context = context;
        this.familias = familias;
    }

    @NonNull
    @Override
    public FamiliaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_familia, parent, false);
        return new FamiliaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FamiliaViewHolder holder, int position) {
        Familia familia = familias.get(position);
        holder.familiaNombre.setText(familia.getNombre());

        /*
        // Usando Picasso para cargar la imagen desde una URL
        Picasso.get()
                .load(familia.getImagenUrl())
                .placeholder(R.drawable.placeholder) // Imagen por defecto
                .error(R.drawable.error) // Imagen en caso de error
                .into(holder.familiaImagen);
                */
    }

    @Override
    public int getItemCount() {
        return familias.size();
    }

    public static class FamiliaViewHolder extends RecyclerView.ViewHolder {
        ImageView familiaImagen;
        TextView familiaNombre;

        public FamiliaViewHolder(@NonNull View itemView) {
            super(itemView);
            familiaImagen = itemView.findViewById(R.id.familiaImagen);
            familiaNombre = itemView.findViewById(R.id.familiaNombre);
        }
    }
}
