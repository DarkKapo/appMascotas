package com.example.appmascotas.repositorio

import com.example.appmascotas.datos.AccesoDatosPaseos
import com.example.appmascotas.datos.EntidadPaseoMascota
import kotlinx.coroutines.flow.Flow

class RepositorioPaseosMascotas(private val accesoDatosPaseos: AccesoDatosPaseos) {

    //CRUD básico
    fun obtenerPaseos(): Flow<List<EntidadPaseoMascota>> {
        return accesoDatosPaseos.obtenerPaseos()
    }

    suspend fun agregar(paseoMascota: EntidadPaseoMascota) {
        accesoDatosPaseos.agregar(paseoMascota)
    }

    suspend fun actualizar(paseoMascota: EntidadPaseoMascota) {
        accesoDatosPaseos.actualizar(paseoMascota)
    }

    suspend fun eliminar(paseoMascota: EntidadPaseoMascota) {
        accesoDatosPaseos.eliminar(paseoMascota)
    }

    //Consultas específicas
    fun obtenerPorFechaPaseo(): Flow<List<EntidadPaseoMascota>> {
        return accesoDatosPaseos.obtenerPorFechaPaseo()
    }

    fun obtenerNoPagados(): Flow<List<EntidadPaseoMascota>> {
        return accesoDatosPaseos.obtenerNoPagados()
    }

    fun obtenerPagados(): Flow<List<EntidadPaseoMascota>> {
        return accesoDatosPaseos.obtenerPagados()
    }

    fun obtenerTotalGanancias(): Flow<Double?> {
        return accesoDatosPaseos.obtenerTotalGanancias()
    }

    fun obtenerTotalPendiente(): Flow<Double?> {
        return accesoDatosPaseos.obtenerTotalPendiente()
    }
}