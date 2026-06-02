package com.example.biometricos8b.data

import com.google.gson.annotations.SerializedName

data class Nota(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("tituloNota")
    val titulo: String,

    @SerializedName("contenidoNota")
    val contenido: String,

    @SerializedName("timestamp")
    val timestamp: Long? = null
)
