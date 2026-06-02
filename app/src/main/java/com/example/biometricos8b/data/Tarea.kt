package com.example.biometricos8b.data

import com.google.gson.annotations.SerializedName

data class Tarea(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("tituloTarea")
    val titulo: String,

    @SerializedName("descripcionTarea")
    val descripcion: String,

    @SerializedName("categoriaTarea")
    val categoria: String,

    @SerializedName("prioridadTarea")
    val prioridad: String,

    @SerializedName("completada")
    val completada: Boolean = false,

    @SerializedName("timestamp")
    val timestamp: Long? = null
)
