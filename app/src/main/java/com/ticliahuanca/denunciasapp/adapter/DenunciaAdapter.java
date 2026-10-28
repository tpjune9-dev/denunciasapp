package com.ticliahuanca.denunciasapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ticliahuanca.denunciasapp.DetalleActivity;
import com.ticliahuanca.denunciasapp.R;
import com.ticliahuanca.denunciasapp.model.Denuncia;

import java.util.ArrayList;
import java.util.List;

public class DenunciaAdapter extends RecyclerView.Adapter<DenunciaAdapter.DenunciaViewHolder> {

    private List<Denuncia> denuncias = new ArrayList<>();
    private final Context context;

    public DenunciaAdapter(Context context) {
        this.context = context;
    }

    public void setDenuncias(List<Denuncia> nuevasDenuncias) {
        this.denuncias = nuevasDenuncias;
        notifyDataSetChanged();
    }

    public void agregarDenuncias(List<Denuncia> masDenuncias) {
        int posicionInicial = this.denuncias.size();
        this.denuncias.addAll(masDenuncias);
        notifyItemRangeInserted(posicionInicial, masDenuncias.size());
    }

    @Override
    public DenunciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_denuncia, parent, false);
        return new DenunciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DenunciaViewHolder holder, int position) {
        Denuncia denuncia = denuncias.get(position);

        holder.tvTipo.setText(denuncia.getTipo() != null ? denuncia.getTipo() : "Sin Tipo");
        holder.tvDireccion.setText("📍 " + (denuncia.getDireccion() != null ? denuncia.getDireccion() : "N/A"));
        holder.tvDni.setText("🪪 DNI Ciudadano: " + (denuncia.getDni() != null ? denuncia.getDni() : "N/A"));

        if (denuncia.getEstado() != null && denuncia.getEstado() == 0) {
            holder.tvEstado.setText("ATENDIDA");
            holder.tvEstado.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_status_attended));
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.status_attended_text));
        } else {
            holder.tvEstado.setText("PENDIENTE");
            holder.tvEstado.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_status_pending));
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
        }

        holder.itemView.setOnClickListener(v -> {
            if (denuncia.getId() != null) {
                Intent intent = new Intent(context, DetalleActivity.class);
                intent.putExtra("id", denuncia.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return denuncias.size();
    }

    public static class DenunciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvEstado, tvDireccion, tvDni;

        public DenunciaViewHolder(View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvDni = itemView.findViewById(R.id.tvDni);
        }
    }
}
