package com.example.comandera.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.R;
import com.example.comandera.data.PreguntasBD;
import com.example.comandera.utils.Articulo;
import com.example.comandera.utils.PreguntaArticulo;

import java.util.List;

public class ArticulosAdapter extends RecyclerView.Adapter<ArticulosAdapter.ArticuloViewHolder> {

    private Context context;
    private List<Articulo> articulos;
    private OnItemClickListener onItemClickListener;

    // Interfaz para manejar clics
    public interface OnItemClickListener {
        void onItemClick(Articulo articulo);
    }

    public ArticulosAdapter(Context context, List<Articulo> articulos, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.articulos = articulos;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        int screenWidth = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = holder.itemView.getContext().getResources().getDisplayMetrics().heightPixels;

// Establecer el ancho del ítem al 25% del ancho de la pantalla
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = (int) (screenWidth * 0.22);  // 22% del ancho de la pantalla

// Establecer el alto del ítem al 25% del alto de la pantalla
        params.height = (int) (screenHeight * 0.15);  // 15% del alto de la pantalla

        holder.itemView.setLayoutParams(params);

        Articulo articulo = articulos.get(position);
        holder.articuloNombre.setText(articulo.getNombre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(articulo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articulos.size();
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {
        ImageView articuloImagen;
        TextView articuloNombre;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            articuloImagen = itemView.findViewById(R.id.articuloImagen);
            articuloNombre = itemView.findViewById(R.id.articuloNombre);
        }
    }
}
