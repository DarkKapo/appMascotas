package com.example.appmascotas.modelovista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmascotas.datos.EntidadPaseoMascota
import com.example.appmascotas.repositorio.RepositorioPaseosMascotas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

//Acá se maneja toda la lógica de la comunicación entre la interfaz con la DB
class ModeloVistaPaseos(private val repositorio: RepositorioPaseosMascotas): ViewModel() {

    //Seccion Variables

    //Variables que se modifican en este archivo
    private val _paseos = MutableStateFlow<List<EntidadPaseoMascota>>(emptyList())
    private val _totalGanado = MutableStateFlow(0.0)
    private val _totalPendiente = MutableStateFlow(0.0)
    private val _nombreMascota = MutableStateFlow("")
    private val _tipoMascota = MutableStateFlow("Perro")
    private val _nombreCliente = MutableStateFlow("")
    private val _duracionPaseo = MutableStateFlow("")
    private val _precioPorHora = MutableStateFlow("")
    private val _comentario = MutableStateFlow("")

    //Variables de solo lectura
    val paseos: StateFlow<List<EntidadPaseoMascota>> = _paseos.asStateFlow()
    val totalGanado: StateFlow<Double> = _totalGanado.asStateFlow()

    val totalPendiente: StateFlow<Double> = _totalPendiente.asStateFlow()

    val nombreMascota: StateFlow<String> = _nombreMascota.asStateFlow()

    val tipoMascota: StateFlow<String> = _tipoMascota.asStateFlow()

    val nombreCliente: StateFlow<String> = _nombreCliente.asStateFlow()

    val duracionPaseo: StateFlow<String> = _duracionPaseo.asStateFlow()

    val precioPorHora: StateFlow<String> = _precioPorHora.asStateFlow()

    val comentario: StateFlow<String> = _comentario.asStateFlow()

    init {
        // Cuando se crea el ViewModel, cargar todos los datos
        obtenerPaseos()
    }

    //Obtener los datos basicos(lista, ganancias y pendiente)
    private fun obtenerPaseos() {
        viewModelScope.launch {
            repositorio.obtenerPaseos().collect { lista ->
                _paseos.value = lista
            }
        }

        viewModelScope.launch {
            repositorio.obtenerTotalGanancias().collect { t ->
                _totalGanado.value = t ?: 0.0
            }
        }

        viewModelScope.launch {
            repositorio.obtenerTotalPendiente().collect { p ->
                _totalPendiente.value = p ?: 0.0
            }
        }
    }

    //Funciones para modificar los datos con signo _
    fun actualizarNombreMascota(nombre: String) {
        _nombreMascota.value = nombre
    }

    fun actualizarTipoMascota(tipo: String) {
        _tipoMascota.value = tipo
    }

    fun actualizarNombreCliente(nombre: String) {
        _nombreCliente.value = nombre
    }

    fun actualizarDuracionPaseo(duracion: String) {
        _duracionPaseo.value = duracion
    }

    fun actualizarPrecioPorHora(precio: String) {
        _precioPorHora.value = precio
    }

    fun actualizarComentario(comentario: String) {
        _comentario.value = comentario
    }

    //Cálculos automáticos (no vienen del usuario)

    fun calcularTotalGanado(): Double {
        val precio = _precioPorHora.value.toDoubleOrNull() ?: 0.0
        val horas = _duracionPaseo.value.toDoubleOrNull() ?: 0.0
        return precio * horas
    }

    fun agregarPaseo() {
        viewModelScope.launch {
            val precio = _precioPorHora.value.toDoubleOrNull() ?: 0.0
            val horas = _duracionPaseo.value.toDoubleOrNull() ?: 0.0
            val total = precio * horas

            val nuevoPaseo = EntidadPaseoMascota(
                nombreMascota =_nombreMascota.value,
                tipoMascota = _tipoMascota.value,
                nombreCliente = _nombreCliente.value,
                duracionPaseo = horas,
                precioHora = precio,
                montoTotal = total,
                estaPagado = false,
                fechaPaseo = Date(),
                fechaPago = null,
                comentario = _comentario.value,
                fechaCreacion = Date(),
                fechaEdicion = null
            )

            repositorio.agregar(nuevoPaseo)
            limpiarFormulario()
        }
    }
    fun eliminar(paseo: EntidadPaseoMascota) {
        viewModelScope.launch {
            repositorio.eliminar(paseo)
        }
    }

    fun actualizarEstaPagado(paseo: EntidadPaseoMascota) {
        viewModelScope.launch {
            val paseoOk = paseo.copy(estaPagado = !paseo.estaPagado)
            repositorio.actualizar(paseoOk)
        }
    }
    private fun limpiarFormulario() {
        _nombreMascota.value = ""
        _tipoMascota.value = ""
        _nombreCliente.value = ""
        _duracionPaseo.value = ""
        _precioPorHora.value = ""
        _comentario.value = ""
    }

    //validaciones simples
    fun validacionFormulario(): Boolean {
        return _nombreMascota.value.isNotBlank() &&
                _nombreCliente.value.isNotBlank() &&
                _duracionPaseo.value.toDoubleOrNull() != null &&
                _precioPorHora.value.toDoubleOrNull() != null
    }
}