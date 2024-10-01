package com.example.comandera.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandera.ArticulosActivity;
import com.example.comandera.R;
import com.example.comandera.VariablesGlobales;
import com.example.comandera.utils.Familia;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.Ticket;

import java.util.List;

public class FamiliasAdapter extends RecyclerView.Adapter<FamiliasAdapter.FamiliaViewHolder> {
    private Context context;
    private List<Familia> familias;
    private int zonaId, mesaId, comensales, seccionId;
    private Ticket ticket;
    private FichaPersonal fichaPersonal;
    private VariablesGlobales varGlob;

    public FamiliasAdapter(Context context, List<Familia> familias, int zonaId, FichaPersonal fichaPersonal, int mesaId, Ticket ticket, int comensales, int seccionId, VariablesGlobales varGlob) {
        this.context = context;
        this.familias = familias;
        this.zonaId = zonaId;
        this.fichaPersonal = fichaPersonal;
        this.mesaId = mesaId;
        this.ticket = ticket;
        this.comensales = comensales;
        this.seccionId = seccionId;
        this.varGlob=varGlob;
    }

    @NonNull
    @Override
    public FamiliaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_familia, parent, false);
        return new FamiliaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FamiliaViewHolder holder, int position) {
        int screenWidth = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = holder.itemView.getContext().getResources().getDisplayMetrics().heightPixels;

// Establecer el ancho del ítem al 25% del ancho de la pantalla
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = (int) (screenWidth * 0.22);  // 22% del ancho de la pantalla

// Establecer el alto del ítem al 25% del alto de la pantalla
        params.height = (int) (screenHeight * 0.15);  // 15% del alto de la pantalla

        holder.itemView.setLayoutParams(params);
        Familia familia = familias.get(position);
        holder.familiaNombre.setText(familia.getNombre());

        Glide.with(context)
                .load(familia.getImage())
                .into(holder.familiaImagen);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                varGlob.setFamiliaActual(familia);
                Intent intent = new Intent(context, ArticulosActivity.class);
                context.startActivity(intent);
                varGlob.setGuardarYsalirFamiliasArticulos(false);

            }
        });
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
