package com.ticliahuanca.denunciasapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ticliahuanca.denunciasapp.adapter.DenunciaAdapter;
import com.ticliahuanca.denunciasapp.model.Denuncia;
import com.ticliahuanca.denunciasapp.network.DenunciaApi;
import com.ticliahuanca.denunciasapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarActivity extends AppCompatActivity {

    private EditText etBuscarDni;
    private Button btnBuscar;
    private RecyclerView rvBuscarDenuncias;
    private DenunciaAdapter adapter;
    private DenunciaApi denunciaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        etBuscarDni = findViewById(R.id.etBuscarDni);
        btnBuscar = findViewById(R.id.btnBuscar);
        rvBuscarDenuncias = findViewById(R.id.rvBuscarDenuncias);

        adapter = new DenunciaAdapter(this);
        rvBuscarDenuncias.setLayoutManager(new LinearLayoutManager(this));
        rvBuscarDenuncias.setAdapter(adapter);

        denunciaApi = RetrofitClient.getDenunciaApi();

        btnBuscar.setOnClickListener(v -> buscarPorDni());
    }

    private void buscarPorDni() {
        String dni = etBuscarDni.getText().toString().trim();

        if (dni.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.length() != 8) {
            Toast.makeText(this, "El DNI debe contener exactamente 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnBuscar.setEnabled(false);

        Call<List<Denuncia>> call = denunciaApi.obtenerDenunciasPorCiudadano(dni);
        call.enqueue(new Callback<List<Denuncia>>() {
            @Override
            public void onResponse(Call<List<Denuncia>> call, Response<List<Denuncia>> response) {
                btnBuscar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    List<Denuncia> listaResultados = response.body();
                    if (!listaResultados.isEmpty()) {
                        adapter.setDenuncias(listaResultados);
                    } else {
                        adapter.setDenuncias(new ArrayList<>());
                        Toast.makeText(BuscarActivity.this, "No se encontraron denuncias para el DNI " + dni, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BuscarActivity.this, "Error al consultar la API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Denuncia>> call, Throwable t) {
                btnBuscar.setEnabled(true);
                Toast.makeText(BuscarActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
