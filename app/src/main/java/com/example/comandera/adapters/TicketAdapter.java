package com.example.comandera.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<String> ticketLines;

    public TicketAdapter(Context context, List<String> ticketLines) {
        this.context = context;
        this.ticketLines = ticketLines;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_articulo_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        String ticketLine = ticketLines.get(position);
        holder.tvDescripcionLarga.setText(ticketLine);
    }

    @Override
    public int getItemCount() {
        return ticketLines.size();
    }

    public void updateData(List<String> descripcionesLargas) {
        this.ticketLines.clear();
        this.ticketLines.addAll(descripcionesLargas);
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcionLarga;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcionLarga = itemView.findViewById(R.id.tvDescripcionLarga);
        }
    }
}
