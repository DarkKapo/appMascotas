package com.example.appmascotas.datos

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "paseoMascotas")
data class EntidadPaseoMascota(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreMascota: String,
    val nombreCliente: String,
    val duracionPaseo: Double,
    val tarifaHora: Double,
    val montoTotal: Double,
    val estaPagado: Boolean = false,
    val fechaPaseo: Date,
    val fechaPago: Date,
    val comentario: String,
    val fechaCreacion: Date, //Fecha de creacion del paseo
    val fechaEdicion: Date //guarda la fecha del último cambio registrado
)