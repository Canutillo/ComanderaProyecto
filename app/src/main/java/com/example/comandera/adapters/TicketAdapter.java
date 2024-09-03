package com.example.comandera.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;
import com.example.comandera.utils.DetalleDocumento;

import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<DetalleDocumento> detalles;

    public TicketAdapter(Context context, List<DetalleDocumento> detalles) {
        this.context = context;
        this.detalles = detalles;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_articulo_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        DetalleDocumento detalle = detalles.get(position);
        holder.tvUd.setText(String.valueOf(detalle.getCantidad()));
        holder.tvDescripcionLarga.setText(detalle.getDescripcionLarga());
        holder.tvPVP.setText(String.valueOf(detalle.getPvp()));
        holder.tvTotal.setText(String.valueOf(detalle.getTotalLinea()));
    }

    @Override
    public int getItemCount() {
        return detalles.size();
    }

    public void updateData(List<DetalleDocumento> newDetalles) {
        this.detalles = newDetalles;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvUd, tvDescripcionLarga, tvPVP, tvTotal;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUd = itemView.findViewById(R.id.tvUd);
            tvDescripcionLarga = itemView.findViewById(R.id.tvDescripcionLarga);
            tvPVP = itemView.findViewById(R.id.tvPVP);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
