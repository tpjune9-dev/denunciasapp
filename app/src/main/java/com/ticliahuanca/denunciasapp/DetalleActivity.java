package com.ticliahuanca.denunciasapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ticliahuanca.denunciasapp.model.Denuncia;
import com.ticliahuanca.denunciasapp.network.DenunciaApi;
import com.ticliahuanca.denunciasapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleActivity extends AppCompatActivity {

    private TextView tvDetalleId, tvDetalleDni, tvDetalleFecha, tvDetalleTipo, tvDetalleDireccion, tvDetalleDescripcion, tvDetalleEstado;
    private Button btnAtender;
    private DenunciaApi denunciaApi;

    private int denunciaId = -1;
    private Denuncia denunciaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        tvDetalleId = findViewById(R.id.tvDetalleId);
        tvDetalleDni = findViewById(R.id.tvDetalleDni);
        tvDetalleFecha = findViewById(R.id.tvDetalleFecha);
        tvDetalleTipo = findViewById(R.id.tvDetalleTipo);
        tvDetalleDireccion = findViewById(R.id.tvDetalleDireccion);
        tvDetalleDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvDetalleEstado = findViewById(R.id.tvDetalleEstado);
        btnAtender = findViewById(R.id.btnAtender);

        denunciaApi = RetrofitClient.getDenunciaApi();

        if (getIntent() != null) {
            if (getIntent().hasExtra("id")) {
                denunciaId = getIntent().getIntExtra("id", -1);
            } else if (getIntent().hasExtra("EXTRA_ID")) {
                denunciaId = getIntent().getIntExtra("EXTRA_ID", -1);
            }
        }

        if (denunciaId != -1) {
            cargarDetalleDenuncia();
        } else {
            Toast.makeText(this, "ID de denuncia no válido", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAtender.setOnClickListener(v -> atenderDenuncia());
    }

    private void cargarDetalleDenuncia() {
        Call<Denuncia> call = denunciaApi.obtenerDenunciaPorId(denunciaId);
        call.enqueue(new Callback<Denuncia>() {
            @Override
            public void onResponse(Call<Denuncia> call, Response<Denuncia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    denunciaActual = response.body();
                    mostrarDatosDenuncia(denunciaActual);
                } else {
                    Toast.makeText(DetalleActivity.this, "Error al obtener detalle: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Denuncia> call, Throwable t) {
                Toast.makeText(DetalleActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatosDenuncia(Denuncia denuncia) {
        tvDetalleId.setText("ID: " + denuncia.getId());
        tvDetalleDni.setText("DNI: " + denuncia.getDni());
        tvDetalleFecha.setText("Fecha: " + denuncia.getFecha());
        tvDetalleTipo.setText("Tipo: " + denuncia.getTipo());
        tvDetalleDireccion.setText("Dirección: " + denuncia.getDireccion());
        tvDetalleDescripcion.setText("Descripción: " + denuncia.getDescripcion());

        if (denuncia.getEstado() != null && denuncia.getEstado() == 0) {
            tvDetalleEstado.setText("Estado: Atendida");
            btnAtender.setEnabled(false);
            btnAtender.setText("Denuncia Atendida");
        } else {
            tvDetalleEstado.setText("Estado: Pendiente");
            btnAtender.setEnabled(true);
        }
    }

    private void atenderDenuncia() {
        if (denunciaActual == null) {
            return;
        }

        btnAtender.setEnabled(false);
        denunciaActual.setEstado(0); // Cambiar estado a 0 (atendida)

        Call<Denuncia> call = denunciaApi.actualizarDenuncia(denunciaId, denunciaActual);
        call.enqueue(new Callback<Denuncia>() {
            @Override
            public void onResponse(Call<Denuncia> call, Response<Denuncia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DetalleActivity.this, "Denuncia atendida con éxito", Toast.LENGTH_SHORT).show();
                    tvDetalleEstado.setText("Estado: Atendida");
                    btnAtender.setEnabled(false);
                    btnAtender.setText("Denuncia Atendida");
                } else {
                    btnAtender.setEnabled(true);
                    Toast.makeText(DetalleActivity.this, "Error al actualizar la denuncia: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Denuncia> call, Throwable t) {
                btnAtender.setEnabled(true);
                Toast.makeText(DetalleActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
