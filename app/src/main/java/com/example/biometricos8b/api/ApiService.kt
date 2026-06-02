package com.example.biometricos8b.api

import com.example.biometricos8b.data.Nota
import com.example.biometricos8b.data.Tarea
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/crearTarea")
    suspend fun crearTarea(@Body tarea: Tarea): Response<Any>

    @GET("api/obtenerTareas")
    suspend fun obtenerTareas(): Response<List<Tarea>>

    @GET("api/obtenerTarea/{id}")
    suspend fun obtenerTareaPorId(
        @Path("id") id: String
    ): Response<Tarea>

    @PUT("api/actualizarTarea/{id}")
    suspend fun actualizarTarea(
        @Path("id") id: String,
        @Body tarea: Tarea
    ): Response<Any>

    @DELETE("api/eliminarTarea/{id}")
    suspend fun eliminarTarea(
        @Path("id") id: String
    ): Response<Any>

    @POST("api/crearNota")
    suspend fun crearNota(
        @Body nota: Nota
    ): Response<Any>

    @GET("api/obtenerNotas")
    suspend fun obtenerNotas(): Response<List<Nota>>

    @GET("api/obtenerNota/{id}")
    suspend fun obtenerNotaPorId(
        @Path("id") id: String
    ): Response<Nota>

    @PUT("api/actualizarNota/{id}")
    suspend fun actualizarNota(
        @Path("id") id: String,
        @Body nota: Nota
    ): Response<Any>

    @DELETE("api/eliminarNota/{id}")
    suspend fun eliminarNota(
        @Path("id") id: String
    ): Response<Any>
}