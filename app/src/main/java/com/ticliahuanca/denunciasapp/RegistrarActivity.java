package com.ticliahuanca.denunciasapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ticliahuanca.denunciasapp.model.Denuncia;
import com.ticliahuanca.denunciasapp.network.DenunciaApi;
import com.ticliahuanca.denunciasapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarActivity extends AppCompatActivity {

    private EditText etDni, etTipo, etDireccion, etDescripcion;
    private Button btnGuardar;
    private DenunciaApi denunciaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        etDni = findViewById(R.id.etDni);
        etTipo = findViewById(R.id.etTipo);
        etDireccion = findViewById(R.id.etDireccion);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);

        denunciaApi = RetrofitClient.getDenunciaApi();

        btnGuardar.setOnClickListener(v -> registrarDenuncia());
    }

    private void registrarDenuncia() {
        String dni = etDni.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (dni.isEmpty() || tipo.isEmpty() || direccion.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.length() != 8) {
            Toast.makeText(this, "El DNI debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);

        // Se envía únicamente el objeto con los 4 campos.
        // id, fecha y estado se mantienen como null para que Gson los omita del payload JSON.
        Denuncia nuevaDenuncia = new Denuncia(dni, tipo, direccion, descripcion);

        Call<Denuncia> call = denunciaApi.crearDenuncia(nuevaDenuncia);
        call.enqueue(new Callback<Denuncia>() {
            @Override
            public void onResponse(Call<Denuncia> call, Response<Denuncia> response) {
                btnGuardar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegistrarActivity.this, "Denuncia registrada con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorDetalle = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errStr = response.errorBody().string();
                            Log.e("REGISTRAR_ERROR", "Error body: " + errStr);
                            if (!errStr.isEmpty()) {
                                errorDetalle += ": " + errStr;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("REGISTRAR_ERROR", "Exception reading error body", e);
                    }
                    Toast.makeText(RegistrarActivity.this, errorDetalle, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Denuncia> call, Throwable t) {
                btnGuardar.setEnabled(true);
                Toast.makeText(RegistrarActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
