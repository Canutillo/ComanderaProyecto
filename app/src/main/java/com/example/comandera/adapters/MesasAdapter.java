package com.example.comandera.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.FamiliasActivity;
import com.example.comandera.MesasActivity;
import com.example.comandera.R;
import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.DetalleDocumento;
import com.example.comandera.utils.Mesa;

import java.util.List;

public class MesasAdapter extends RecyclerView.Adapter<MesasAdapter.MesaViewHolder> {

    private List<Mesa> mesas;
    private OnItemClickListener listener;


    public void updateData(List<Mesa> newMesas) {
        this.mesas = newMesas;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
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
        int screenWidth = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = holder.itemView.getContext().getResources().getDisplayMetrics().heightPixels;

// Establecer el ancho del ítem al 25% del ancho de la pantalla
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = (int) (screenWidth * 0.22);  // 22% del ancho de la pantalla

// Establecer el alto del ítem al 25% del alto de la pantalla
        params.height = (int) (screenHeight * 0.15);  // 15% del alto de la pantalla

        holder.itemView.setLayoutParams(params);
        Mesa mesa = mesas.get(position);
        int valor=mesa.getEstado();
        switch (valor) {
            case 1:
                // Cambiar a verde
                holder.textViewNombre.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#afff80"))); // Verde
                break;
            case 2:
                // Cambiar a rojo
                holder.textViewNombre.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff8080"))); // Rojo
                break;
            case 3:
                // Cambiar a amarillo
                holder.textViewNombre.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fcff80"))); // Amarillo
                break;
            default:
                // Opción por defecto (puedes manejarla si lo deseas)
                break;
        }
        holder.bind(mesa,position, listener);
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

        public void bind(Mesa mesa,final int position, final OnItemClickListener listener) {
            textViewNombre.setText(mesa.getNombre());
            //Manejo click corto
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listener.onItemClick(position);
                }
            });

            // Manejar el clic largo
            itemView.setOnLongClickListener(new View.OnLongClickListener() { // /* cambio */
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(position); // /* cambio */
                    return true; // Indicar que el evento ha sido manejado
                }
            });
        }
    }
}
