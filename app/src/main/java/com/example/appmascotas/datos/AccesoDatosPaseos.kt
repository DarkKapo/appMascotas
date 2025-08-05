package com.example.appmascotas.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccesoDatosPaseos {
    //CRUD
    @Query("SELECT * FROM paseoMascotas ORDER BY fechaCreacion DESC")
    fun obtenerPaseos(): Flow<List<EntidadPaseoMascota>>

    @Insert
    suspend fun agregar(paseoMascota: EntidadPaseoMascota)

    @Update
    suspend fun actualizar(paseoMascota: EntidadPaseoMascota)

    @Delete
    suspend fun eliminar(paseoMascota: EntidadPaseoMascota)

    //Consultas especificas
    @Query("SELECT * FROM paseoMascotas ORDER BY fechaPaseo DESC")
    fun obtenerPorFechaPaseo(): Flow<List<EntidadPaseoMascota>>

    @Query("SELECT * FROM paseoMascotas WHERE estaPagado = 0 ORDER BY fechaCreacion DESC")
    fun obtenerNoPagados(): Flow<List<EntidadPaseoMascota>>

    @Query("SELECT * FROM paseoMascotas WHERE estaPagado = 1 ORDER BY fechaCreacion DESC")
    fun obtenerPagados(): Flow<List<EntidadPaseoMascota>>

    @Query("SELECT SUM(montoTotal) FROM paseoMascotas WHERE estaPagado = 1")
    fun obtenerTotalGanancias(): Flow<Double?>

    @Query("SELECT SUM(montoTotal) FROM paseoMascotas WHERE estaPagado = 0")
    fun obtenerTotalPendiente(): Flow<Double?>
}