package com.ticliahuanca.denunciasapp.network;

import com.ticliahuanca.denunciasapp.model.Denuncia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DenunciaApi {

    @POST("denuncias")
    Call<Denuncia> crearDenuncia(@Body Denuncia denuncia);

    @GET("denuncias")
    Call<List<Denuncia>> obtenerDenuncias(@Query("offset") int offset, @Query("limit") int limit);

    @GET("denuncias/{id}")
    Call<Denuncia> obtenerDenunciaPorId(@Path("id") int id);

    @GET("denuncias/ciudadano/{dni}")
    Call<List<Denuncia>> obtenerDenunciasPorCiudadano(@Path("dni") String dni);

    @PUT("denuncias/{id}")
    Call<Denuncia> actualizarDenuncia(@Path("id") int id, @Body Denuncia denuncia);
}
