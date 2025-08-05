package com.example.appmascotas.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [EntidadPaseoMascota::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ConvertidoresDeTipo::class)
abstract class BaseDeDatosPaseos : RoomDatabase() {
    abstract fun AccesoDatosPaseos() : AccesoDatosPaseos

    companion object {
        @Volatile
        private var INSTANCIA: BaseDeDatosPaseos? = null

        fun obtenerBD(context: Context): BaseDeDatosPaseos {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDeDatosPaseos::class.java,
                    "base_datos_paseos"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}