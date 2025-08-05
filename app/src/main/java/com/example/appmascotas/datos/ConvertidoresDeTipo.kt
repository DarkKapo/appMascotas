package com.example.appmascotas.datos

import androidx.room.TypeConverter
import java.util.Date

//Room no puede guardar directamente un dato Date, pero sí un Long
class ConvertidoresDeTipo {
    //cambia fecha de 1722796200000 a Tue Aug 05 14:30:00 GMT-04:00 2025
    @TypeConverter
    fun TimestampAFecha(valor: Long?): Date? {
        return valor?.let { Date(it) }
    }
    //el inverso a la funcion anterior
    @TypeConverter
    fun FechaATimestamp(date: Date?): Long? {
        return date?.time
    }
}