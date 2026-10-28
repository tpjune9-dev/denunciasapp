package com.ticliahuanca.denunciasapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ticliahuanca.denunciasapp.adapter.DenunciaAdapter;
import com.ticliahuanca.denunciasapp.model.Denuncia;
import com.ticliahuanca.denunciasapp.network.DenunciaApi;
import com.ticliahuanca.denunciasapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDenuncias;
    private SwipeRefreshLayout swipeRefresh;
    private Button btnRegistrar, btnBuscarDni, btnCargarMas;
    private DenunciaAdapter adapter;
    private DenunciaApi denunciaApi;

    private int offset = 0;
    private final int limit = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias de vistas
        rvDenuncias = findViewById(R.id.rvDenuncias);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnBuscarDni = findViewById(R.id.btnBuscarDni);
        btnCargarMas = findViewById(R.id.btnCargarMas);

        // Configurar RecyclerView
        adapter = new DenunciaAdapter(this);
        rvDenuncias.setLayoutManager(new LinearLayoutManager(this));
        rvDenuncias.setAdapter(adapter);

        // Instancia de Retrofit API
        denunciaApi = RetrofitClient.getDenunciaApi();

        // Cargar primeros registros
        cargarDenuncias();

        // Listener para deslizar hacia abajo y recargar
        swipeRefresh.setOnRefreshListener(this::recargarDenuncias);

        // Listener botón "Cargar siguientes 5"
        btnCargarMas.setOnClickListener(v -> cargarDenuncias());

        // Eventos para botones superiores
        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
            startActivity(intent);
        });

        btnBuscarDni.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BuscarActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Al regresar a la pantalla principal, recargar para reflejar cambios (nuevas denuncias o cambios de estado)
        recargarDenuncias();
    }

    private void recargarDenuncias() {
        offset = 0;
        swipeRefresh.setRefreshing(true);
        btnCargarMas.setEnabled(false);

        Call<List<Denuncia>> call = denunciaApi.obtenerDenuncias(0, limit);
        call.enqueue(new Callback<List<Denuncia>>() {
            @Override
            public void onResponse(Call<List<Denuncia>> call, Response<List<Denuncia>> response) {
                swipeRefresh.setRefreshing(false);
                btnCargarMas.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    List<Denuncia> listaActualizada = response.body();
                    adapter.setDenuncias(listaActualizada);
                    offset = limit;
                } else {
                    Toast.makeText(MainActivity.this, "Error al actualizar lista: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Denuncia>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                btnCargarMas.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDenuncias() {
        btnCargarMas.setEnabled(false);

        Call<List<Denuncia>> call = denunciaApi.obtenerDenuncias(offset, limit);
        call.enqueue(new Callback<List<Denuncia>>() {
            @Override
            public void onResponse(Call<List<Denuncia>> call, Response<List<Denuncia>> response) {
                btnCargarMas.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    List<Denuncia> listaNuevas = response.body();
                    if (!listaNuevas.isEmpty()) {
                        if (offset == 0) {
                            adapter.setDenuncias(listaNuevas);
                        } else {
                            adapter.agregarDenuncias(listaNuevas);
                        }
                        offset += limit;
                    } else {
                        Toast.makeText(MainActivity.this, "No hay más denuncias para cargar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener denuncias: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Denuncia>> call, Throwable t) {
                btnCargarMas.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
